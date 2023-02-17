package com.ranseo.valendar.ui

import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
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

    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var bottomWeatherSheet: ConstraintLayout
    private lateinit var sheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest : LocationRequest
    private lateinit var locationCallback : LocationCallback

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewModel = mainViewModel
        binding.lifecycleOwner = this

        bottomWeatherSheet = binding.layoutBottomSheet
        sheetBehavior = BottomSheetBehavior.from(bottomWeatherSheet)

        sheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (slideOffset == 0.5f)
                    sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        })

        val calendar = binding.layoutCalendar.apply {
            setOnDateChangeListener { p0, year, month, day ->
                val baseDate = "%d%02d%02d".format(year, month + 1, day)
                val date = Date(System.currentTimeMillis())
                val currTime = SimpleDateFormat("kkmm").format(date)

                val baseTime = FcstBaseTime.getFcstBaseTime(currTime)
                Log.log(
                    TAG,
                    "${baseDate}, currTime : ${currTime} ,baseTime : ${baseTime}",
                    LogTag.I
                )
                getWeatherInfo(baseDate, baseTime, "55", "123")
                showWeatherSheet()
            }
        }

        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                when {
                    permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {

                    }
                    permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {

                    }
                    else -> {

                    }
                }
            }

        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )== PackageManager.PERMISSION_GRANTED
             &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )== PackageManager.PERMISSION_GRANTED -> {
            }

            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) ||
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION
            ) -> {

            }

            else -> requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                p0 ?: return
                for(location in p0.locations) {
                    Log.log(TAG, "LocationCallback() : ${location.toString()}, 위도 : ${location.latitude}, 경도 : ${location.longitude}", LogTag.I)
                }
            }
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
            Log.log(TAG, "btnLoc getLocation() : ${loc.toString()}", LogTag.I)
        }
        createLocationRequest()
    }

    private fun startLocationUpdate() {
        fusedLocationClient.requestLocationUpdates(locationRequest,
            locationCallback,
            Looper.getMainLooper())
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdate()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun createLocationRequest() {
        locationRequest = LocationRequest.Builder(10000)
            .setPriority(android.location.LocationRequest.QUALITY_LOW_POWER)
            .build()

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client : SettingsClient = LocationServices.getSettingsClient(this)
        val task : Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener { locationSettingResponse ->
            Log.log(TAG, "task.addOnSuccessListener : ${locationSettingResponse.locationSettingsStates}", LogTag.I)

        }

        task.addOnFailureListener{ exception ->
            if(exception is ResolvableApiException) {
                try{
                    Log.log(TAG,"task failure : ${exception.message}", LogTag.I)
//                    exception.startResolutionForResult(this@MainActivity,
//                    REQUEST_CHECK_SETTINGS)
                }catch (sendEx: IntentSender.SendIntentException) {

                }
            }
        }
    }



    fun showWeatherSheet() {
        sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    fun getWeatherInfo(baseDate: String, baseTime: String, nx: String, ny: String) {
        mainViewModel.getWeather(baseDate, baseTime, nx, ny)
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}