package com.parthyapps.taskapplication.view

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.parthyapps.taskapplication.R
import com.parthyapps.taskapplication.databinding.ActivityTaskHistoryListBinding
import com.parthyapps.taskapplication.database.TaskRecord
import com.parthyapps.taskapplication.view.adapter.TaskListAdapter
import com.parthyapps.taskapplication.view.viewmodel.TaskViewModel
import com.parthyapps.taskapplication.utils.ItemSwipeCallback
import com.terentiev.notes.utils.Constants

class TaskListActivity : AppCompatActivity(), TaskListAdapter.TodoEvents {

    private lateinit var taskViewModel: TaskViewModel
    private lateinit var searchView: SearchView
    private lateinit var taskAdapter: TaskListAdapter

    private lateinit var binding: ActivityTaskHistoryListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskHistoryListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.toolbar.setTitle(R.string.app_name)
        setSupportActionBar(binding.toolbar.toolbar)

        binding.rv.rvTodoList.layoutManager = LinearLayoutManager(this)
        taskAdapter = TaskListAdapter(this)
        binding.rv.rvTodoList.adapter = taskAdapter
        ItemTouchHelper(
            ItemSwipeCallback(
                taskAdapter,
                applicationContext,
                false
            )
        ).attachToRecyclerView(binding.rv.rvTodoList)

        taskViewModel = ViewModelProviders.of(this).get(TaskViewModel::class.java)
        taskViewModel.getActiveNotes().observe(this, Observer {
            taskAdapter.setAllTodos(it)
        })

        binding.create.setOnClickListener {
            resetSearchView()
            val intent = Intent(this@TaskListActivity, CreateTaskActivity::class.java)
            startActivityForResult(intent, Constants.INTENT_CREATE_NOTE)
        }
    }

    override fun onItemDeleted(task: TaskRecord, position: Int) {
        val snackbar = Snackbar.make(binding.rv.container, R.string.archived, Snackbar.LENGTH_LONG)
        snackbar.setAction(R.string.undo) { undoArchive(task, position) }
        snackbar.show()
    }

    private fun undoArchive(task: TaskRecord, position: Int) {
        taskAdapter.notifyItemInserted(position)
    }

    override fun onViewClicked(task: TaskRecord) {
        resetSearchView()
        val intent = Intent(this@TaskListActivity, CreateTaskActivity::class.java)
        intent.putExtra(Constants.INTENT_OBJECT, task)
        startActivityForResult(intent, Constants.INTENT_UPDATE_NOTE)
    }

    override fun onItemUnarchived(task: TaskRecord, position: Int) {
        onItemDeleted(task, position)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val todo = data?.getParcelableExtra<TaskRecord>(Constants.INTENT_OBJECT)!!
            when (requestCode) {
                Constants.INTENT_CREATE_NOTE -> {
                    taskViewModel.saveTask(todo)
                }
                Constants.INTENT_UPDATE_NOTE -> {
                    taskViewModel.updateTask(todo)
                }
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        searchView = menu?.findItem(R.id.search_active_item)?.actionView as SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.maxWidth = Int.MAX_VALUE
        searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                taskAdapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                taskAdapter.filter.filter(newText)
                return false
            }

        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search_active_item -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        resetSearchView()
        super.onBackPressed()
    }

    private fun resetSearchView() {
        if (!searchView.isIconified) {
            searchView.isIconified = true
            return
        }
    }
}
