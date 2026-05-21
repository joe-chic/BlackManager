package jonathan.humphreys.blackmanager.data.entity

import androidx.room.Entity
import androidx.room.ColumnInfo
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "assignees_projects",
    foreignKeys = [
        ForeignKey(
            entity = Users::class,
            parentColumns = ["id_user"],
            childColumns = ["id_user"],
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
data class AsigneesProjects(
    @ColumnInfo("id_user")
    @PrimaryKey(autoGenerate = true) val idUser : Int,
    @ColumnInfo("id_project") val idProject : Int,
    @ColumnInfo("timestamp_assignation") val timestampAssignation : String
)