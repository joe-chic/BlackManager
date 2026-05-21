package jonathan.humphreys.blackmanager.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "priorities")
data class Priorities(
    @ColumnInfo(name = "id_priority")
    @PrimaryKey(autoGenerate = true) val idPriority : Int,
    @ColumnInfo(name = "priority") val priority : String
)