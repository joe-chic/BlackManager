package jonathan.humphreys.blackmanager.data.entity

import androidx.room.Entity
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = Users::class,
            parentColumns = ["id_user"],
            childColumns = ["id_creator"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Tasks::class,
            parentColumns = ["id_task"],
            childColumns = ["id_parent_task"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Priorities::class,
            parentColumns = ["id_priority"],
            childColumns = ["id_priority"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Statuses::class,
            parentColumns = ["id_status"],
            childColumns = ["id_status"],
            onDelete = ForeignKey.CASCADE
        ),
    ]
)
data class Tasks(
    @ColumnInfo(name = "id_task")
    @PrimaryKey(autoGenerate = true) val idTask : Int,
    @ColumnInfo(name = "id_creator") val idCreator : Int,
    @ColumnInfo(name = "title") val title : String?,
    @ColumnInfo(name = "description") val description : String ?,
    @ColumnInfo(name = "kick_off") val kickOff : String?,
    @ColumnInfo(name = "deadline") val deadline : String?,
    @ColumnInfo(name="id_parent_task") val idParentTask : Int?,
    @ColumnInfo(name="id_priority") val idPriority : Int?,
    @ColumnInfo(name="id_status") val idStatus : Int?
)