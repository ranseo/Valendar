package com.ranseo.valendar.ui

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import com.ranseo.valendar.databinding.ActivityMainBinding
import com.ranseo.valendar.util.Log
import com.ranseo.valendar.util.LogTag
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val mainViewModel : MainViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val calendar = binding.layoutCalendar.apply {
            setOnDateChangedListener(OnDateSelectedListener{ widget, date, selected ->
                val day = date.day
                val month = date.month
                val year = date.year

                val baseDate = "${year}${month}${day}"
                val baseTime = System.currentTimeMillis()
                Log.log(TAG, "selected : ${selected}, ${LocalDate.of(year, month, day)}", LogTag.I)

            })
        }

        val btn = binding.btnTmp.apply {
            setOnClickListener {
                mainViewModel.getLandFcst()
            }
        }

    }

    companion object {
        private const val TAG = "MainActivity"
    }
}