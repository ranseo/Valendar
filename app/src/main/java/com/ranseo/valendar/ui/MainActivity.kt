package com.ranseo.valendar.ui

import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.ranseo.valendar.R
import com.ranseo.valendar.databinding.ActivityMainBinding
import com.ranseo.valendar.util.Log
import com.ranseo.valendar.util.LogTag
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val mainViewModel : MainViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val calendar = binding.layoutCalendar.apply {
            setOnDateChangeListener { p0, year, month, day ->
                val baseDate = "${year}${month}${day}"
                val date = Date(System.currentTimeMillis())
                val baseTime = SimpleDateFormat("kkmm").format(date)
                Log.log(TAG, "${baseDate}, baseTime : ${baseTime}", LogTag.I)
            }
        }






        val btn = binding.btnTmp.apply {
            setOnClickListener {

            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem) = when(item.itemId) {
        R.id.location_spinner -> {
            true
        }
        R.id.location_name -> {
            true
        }
        else -> super.onOptionsItemSelected(item)
    }



    companion object {
        private const val TAG = "MainActivity"
    }
}