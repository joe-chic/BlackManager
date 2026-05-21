package jonathan.humphreys.blackmanager.data.entity

import androidx.room.Entity
import androidx.room.ColumnInfo
import androidx.room.ForeignKey

@Entity(tableName = "assignees_tasks",
    primaryKeys = ["id_user", "id_task"],
    foreignKeys = [
        ForeignKey(
            entity = Users::class,
            parentColumns = ["id_user"],
            childColumns = ["id_user"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Tasks::class,
            parentColumns = ["id_task"],
            childColumns = ["id_task"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class AsigneesTasks(
    @ColumnInfo("id_user")
    val idUser : Int,
    @ColumnInfo("id_task")
    val idTask : Int,
    @ColumnInfo("timestamp_assignation")
    val timestampAssignation : String
)