package com.ranseo.valendar

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import com.ranseo.valendar.databinding.ActivityMainBinding
import com.ranseo.valendar.util.Log
import com.ranseo.valendar.util.LogTag
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val calendar = binding.layoutCalendar.apply {
            setOnDateChangedListener(OnDateSelectedListener{ widget, date, selected ->
                val day = date.day
                val month = date.month
                val year = date.year

                Log.log(TAG, "selected : ${selected}, ${LocalDate.of(year, month, day)}", LogTag.I)
            })
        }

    }

    companion object {
        private const val TAG = "MainActivity"
    }
}