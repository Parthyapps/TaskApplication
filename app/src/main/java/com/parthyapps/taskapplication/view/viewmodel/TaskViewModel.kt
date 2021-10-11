package com.parthyapps.taskapplication.view.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.parthyapps.taskapplication.database.TaskRecord
import com.parthyapps.taskapplication.database.TaskRepository

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repo: TaskRepository = TaskRepository(application)

    fun saveTask(task: TaskRecord) {
        repo.saveTask(task)
    }

    fun updateTask(task: TaskRecord) {
        repo.updateTask(task)
    }

    fun getActiveNotes(): LiveData<List<TaskRecord>> {
        return repo.getActiveNotes()
    }

}