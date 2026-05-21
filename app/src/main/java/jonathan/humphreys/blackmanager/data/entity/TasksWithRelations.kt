package jonathan.humphreys.blackmanager.data.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class TaskWithRelations(
    @Embedded val task: Tasks,

    @Relation(
        parentColumn = "id_creator",
        entityColumn = "id_user"
    )
    val creator: Users,

    @Relation(
        parentColumn = "id_priority",
        entityColumn = "id_priority"
    )
    val priority: Priorities,

    @Relation(
        parentColumn = "id_status",
        entityColumn = "id_status"
    )
    val status: Statuses,

    @Relation(
        parentColumn = "id_task",
        entityColumn = "id_user",
        associateBy = Junction(
            value = AsigneesTasks::class,
            parentColumn = "id_task",
            entityColumn = "id_user"
        )
    )
    val assignees: List<Users>
)
