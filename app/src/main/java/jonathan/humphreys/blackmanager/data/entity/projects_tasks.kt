package jonathan.humphreys.blackmanager.data.entity

import androidx.room.Entity
import androidx.room.ColumnInfo
import androidx.room.ForeignKey

@Entity(tableName = "projects_tasks",
    primaryKeys = ["id_project", "id_task"],
    foreignKeys = [
        ForeignKey(
            entity = Projects::class,
            parentColumns = ["id_project"],
            childColumns = ["id_project"],
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
class ProjectsTasks(
    @ColumnInfo("id_project") val idProject : Int,
    @ColumnInfo("id_task") val idTask : Int
)