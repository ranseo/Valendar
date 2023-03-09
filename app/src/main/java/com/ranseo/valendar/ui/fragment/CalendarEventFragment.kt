package com.ranseo.valendar.ui.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.ranseo.valendar.FRAGMENT_KEY_CALENDAR_EVENT
import com.ranseo.valendar.R
import com.ranseo.valendar.data.Event
import com.ranseo.valendar.data.model.ui.CalendarEventUIState
import com.ranseo.valendar.databinding.FragmentCalendarEventBinding
import dagger.hilt.android.AndroidEntryPoint
import com.ranseo.valendar.util.Log
import com.ranseo.valendar.util.LogTag

@AndroidEntryPoint
class CalendarEventFragment : Fragment() {

    lateinit var binding: FragmentCalendarEventBinding
    lateinit var calendarEvent: CalendarEventUIState

    private val calendarEventViewModel: CalendarEventViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        calendarEvent =
            arguments?.getParcelable(FRAGMENT_KEY_CALENDAR_EVENT, CalendarEventUIState::class.java)
                ?: CalendarEventUIState.getEmpty()

        Log.log(TAG, "onCreate() : calendarEvent ${calendarEvent}", LogTag.I)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<FragmentCalendarEventBinding>(
            inflater,
            R.layout.fragment_calendar_event,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.log(TAG, "onViewCreated : ${calendarEvent}", LogTag.I)

        with(binding) {
            lifecycleOwner = viewLifecycleOwner
            viewModel = calendarEventViewModel
            calendarEventViewModel.setCalendarEvent(this@CalendarEventFragment.calendarEvent)
            calendarEvent = this@CalendarEventFragment.calendarEvent
        }

        with(calendarEventViewModel) {
            isDeleted.observe(viewLifecycleOwner, isDeletedObserver())
        }
    }

    private fun isDeletedObserver() = Observer<Event<Any?>> {
        it.getContentIfNotHandled()?.let {
            requireActivity().onBackPressed()
        }
    } 


    companion object {
        private const val TAG = "CalendarEventFragment"
    }

}