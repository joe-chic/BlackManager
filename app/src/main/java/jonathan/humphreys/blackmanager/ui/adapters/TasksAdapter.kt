package jonathan.humphreys.blackmanager.ui.adapters

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.view.children
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import jonathan.humphreys.blackmanager.R
import jonathan.humphreys.blackmanager.data.database.AppDatabase
import jonathan.humphreys.blackmanager.data.entity.*
import jonathan.humphreys.blackmanager.databinding.ItemTaskBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class TasksAdapter(
    private val scope: CoroutineScope,
    private val currentUserId: Int,
    private val priorityMap: Map<String, Int>,
    private val statusMap:   Map<String, Int>,
    private val allUsers:    List<String>,
    private val allFilters:  List<String>,
    private val onUpdate:    (Tasks) -> Unit,
    private val onDelete:    (Int) -> Unit
) : ListAdapter<TaskDisplay, TasksAdapter.TaskViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TaskViewHolder(
        ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) =
        holder.bind(getItem(position))

    inner class TaskViewHolder(private val b: ItemTaskBinding) : RecyclerView.ViewHolder(b.root) {

        private fun showDateTimePicker(onPicked: (String) -> Unit) {
            val now = Calendar.getInstance()
            DatePickerDialog(
                b.root.context,
                { _, y, m, d ->
                    TimePickerDialog(
                        b.root.context,
                        { _, h, mi ->
                            val cal = Calendar.getInstance().apply { set(y, m, d, h, mi) }
                            onPicked(
                                SimpleDateFormat("yyyy-MM-dd HH:mm:00.000", Locale.getDefault())
                                    .format(cal.time)
                            )
                        },
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        true
                    ).show()
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        fun bind(d: TaskDisplay) = with(b) {
            // --- wire up priority & status dropdowns ---
            val ctx = root.context
            val priorityList = priorityMap.keys.toList()
            val statusList   = statusMap.keys.toList()

            actvPriority.apply {
                threshold = 0
                setAdapter(ArrayAdapter(ctx, R.layout.dropdown_item_white, priorityList))
                setOnFocusChangeListener { _, has -> if (has) showDropDown() }
                setOnClickListener { showDropDown() }
            }
            actvStatus.apply {
                threshold = 0
                setAdapter(ArrayAdapter(ctx, R.layout.dropdown_item_white, statusList))
                setOnFocusChangeListener { _, has -> if (has) showDropDown() }
                setOnClickListener { showDropDown() }
            }

            // Populate fields
            etTitle.setText(d.title)
            etDescription.setText(d.description)
            etStartDate.setText(d.kickOff)
            etDeadline.setText(d.deadline)
            actvPriority.setText(d.priorityName ?: "", false)
            actvStatus  .setText(d.statusName   ?: "", false)
            etCreator.setText(d.creatorName ?: "")

            // Clear old chips
            chipGroupAssignees.removeAllViews()
            chipGroupTags.removeAllViews()

            // Date pickers
            etStartDate.setOnClickListener { showDateTimePicker { etStartDate.setText(it) } }
            etDeadline  .setOnClickListener { showDateTimePicker { etDeadline  .setText(it) } }

            // Load persisted chips
            scope.launch(Dispatchers.IO) {
                val db = AppDatabase.getInstance(root.context)
                val assignedUsers = db.assigneesTasksDao().getAssigneesForTask(d.idTask)
                val assignedTags  = db.taskFilterDao().getFiltersForTask(d.idTask)

                Log.d("TasksAdapter", "Task ${d.idTask} tags (IO): $assignedTags")

                withContext(Dispatchers.Main) {
                    assignedUsers.forEach { user ->
                        addPersistedChip(user.username, chipGroupAssignees) {
                            scope.launch(Dispatchers.IO) {
                                db.assigneesTasksDao().deleteAssigneeFromTask(d.idTask, user.idUser)
                            }
                        }
                    }
                    assignedTags.forEach { filter ->
                        addPersistedChip(filter.filter, chipGroupTags) {
                            scope.launch(Dispatchers.IO) {
                                db.taskFilterDao().deleteTaskFilter(d.idTask, filter.idFilter)
                            }
                        }
                    }
                }
            }

            // Wire new‐chip adders
            wireAdder(actvAssignees, chipGroupAssignees, allUsers) { name ->
                val dao = AppDatabase.getInstance(root.context)
                scope.launch(Dispatchers.IO) {
                    val user = dao.userDao().getByUsername(name)
                        ?: Users(0, name).also { dao.userDao().insert(it) }
                    dao.assigneesTasksDao().insert(
                        AsigneesTasks(
                            idUser = user.idUser,
                            idTask = d.idTask,
                            timestampAssignation =
                            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                                .format(Date())
                        )
                    )
                }
            }
            wireAdder(actvTags, chipGroupTags, allFilters) { tag ->
                val dao = AppDatabase.getInstance(root.context)
                scope.launch(Dispatchers.IO) {
                    val f = dao.filterDao().findByName(tag)
                        ?: Filters(0, tag).also { dao.filterDao().insert(it) }
                    dao.taskFilterDao().insert(TasksFilters(d.idTask, f.idFilter))

                    val after = dao.taskFilterDao().getFiltersForTask(d.idTask)
                    Log.d("TasksAdapterFilterDao", "After insert, task ${d.idTask} filters = ${after.map { it.filter }}")

                    // 3) Switch to Main and re‑add chips
                    withContext(Dispatchers.Main) {
                        // clear & re‑draw all chips
                        chipGroupTags.removeAllViews()
                        after.forEach { ff ->
                            addPersistedChip(ff.filter, chipGroupTags) {
                                scope.launch(Dispatchers.IO) {
                                    dao.taskFilterDao().deleteTaskFilter(d.idTask, ff.idFilter)
                                    // optionally re‑bind again
                                }
                            }
                        }
                    }
                }
            }

            // --- Save & Delete ---
            btnSave.setOnClickListener {
                // 1) Build updated Tasks object and call onUpdate()
                val updated = Tasks(
                    idTask       = d.idTask,
                    idCreator    = currentUserId,
                    title        = etTitle.text.toString().takeIf(String::isNotBlank),
                    description  = etDescription.text.toString().takeIf(String::isNotBlank),
                    kickOff      = etStartDate.text.toString().takeIf(String::isNotBlank),
                    deadline     = etDeadline.text.toString().takeIf(String::isNotBlank),
                    idParentTask = null,
                    idPriority   = priorityMap[actvPriority.text.toString()] ?: d.idPriority,
                    idStatus     = statusMap  [actvStatus  .text.toString()] ?: d.idStatus
                )
                onUpdate(updated)

                // 2) Grab all the chip texts
                val assigneesToSave = chipGroupAssignees.children
                    .filterIsInstance<Chip>()
                    .map { it.text.toString() }
                    .toList()
                val tagsToSave = chipGroupTags.children
                    .filterIsInstance<Chip>()
                    .map { it.text.toString() }
                    .toList()

                Log.d("ASIGNEES",assigneesToSave.toString())
                Log.d("TAGS",tagsToSave.toString())

                // 3) Persist relationships on background thread
                scope.launch(Dispatchers.IO) {
                    val db = AppDatabase.getInstance(itemView.context)

                    // --- ASSIGNEES ---
                    db.assigneesTasksDao().deleteAllForTask(d.idTask)

                    assigneesToSave.forEach { username ->
                        val userId = db.userDao().getByUsername(username)?.idUser ?: run {
                            val newUser = Users(0, username)
                            val id = db.userDao().insert(newUser)
                            id.toInt()
                        }
                        Log.d("ASIGNEES", "userId = $userId")
                        db.assigneesTasksDao().insert(
                            AsigneesTasks(
                                idUser = userId,
                                idTask = d.idTask,
                                timestampAssignation = SimpleDateFormat(
                                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                                    Locale.getDefault()
                                ).format(Date())
                            )
                        )
                    }

                    // --- TAGS / FILTERS ---
                    db.taskFilterDao().deleteAllForTask(d.idTask)
                    tagsToSave.forEach { tag ->
                        val f = db.filterDao().findByName(tag) ?: run {
                            val newFilter = Filters(0, tag)
                            val id = db.filterDao().insert(newFilter) // <- devuelve Long
                            newFilter.copy(idFilter = id.toInt())
                        }
                        db.taskFilterDao().insert(TasksFilters(d.idTask, f.idFilter))

                    }

                    // 4) Switch to Main and re‑draw this row
                    withContext(Dispatchers.Main) {
                        val pos = adapterPosition
                        if (pos != RecyclerView.NO_POSITION) {
                            this@TasksAdapter.notifyItemChanged(pos)
                        }
                    }
                }
            }

            btnDelete.setOnClickListener {
                onDelete(d.idTask)
            }

        }

        private fun addPersistedChip(
            text: String,
            group: ChipGroup,
            onClose: () -> Unit
        ) = Chip(itemView.context).apply {
            this.text = text
            isCloseIconVisible = true
            setOnCloseIconClickListener {
                group.removeView(this)
                onClose()
            }
            group.addView(this)
        }

        private fun wireAdder(
            actv: AutoCompleteTextView,
            group: ChipGroup,
            allItems: List<String>,
            insertFn: suspend (String) -> Unit
        ) {
            actv.threshold = 0
            actv.setAdapter(ArrayAdapter(itemView.context, R.layout.dropdown_item_white, allItems))
            actv.setOnClickListener { actv.showDropDown() }
            actv.setOnFocusChangeListener { _, has -> if (has) actv.showDropDown() }
            actv.setOnItemClickListener { _, _, pos, _ ->
                val txt = actv.adapter.getItem(pos) as String
                addPersistedChip(txt, group) { scope.launch(Dispatchers.IO) { insertFn(txt) } }
                actv.setText("")
            }
            actv.setOnEditorActionListener { v, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    val txt = v.text.toString().trim()
                    if (txt.isNotEmpty())
                        addPersistedChip(txt, group) { scope.launch(Dispatchers.IO) { insertFn(txt) } }
                    actv.setText("")
                    true
                } else false
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<TaskDisplay>() {
        override fun areItemsTheSame(a: TaskDisplay, b: TaskDisplay) = a.idTask == b.idTask
        override fun areContentsTheSame(a: TaskDisplay, b: TaskDisplay) = a == b
    }
}
