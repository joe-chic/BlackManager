package jonathan.humphreys.blackmanager.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "statuses")
data class Statuses(
    @ColumnInfo(name = "id_status")
    @PrimaryKey(autoGenerate = true) val idStatus : Int,
    @ColumnInfo(name = "status") val status : String
)