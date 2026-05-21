package jonathan.humphreys.blackmanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import jonathan.humphreys.blackmanager.data.entity.Users

@Dao
interface UserDao{
    @Query("SELECT username FROM users")
    suspend fun getAllUsernames(): List<String>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: Users) : Long

    @Query("SELECT * FROM users WHERE username = :name LIMIT 1")
    suspend fun getByUsername(name: String): Users?

    @Query("SELECT username FROM users WHERE id_user = :id LIMIT 1")
    suspend fun getUsernameById(id: Int): String?

    @Query("SELECT * FROM users")
    suspend fun getAll() : List<Users>
}