package jonathan.humphreys.blackmanager.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import jonathan.humphreys.blackmanager.data.database.AppDatabase
import jonathan.humphreys.blackmanager.data.entity.TaskDisplay
import jonathan.humphreys.blackmanager.data.entity.Tasks

class TasksViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getInstance(application).taskDao()

    // Now exposes a joined projection with human-readable fields
    val tasks: LiveData<List<TaskDisplay>> = dao.getAllTaskDisplays()

    fun insertTask(task: Tasks) = viewModelScope.launch { dao.insertTask(task) }
    fun updateTask(task: Tasks) = viewModelScope.launch { dao.updateTask(task) }
    fun deleteTask(task: Tasks) = viewModelScope.launch { dao.deleteTask(task) }
}