package jonathan.humphreys.blackmanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import jonathan.humphreys.blackmanager.data.entity.Filters

@Dao
interface FilterDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg filters: Filters)

    @Query("SELECT * FROM filters")
    suspend fun getAll(): List<Filters>

    @Query("SELECT * FROM filters WHERE filter = :name LIMIT 1")
    suspend fun findByName(name: String): Filters?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(filter: Filters): Long

    @Query("SELECT * FROM filters WHERE id_filter = :id")
    suspend fun findById(id: Int) : Filters
}