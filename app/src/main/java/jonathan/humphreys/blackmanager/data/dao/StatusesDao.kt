package jonathan.humphreys.blackmanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import jonathan.humphreys.blackmanager.data.entity.Statuses

@Dao
interface StatusesDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg statuses: Statuses)

    @Query("SELECT * FROM statuses ORDER BY id_status")
    suspend fun getAll(): List<Statuses>

    @Query("SELECT * FROM statuses WHERE status = :name LIMIT 1")
    suspend fun findByName(name: String): Statuses?
}