package com.ranseo.valendar.ui

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.ranseo.valendar.databinding.ActivityMainBinding
import com.ranseo.valendar.util.FcstBaseTime
import com.ranseo.valendar.util.Log
import com.ranseo.valendar.util.LogTag
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val mainViewModel : MainViewModel by viewModels()
    private lateinit var bottomWeatherSheet : ConstraintLayout
    private lateinit var sheetBehavior: BottomSheetBehavior<ConstraintLayout>
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewModel = mainViewModel
        binding.lifecycleOwner = this

        bottomWeatherSheet = binding.layoutBottomSheet
        sheetBehavior = BottomSheetBehavior.from(bottomWeatherSheet)

        sheetBehavior.addBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback(){
            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if(slideOffset == 0.5f)
                    sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        })

        val calendar = binding.layoutCalendar.apply {
            setOnDateChangeListener { p0, year, month, day ->
                val baseDate = "%d%02d%02d".format(year,month+1,day)
                val date = Date(System.currentTimeMillis())
                val currTime = SimpleDateFormat("kkmm").format(date)

                val baseTime = FcstBaseTime.getFcstBaseTime(currTime)
                Log.log(TAG, "${baseDate}, currTime : ${currTime} ,baseTime : ${baseTime}", LogTag.I)
                getWeatherInfo(baseDate, baseTime,"55","123")
                showWeatherSheet()
            }
        }
    }

    fun showWeatherSheet() {
        sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    fun getWeatherInfo(baseDate:String, baseTime:String, nx:String, ny:String) {
        mainViewModel.getWeather(baseDate,baseTime,nx,ny)
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}