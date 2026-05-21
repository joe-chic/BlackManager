// TaskDao.kt
package jonathan.humphreys.blackmanager.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import jonathan.humphreys.blackmanager.data.entity.TaskDisplay
import jonathan.humphreys.blackmanager.data.entity.TaskWithRelations
import jonathan.humphreys.blackmanager.data.entity.Tasks

@Dao
interface TaskDao {
    /** Inserts a task and returns its auto‑generated ID. */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTask(task: Tasks): Long

    /** Returns all raw Tasks (for internal use). */
    @Query("SELECT * FROM tasks")
    fun getAllTasks(): LiveData<List<Tasks>>

    @Update
    suspend fun updateTask(task: Tasks)

    @Delete
    suspend fun deleteTask(task: Tasks)

    /**
     * Returns a fully joined view combining Tasks + Users + Priorities + Statuses,
     * mapped into your TaskDisplay projection.
     */
    @Query("""
        SELECT
            t.id_task          AS idTask,
            t.title            AS title,
            t.description      AS description,
            t.kick_off         AS kickOff,
            t.deadline         AS deadline,
            u.username         AS creatorName,
            p.id_priority      AS idPriority,
            p.priority         AS priorityName,
            s.id_status        AS idStatus,
            s.status           AS statusName,
            GROUP_CONCAT(DISTINCT a.username) AS assignees,
            GROUP_CONCAT(DISTINCT f.filter)      AS tags
        FROM tasks t
        LEFT JOIN users      u ON t.id_creator  = u.id_user
        LEFT JOIN priorities p ON t.id_priority = p.id_priority
        LEFT JOIN statuses   s ON t.id_status   = s.id_status
        LEFT JOIN assignees_tasks at ON at.id_task = t.id_task
        LEFT JOIN users a ON a.id_user = at.id_user
        LEFT JOIN tasks_filters tf ON tf.id_task = t.id_task
        LEFT JOIN filters f ON f.id_filter = tf.id_filter
        GROUP BY t.id_task
        ORDER BY t.deadline ASC
    """)
    fun getAllTaskDisplays(): LiveData<List<TaskDisplay>>
}
