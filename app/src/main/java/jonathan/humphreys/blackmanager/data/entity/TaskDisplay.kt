// TaskDisplay.kt
package jonathan.humphreys.blackmanager.data.entity

data class TaskDisplay(
    val idTask:       Int,
    val title:        String?,
    val description:  String?,
    val kickOff:      String?,
    val deadline:     String?,
    val creatorName:  String?,
    val idPriority:   Int?,
    val priorityName: String?,
    val idStatus:     Int?,
    val statusName:   String?,
    val assignees:    String?,
    val tags:         String?
)
