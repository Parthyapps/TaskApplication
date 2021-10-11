package com.parthyapps.taskapplication.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TaskRecord::class], version = 4, exportSchema = false)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun todoDao(): TaskDao

    companion object {
        private var INSTANCE: TaskDatabase? = null

        fun getInstance(context: Context): TaskDatabase? {
            if (INSTANCE == null) {
                synchronized(TaskDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context,
                        TaskDatabase::class.java,
                        "task_db"
                    ).build()
                }
            }
            return INSTANCE
        }
    }
}