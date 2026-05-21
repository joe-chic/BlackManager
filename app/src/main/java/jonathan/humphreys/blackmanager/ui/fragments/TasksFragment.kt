// TasksFragment.kt
package jonathan.humphreys.blackmanager.ui.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import jonathan.humphreys.blackmanager.R
import jonathan.humphreys.blackmanager.data.database.AppDatabase
import jonathan.humphreys.blackmanager.data.entity.*
import jonathan.humphreys.blackmanager.databinding.FragmentTaskBinding
import jonathan.humphreys.blackmanager.databinding.ItemTaskFormBinding
import jonathan.humphreys.blackmanager.ui.adapters.TasksAdapter
import jonathan.humphreys.blackmanager.ui.viewmodels.TasksViewModel
import jonathan.humphreys.blackmanager.ui.viewmodels.TasksViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class TasksFragment : Fragment() {
    private var _binding: FragmentTaskBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TasksViewModel by viewModels {
        TasksViewModelFactory(requireActivity().application)
    }

    private lateinit var prefs: SharedPreferences

    // dropdown data
    private lateinit var priorities: List<Priorities>
    private lateinit var statuses:  List<Statuses>
    private lateinit var filters:   List<Filters>
    private lateinit var users:     List<String>

    // for new-task chips
    private val selectedAssignees = mutableListOf<String>()
    private val selectedTags      = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        loadDropdownsAndSetup()
    }

    private fun loadDropdownsAndSetup() {
        val dao = AppDatabase.getInstance(requireContext())
        lifecycleScope.launch {
            val currentUserId = prefs.getInt("currentUserId", 1)
            val username = withContext(Dispatchers.IO) {
                dao.userDao().getUsernameById(currentUserId)
            }

            withContext(Dispatchers.IO) {
                priorities = dao.priorityDao().getAll()
                statuses   = dao.statusesDao().getAll()
                filters    = dao.filterDao().getAll()
                users      = dao.userDao().getAllUsernames()
            }
            bindForm(username)  // setup new-task form
            setupRecycler()
        }
    }

    private fun bindForm(creatorName: String?) {
        // 1) Find and bind the form root
        val formRoot: View = requireView().findViewById(R.id.cardTaskInputRoot)
        val form = ItemTaskFormBinding.bind(formRoot)

        // 2) Wire up the dropdown adapters
        form.actvAssignees.setAdapter(
            ArrayAdapter(requireContext(), R.layout.dropdown_item_white, users)
        )
        form.actvPriority.setAdapter(
            ArrayAdapter(requireContext(), R.layout.dropdown_item_white, priorities.map { it.priority })
        )
        form.actvStatus.setAdapter(
            ArrayAdapter(requireContext(), R.layout.dropdown_item_white, statuses.map { it.status })
        )
        form.actvTags.setAdapter(
            ArrayAdapter(requireContext(), R.layout.dropdown_item_white, filters.map { it.filter })
        )

        // 3) Always show the drop‑down when focused
        listOf(form.actvAssignees, form.actvPriority, form.actvStatus, form.actvTags)
            .forEach { actv ->
                actv.threshold = 0
                actv.setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) actv.showDropDown()
                }
            }

        // 4) Prefill the creator field from the argument
        form.etCreator.setText(creatorName ?: "")

        // 5) Hook up chips, date‑pickers, and the save button
        setupChips(form)
        setupDatePickers(form)
        setupSave(form)
    }

    private fun setupRecycler() {
        // build maps
        val priorityMap = priorities.associate { it.priority to it.idPriority }
        val statusMap   = statuses.associate  { it.status   to it.idStatus   }
        val currentUser = prefs.getInt("currentUserId", 1)
        val adapter = TasksAdapter(
            scope         = viewLifecycleOwner.lifecycleScope,
            currentUserId = currentUser,
            priorityMap   = priorityMap,
            statusMap     = statusMap,
            allUsers = users,
            allFilters = filters.map { it.filter },
            onUpdate      = { viewModel.updateTask(it) },
            onDelete      = { id ->
                viewModel.deleteTask(
                    Tasks(id, currentUser, null, null, null, null, null, null, null)
                )
            }
        )
        binding.rvTasks.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = adapter
        }
        viewModel.tasks.observe(viewLifecycleOwner) { adapter.submitList(it) }
    }

    private fun setupChips(form: ItemTaskFormBinding) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE)
                as InputMethodManager
        fun wire(
            actv: android.widget.AutoCompleteTextView,
            group: ChipGroup,
            list: MutableList<String>
        ) {
            actv.setOnClickListener { actv.showDropDown(); actv.requestFocus();
                imm.showSoftInput(actv, InputMethodManager.SHOW_IMPLICIT)
            }
            actv.setOnItemClickListener { _, _, pos, _ ->
                val text = actv.adapter.getItem(pos) as String
                if (list.add(text)) addChip(text, group, list)
                actv.setText("")
            }
            actv.setOnEditorActionListener { _, id, _ ->
                if (id == EditorInfo.IME_ACTION_DONE) {
                    val txt = actv.text.toString().trim()
                    if (txt.isNotEmpty() && list.add(txt)) addChip(txt, group, list)
                    actv.setText("")
                    true
                } else false
            }
        }
        wire(form.actvAssignees, form.chipGroupAssignees, selectedAssignees)
        wire(form.actvTags,      form.chipGroupTags,      selectedTags)
    }

    private fun addChip(text: String, chipGroup: ChipGroup, list: MutableList<String>) {
        val chip = Chip(requireContext()).apply {
            this.text = text
            isCloseIconVisible = true
            setOnCloseIconClickListener {
                chipGroup.removeView(this)
                list.remove(text)
            }
        }
        chipGroup.addView(chip)
    }

    private fun setupDatePickers(form: ItemTaskFormBinding) {
        form.etStartDate.setOnClickListener { pickDateTime { form.etStartDate.setText(it) } }
        form.etDeadline.setOnClickListener { pickDateTime { form.etDeadline.setText(it) } }
    }

    private fun setupSave(form: ItemTaskFormBinding) {
        form.btnSaveTask.setOnClickListener {
            val currentUser = prefs.getInt("currentUserId", 1)
            val newTask = Tasks(
                idTask       = 0,
                idCreator    = currentUser,
                title        = form.etTitle.text.toString().takeIf(String::isNotBlank),
                description  = form.etDescription.text.toString().takeIf(String::isNotBlank),
                kickOff      = form.etStartDate.text.toString().takeIf(String::isNotBlank),
                deadline     = form.etDeadline.text.toString().takeIf(String::isNotBlank),
                idParentTask = null,
                idPriority   = priorities.find { it.priority == form.actvPriority.text.toString() }?.idPriority,
                idStatus     = statuses.find   { it.status   == form.actvStatus.text.toString()   }?.idStatus
            )
            lifecycleScope.launch(Dispatchers.IO) {
                val dao = AppDatabase.getInstance(requireContext())
                val taskId = dao.taskDao().insertTask(newTask).toInt()
                // insert relationships...
                selectedAssignees.forEach { name ->
                    val u = dao.userDao().getByUsername(name) ?:
                    Users(0, name).also { dao.userDao().insert(it) }
                    dao.assigneesTasksDao().insert(
                        AsigneesTasks(
                            idUser = u.idUser,
                            idTask = taskId,
                            timestampAssignation =
                            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                                .format(Date())
                        )
                    )
                }
                selectedTags.forEach { tag ->
                    val f = dao.filterDao().findByName(tag) ?: run {
                        val newId = dao.filterDao().insert(Filters(0, tag))
                        dao.filterDao().findById(newId.toInt())
                    }
                    dao.taskFilterDao().insert(TasksFilters(taskId, f.idFilter))
                }

                val allForThis = dao.taskFilterDao().getFiltersForTask(taskId)
                Log.d("TasksFragment", "After save, task $taskId filters in DB = ${allForThis.map { it.filter }}")
            }
        }
    }

    private fun pickDateTime(onPicked: (String) -> Unit) {
        val now = Calendar.getInstance()
        DatePickerDialog(
            requireContext(), { _, y, m, d ->
                TimePickerDialog(
                    requireContext(), { _, h, mi ->
                        Calendar.getInstance().apply { set(y, m, d, h, mi) }.time.let {
                            onPicked(
                                SimpleDateFormat("yyyy-MM-dd HH:mm:00.000",
                                    Locale.getDefault()).format(it)
                            )
                        }
                    }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true
                ).show()
            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}