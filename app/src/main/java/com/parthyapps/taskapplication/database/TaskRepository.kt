package com.parthyapps.taskapplication.database

import android.app.Application
import android.util.Log.d
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class TaskRepository(application: Application) {
    private val TAG = "TaskRepository"
    private val taskDao: TaskDao

    private val activeNotes: LiveData<List<TaskRecord>>

    init {
        val db = TaskDatabase.getInstance(application.applicationContext)
        taskDao = db!!.todoDao()
        activeNotes = taskDao.getArchivedNotes()
    }

    fun saveTask(task: TaskRecord) = runBlocking {
        d(TAG, ":saveTask()")
        this.launch(Dispatchers.IO) {
            taskDao.saveNote(task)
        }
    }

    fun updateTask(task: TaskRecord) = runBlocking {
        d(TAG, ":updateTask()")
        this.launch(Dispatchers.IO) {
            taskDao.updateNote(task)
        }
    }

    fun getActiveNotes(): LiveData<List<TaskRecord>> {
        d(TAG, ":getActiveNotes()")
        return activeNotes
    }

    fun deleteTask(task: TaskRecord) = runBlocking {
        d(TAG, ":deleteTask()")
        this.launch(Dispatchers.IO) {
            taskDao.deleteNote(task)
        }
    }
}