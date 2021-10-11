package com.parthyapps.taskapplication.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks WHERE id=:tid")
    fun getNote(tid: Int): TaskRecord

    @Query("SELECT * FROM tasks ")
    fun getArchivedNotes(): LiveData<List<TaskRecord>>

    @Insert
    suspend fun saveNote(task: TaskRecord)

    @Update
    suspend fun updateNote(task: TaskRecord)

    @Delete
    suspend fun deleteNote(task: TaskRecord)

}