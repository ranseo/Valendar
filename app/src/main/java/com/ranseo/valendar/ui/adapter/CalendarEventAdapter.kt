package com.ranseo.valendar.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ranseo.valendar.data.model.ui.CalendarEventUIState
import com.ranseo.valendar.databinding.ListItemCalendarEventBinding
import com.ranseo.valendar.ui.OnClickListener
import javax.inject.Inject

class CalendarEventAdapter @Inject constructor() : ListAdapter<CalendarEventUIState, CalendarEventAdapter.CalendarEventViewHolder>(CalendarEventUIState.itemCallback()) {


    lateinit var onClickListener : OnClickListener

    fun setOnClickListener(listener: OnClickListener) {
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
            clickListener: OnClickListener
        ) {
            binding.title = item.title
            binding.writingTime = item.writingTime
            binding.onClickListener = clickListener
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