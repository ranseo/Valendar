package com.ranseo.valendar.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ranseo.valendar.data.model.ui.CalendarEventUIState
import com.ranseo.valendar.databinding.ListItemCalendarEventBinding
import com.ranseo.valendar.ui.OnClickListener
import javax.inject.Inject

class CalendarEventAdapter @Inject constructor() : ListAdapter<CalendarEventUIState, CalendarEventAdapter.CalendarEventViewHolder>(CalendarEventUIState.itemCallback()) {


    private lateinit var onClickListener : OnClickListener<CalendarEventUIState>

    fun setOnClickListener(listener: OnClickListener<CalendarEventUIState>) {
        onClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarEventViewHolder {
        return CalendarEventViewHolder.from(parent)
    }



    override fun onBindViewHolder(holder: CalendarEventViewHolder, position: Int) {
        val item = getItem(position) as CalendarEventUIState
        holder.bind(item, onClickListener)
    }



    class CalendarEventViewHolder(val binding: ListItemCalendarEventBinding) : ViewHolder(binding.root) {
        fun bind(
            item: CalendarEventUIState,
            clickListener: OnClickListener<CalendarEventUIState>
        ) {
            binding.title = item.title
            binding.baseTime = item.baseTime
            binding.onClickListener = clickListener
            binding.description = item.description
            binding.calendarEvent = item
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