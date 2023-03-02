package com.ranseo.valendar.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ranseo.valendar.data.model.ui.CalendarEventUIState
import com.ranseo.valendar.databinding.ListItemCalendarEventBinding

class CalendarEventAdapter : ListAdapter<CalendarEventUIState, CalendarEventAdapter.CalendarEventViewHolder>(CalendarEventUIState.itemCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarEventViewHolder {
        return CalendarEventViewHolder.from(parent)
    }



    override fun onBindViewHolder(holder: CalendarEventViewHolder, position: Int) {
        val item = getItem(position) as CalendarEventUIState
        holder.bind(item)
    }



    class CalendarEventViewHolder(val binding: ListItemCalendarEventBinding) : ViewHolder(binding.root) {
        fun bind(
            item: CalendarEventUIState
        ) {
            binding.title = item.title
            binding.writingTime = item.writingTime
        }
        companion object {
            fun from(parent: ViewGroup): CalendarEventViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemCalendarEventBinding.inflate(layoutInflater, parent, false)
                return CalendarEventViewHolder(binding)
            }
        }
    }
}