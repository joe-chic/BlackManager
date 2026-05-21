package jonathan.humphreys.blackmanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import jonathan.humphreys.blackmanager.data.entity.Priorities

@Dao
interface PriorityDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg priorities: Priorities)

    @Query("SELECT * FROM priorities ORDER BY id_priority")
    suspend fun getAll(): List<Priorities>

    @Query("SELECT * FROM priorities WHERE priority = :name LIMIT 1")
    suspend fun findByName(name: String): Priorities?
}
