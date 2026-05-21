// AssigneesTasksDao.kt
package jonathan.humphreys.blackmanager.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import jonathan.humphreys.blackmanager.data.entity.AsigneesTasks
import jonathan.humphreys.blackmanager.data.entity.Users

@Dao
interface AssigneesTasksDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(assignment: AsigneesTasks): Long

    @Query("""
    SELECT u.* FROM users u
    INNER JOIN assignees_tasks at ON u.id_user = at.id_user
    WHERE at.id_task = :taskId
    """)

    suspend fun getAssigneesForTask(taskId: Int): List<Users>

    @Query("DELETE FROM assignees_tasks WHERE id_task = :taskId AND id_user = :userId")
    suspend fun deleteAssigneeFromTask(taskId: Int, userId: Int)

    @Query("DELETE FROM assignees_tasks WHERE id_task = :taskId")
    suspend fun deleteAllForTask(taskId: Int)
}