package jonathan.humphreys.blackmanager.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "filters")
data class Filters(
    @ColumnInfo(name = "id_filter")
    @PrimaryKey(autoGenerate = true) val idFilter : Int,
    @ColumnInfo(name = "filter") val filter : String
)