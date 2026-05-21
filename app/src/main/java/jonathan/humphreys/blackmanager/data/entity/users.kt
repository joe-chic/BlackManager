package jonathan.humphreys.blackmanager.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// We use ColumnInfo explicitly to rename the columns in the database.
// Adding the ? at the end of the data type means we have a NULL attribute.
// What does autoGenerate do in Primary Key? autoGenerate is equivalent to AUTOINCREMENT in SQL.

// How to a avoid waisting the id indexes?
// How to implement the triggers for table participation?

@Entity(tableName = "users")
data class Users(
    @ColumnInfo(name = "id_user")
    @PrimaryKey(autoGenerate = true) val idUser : Int,
    @ColumnInfo(name = "username") val username : String
)