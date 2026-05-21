package jonathan.humphreys.blackmanager.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.ForeignKey

@Entity(tableName = "comments",
        foreignKeys = [
            ForeignKey(
                entity = Users::class,
                parentColumns = ["id_user"],
                childColumns = ["id_user"],
                onDelete = ForeignKey.CASCADE
            )
        ]
)
class Comments(
    @ColumnInfo(name="id_comment")
    @PrimaryKey(autoGenerate = true) val idComment : Int,
    @ColumnInfo(name = "id_user") val idUser : Int,
    @ColumnInfo(name = "description") val description : String
)