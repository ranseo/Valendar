package com.ranseo.valendar.ui.fragment

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavArgs
import com.ranseo.valendar.FRAGMENT_KEY_CALENDAR_EVENT
import com.ranseo.valendar.R
import com.ranseo.valendar.data.model.ui.CalendarEventUIState
import com.ranseo.valendar.databinding.FragmentCalendarEventBinding

class CalendarEventFragment : Fragment() {

    lateinit var binding : FragmentCalendarEventBinding
    lateinit var calendarEvent : CalendarEventUIState
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        calendarEvent = arguments?.getParcelable(FRAGMENT_KEY_CALENDAR_EVENT, CalendarEventUIState::class.java) ?: CalendarEventUIState.getEmpty()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<FragmentCalendarEventBinding>(inflater, R.layout.fragment_calendar_event, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner


    }
}