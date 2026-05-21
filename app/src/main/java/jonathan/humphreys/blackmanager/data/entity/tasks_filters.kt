package jonathan.humphreys.blackmanager.data.entity

import androidx.room.Entity
import androidx.room.ColumnInfo
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "tasks_filters",
    primaryKeys = ["id_task", "id_filter"],
    foreignKeys = [
        ForeignKey(
            entity = Tasks::class,
            parentColumns = ["id_task"],
            childColumns = ["id_task"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Filters::class,
            parentColumns = ["id_filter"],
            childColumns = ["id_filter"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
class TasksFilters(
    @ColumnInfo("id_task") val idTask : Int,
    @ColumnInfo("id_filter") val idFilter : Int
)