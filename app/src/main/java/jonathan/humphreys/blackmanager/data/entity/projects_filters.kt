package jonathan.humphreys.blackmanager.data.entity

import androidx.room.Entity
import androidx.room.ColumnInfo
import androidx.room.ForeignKey

@Entity(tableName = "projects_filters",
    primaryKeys = ["id_project", "id_filter"],
    foreignKeys = [
        ForeignKey(
            entity = Projects::class,
            parentColumns = ["id_project"],
            childColumns = ["id_project"],
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
class ProjectsFilters(
    @ColumnInfo("id_project") val idProject : Int,
    @ColumnInfo("id_filter") val idFilter : Int
)