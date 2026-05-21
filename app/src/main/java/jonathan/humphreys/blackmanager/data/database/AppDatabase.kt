package jonathan.humphreys.blackmanager.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import jonathan.humphreys.blackmanager.data.dao.AssigneesTasksDao
import jonathan.humphreys.blackmanager.data.dao.FilterDao
import jonathan.humphreys.blackmanager.data.dao.PriorityDao
import jonathan.humphreys.blackmanager.data.dao.StatusesDao
import jonathan.humphreys.blackmanager.data.dao.TaskDao
import jonathan.humphreys.blackmanager.data.dao.TaskFilterDao
import jonathan.humphreys.blackmanager.data.dao.UserDao
import jonathan.humphreys.blackmanager.data.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import androidx.room.TypeConverters

@Database(
    entities = [
        AsigneesTasks::class,
        AsigneesProjects::class,
        Comments::class,
        CommentsProjects::class,
        CommentsTasks::class,
        Filters::class,
        Priorities::class,
        Projects::class,
        ProjectsFilters::class,
        ProjectsTasks::class,
        Statuses::class,
        Tasks::class,
        TasksFilters::class,
        Users::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun userDao(): UserDao
    abstract fun statusesDao(): StatusesDao
    abstract fun priorityDao(): PriorityDao
    abstract fun filterDao(): FilterDao
    abstract fun taskFilterDao(): TaskFilterDao
    abstract fun assigneesTasksDao(): AssigneesTasksDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "blackmanager_db"
            )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Insert default data in a coroutine
                        INSTANCE?.let { database ->
                            CoroutineScope(Dispatchers.IO).launch {
                                // Default user
                                database.userDao().insert(
                                    Users(
                                        idUser = 0,
                                        username = "Usuario de Android"
                                    )
                                )

                                // Default statuses
                                database.statusesDao().insertAll(
                                    Statuses(0, "Sin empezar"),
                                    Statuses(0, "En curso"),
                                    Statuses(0, "Listo")
                                )

                                // Default priorities
                                database.priorityDao().insertAll(
                                    Priorities(0, "Baja"),
                                    Priorities(0, "Media"),
                                    Priorities(0, "Alta")
                                )
                            }
                        }
                    }

                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        // Ensure foreign keys are enforced
                        db.execSQL("PRAGMA foreign_keys=ON")
                        val cursor = db.query("PRAGMA foreign_key_check")
                        while (cursor.moveToNext()) {
                            val table = cursor.getString(0)
                            val rowId = cursor.getInt(1)
                            val parent = cursor.getString(2)
                            val fkIndex = cursor.getInt(3)
                            android.util.Log.e(
                                "FK_CHECK",
                                "Violation in table: $table, rowId: $rowId, parent: $parent, fkIndex: $fkIndex"
                            )
                        }
                        cursor.close()
                    }
                })
                .build()
        }
    }
}