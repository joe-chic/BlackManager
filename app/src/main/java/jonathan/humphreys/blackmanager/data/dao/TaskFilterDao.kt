package jonathan.humphreys.blackmanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import jonathan.humphreys.blackmanager.data.entity.Filters
import jonathan.humphreys.blackmanager.data.entity.TasksFilters

@Dao
interface TaskFilterDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(tasksFilters: TasksFilters)

    @Query("DELETE FROM tasks_filters WHERE id_task = :taskId")
    suspend fun deleteAllForTask(taskId: Int): Int

    @Query("""
    SELECT f.*
      FROM filters AS f
      INNER JOIN tasks_filters AS tf
        ON tf.id_filter = f.id_filter
     WHERE tf.id_task = :taskId
  """)
    suspend fun getFiltersForTask(taskId: Int): List<Filters>

    @Query("DELETE FROM tasks_filters WHERE id_task = :taskId AND id_filter = :filterId")
    suspend fun deleteTaskFilter(taskId: Int, filterId: Int)
}
