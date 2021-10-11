package com.parthyapps.taskapplication.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.parthyapps.taskapplication.database.TaskRecord
import com.parthyapps.taskapplication.databinding.TaskItemBinding
import java.util.*

class TaskListAdapter(todoEvents: TodoEvents) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    Filterable {

    private var tasks: List<TaskRecord> = arrayListOf()
    private var filteredTaskList: List<TaskRecord> = arrayListOf()
    private val listener: TodoEvents = todoEvents

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return VHolder(
            TaskItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun getItemCount(): Int = filteredTaskList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as VHolder
        viewHolder.setBind(filteredTaskList[position], listener)
    }

    class VHolder(private val noteItemBinding: TaskItemBinding) :
        RecyclerView.ViewHolder(noteItemBinding.root) {

        fun setBind(task: TaskRecord, listener: TodoEvents) {
            noteItemBinding.cardTitleTv.text = task.title
            noteItemBinding.cardContentTv.text = task.content

            // Glide.with(require).load(bitmap).override(300, 300).fitCenter().into(noteItemBinding.image)

            noteItemBinding.root.setOnClickListener {
                listener.onViewClicked(task)
            }
        }
    }

    fun setAllTodos(tasks: List<TaskRecord>) {
        this.tasks = tasks

        this.filteredTaskList = tasks
        notifyDataSetChanged()
    }

    interface TodoEvents {
        fun onItemDeleted(task: TaskRecord, position: Int)
        fun onViewClicked(task: TaskRecord)
        fun onItemUnarchived(task: TaskRecord, position: Int)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val charString = p0.toString()
                filteredTaskList = if (charString.isEmpty()) {
                    tasks
                } else {
                    val filteredList = arrayListOf<TaskRecord>()
                    for (row in tasks) {
                        if (row.title!!.lowercase(Locale.getDefault()).contains(
                                charString.lowercase(
                                    Locale.getDefault()
                                )
                            )
                            || row.content!!.lowercase(Locale.getDefault()).contains(
                                charString.lowercase(
                                    Locale.getDefault()
                                )
                            )
                        ) {
                            filteredList.add(row)
                        }
                    }
                    filteredList
                }

                val filterResults = FilterResults()
                filterResults.values = filteredTaskList
                return filterResults
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                filteredTaskList = p1?.values as List<TaskRecord>
                notifyDataSetChanged()
            }

        }
    }

    fun deleteItem(position: Int) {
        listener.onItemDeleted(tasks[position], position)
    }

    fun restoreItem(position: Int) {
        listener.onItemUnarchived(tasks[position], position)
    }

}
