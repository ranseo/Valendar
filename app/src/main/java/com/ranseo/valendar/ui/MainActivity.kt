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
import com.ranseo.valendar.util.LocationConverter
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

    private var requestingLocationUpdates : Boolean = false
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest : LocationRequest
    private lateinit var locationCallback : LocationCallback

    private lateinit var  locationConverter: LocationConverter

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

        updateValuesFromBundle(savedInstanceState)

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

        requestPermission()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
            Log.log(TAG, "btnLoc getLocation() : ${loc.toString()}", LogTag.I)
        }

        locationConverter = LocationConverter()


    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
                    &&
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED -> {
            }

            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) ||
                    shouldShowRequestPermissionRationale(
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) -> {

            }

            else -> requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun startLocationUpdate() {
        createLocationRequest()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                p0 ?: return
                for(location in p0.locations) {
                    val lon = location.longitude //경도
                    val lat = location.latitude //위도
                    Log.log(TAG, "LocationCallback() : ${location.toString()}, 위도 : ${lat}, 경도 : ${lon}", LogTag.I)
                    locationConverter.convertLLToXY(lon.toFloat(), lat.toFloat())
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest,
            locationCallback,
            Looper.getMainLooper()).addOnSuccessListener {
                requestingLocationUpdates = true
        } .addOnFailureListener {
            requestingLocationUpdates = false
        }
    }

    override fun onResume() {
        super.onResume()
        if(!requestingLocationUpdates)startLocationUpdate()

        locationConverter.convertLLToXY(126.7077f, 37.4073f)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun createLocationRequest() {
        locationRequest = LocationRequest.Builder(50000)
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

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        requestingLocationUpdates = false
    }

    private fun updateValuesFromBundle(savedInstanceState: Bundle?) {
        savedInstanceState ?: return

        if(savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
            requestingLocationUpdates = savedInstanceState.getBoolean(
                REQUESTING_LOCATION_UPDATES_KEY
            )
        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, requestingLocationUpdates)
        super.onSaveInstanceState(outState)
    }
    companion object {
        private const val TAG = "MainActivity"
        private const val REQUESTING_LOCATION_UPDATES_KEY = "LOCATION_KEY"
    }
}