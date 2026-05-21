package jonathan.humphreys.blackmanager.data.entity

import androidx.room.Entity
import androidx.room.ColumnInfo
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "projects",
    foreignKeys = [
        ForeignKey(
            entity = Users::class,
            parentColumns = ["id_user"],
            childColumns = ["id_creator"],
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
        )
    ]
)
data class Projects(
    @ColumnInfo("id_project")
    @PrimaryKey(autoGenerate = true) val idProject : Int,
    @ColumnInfo("id_creator") val idCreator : Int,
    @ColumnInfo("title") val title : String?,
    @ColumnInfo("description") val description : String?,
    @ColumnInfo("kick_off") val kickOff : String?,
    @ColumnInfo("deadline") val deadline : String?,
    @ColumnInfo("id_priority") val idPriority : Int?,
    @ColumnInfo("id_status") val idStatus : Int?
)