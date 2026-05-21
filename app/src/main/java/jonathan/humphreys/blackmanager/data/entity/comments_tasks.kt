package jonathan.humphreys.blackmanager.data.entity

import androidx.room.Entity
import androidx.room.ColumnInfo
import androidx.room.ForeignKey

@Entity(tableName = "comments_tasks",
    primaryKeys = ["id_task", "id_comment"],
    foreignKeys = [
        ForeignKey(
            entity = Comments::class,
            parentColumns = ["id_comment"],
            childColumns = ["id_comment"],
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
data class CommentsTasks(
    @ColumnInfo("id_task") val idTask : Int,
    @ColumnInfo("id_comment") val idComment : Int
)