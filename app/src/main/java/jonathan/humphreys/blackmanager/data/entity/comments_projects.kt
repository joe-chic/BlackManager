package jonathan.humphreys.blackmanager.data.entity

import androidx.room.Entity
import androidx.room.ColumnInfo
import androidx.room.ForeignKey

@Entity(tableName = "comments_projects",
    primaryKeys = ["id_project", "id_comment"],
    foreignKeys = [
        ForeignKey(
            entity = Comments::class,
            parentColumns = ["id_comment"],
            childColumns = ["id_comment"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Projects::class,
            parentColumns = ["id_project"],
            childColumns = ["id_project"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CommentsProjects(
    @ColumnInfo("id_project")  val idProject : Int,
    @ColumnInfo("id_comment") val idComment : Int
)