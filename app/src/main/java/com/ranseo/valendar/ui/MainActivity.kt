package com.ranseo.valendar.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
//import android.location.Geocoder.GeocodeListener
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.ranseo.valendar.data.Event
import com.ranseo.valendar.databinding.ActivityMainBinding
import com.ranseo.valendar.ui.adapter.CalendarEventAdapter
import com.ranseo.valendar.util.FcstBaseTime
import com.ranseo.valendar.util.LocationConverter
import com.ranseo.valendar.util.Log
import com.ranseo.valendar.util.LogTag
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.Exception

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    @Inject lateinit var calendarEventAdapter: CalendarEventAdapter

    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var bottomWeatherSheet: ConstraintLayout
    private lateinit var sheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>

    private var requestingLocationUpdates: Boolean = false
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private lateinit var locationConverter: LocationConverter
    private lateinit var geoCoder: Geocoder

    @SuppressLint("MissingPermission", "NewApi", "SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateValuesFromBundle(savedInstanceState)

        binding.viewModel = mainViewModel
        binding.lifecycleOwner = this

        calendarEventAdapter.setOnClickListener(
            object : OnClickListener {
                override fun onClick() {
                    Log.log(TAG, "calendarEventAdapter.setOnClickListener() : Success", LogTag.I)
                }

                override fun onLongClick() : Boolean{
                    return true
                }

            }
        )


        bottomWeatherSheet = binding.layoutBottomSheet.layoutBottomSheet
        sheetBehavior = BottomSheetBehavior.from(bottomWeatherSheet)

        sheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (slideOffset == 0.5f)
                    sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        })



        with(binding) {
            layoutCalendar.apply {
                setOnDateChangeListener { _, year, month, day ->
                    val baseDate = "%d%02d%02d".format(year, month + 1, day)
                    val (start,end) = FcstBaseTime.getFcstRange(baseDate)
                    Log.log(TAG, "layoutCalendar start = ${start}, end = ${end}",LogTag.I)
                    mainViewModel.getCalendarEvent(start, end)
                    showWeatherSheet()
                }
            }

            layoutBottomSheet.recEvent.adapter = calendarEventAdapter


        }


        with(mainViewModel) {
            gridLocation.observe(this@MainActivity, gridLocationObserver())
            dataSyncBtn.observe(this@MainActivity, dataSyncBtnObserver())
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.calendarEvents.collect{list ->
                    Log.log(TAG, "mainViewModel.calendarEvents list = ${list}", LogTag.I)
                    calendarEventAdapter.submitList(list)
                }
            }
        }

        registerAcitivityResultLauncher()

        requestAlarmPermission()
        requestCalendarPermission()
        requestLocationPermission()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()
        locationConverter = LocationConverter()
        geoCoder = Geocoder(this, Locale.KOREA)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onResume() {
        super.onResume()
        if (!requestingLocationUpdates) startLocationUpdate()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }


    /**
     * 현재 위치 좌표가 업데이트 되면 'PeriodicWorkRequest<WeatherWorker>()' 시작.
     * */
    private fun gridLocationObserver() =
        Observer<Pair<String, String>> {
            mainViewModel.requestWeatherInfo(it)

        }

    /**
     * BottomSheetLayout 에서 '데이터 동기화' 버튼을 누르면 트리거
     * */
    private fun dataSyncBtnObserver() =
        Observer<Event<Any?>> {
            it.getContentIfNotHandled()?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestCalendarPermission()
                }
            }
        }


    /**
     * 마지막으로 찍혔던 위치 요청
     * */
    private fun getLastLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            try {
                val lon = location.longitude //경도
                val lat = location.latitude //위도
                getAddress(lat, lon)
                Log.log(
                    TAG,
                    "lastLocation() : ${location.toString()}, 위도 : ${lat}, 경도 : ${lon}",
                    LogTag.I
                )
                val p: Pair<String, String> =
                    locationConverter.convertLLToXY(lon.toFloat(), lat.toFloat())
                mainViewModel.setGridLocation(p)

            } catch (error: NullPointerException) {

            }
        }.addOnFailureListener {
            Log.log(
                TAG,
                "lastLocation() failure ${it.message}",
                LogTag.I
            )
        }
    }

    /**
     * 위치 요청 설정
     * */
    @RequiresApi(Build.VERSION_CODES.S)
    fun createLocationRequest() {
        locationRequest = LocationRequest.Builder(10 * 60 * 1000)
            .setMaxUpdateDelayMillis(60 * 60 * 1000)
            .setPriority(android.location.LocationRequest.QUALITY_LOW_POWER)
            .build()

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener { locationSettingResponse ->
            Log.log(
                TAG,
                "task.addOnSuccessListener : ${locationSettingResponse.locationSettingsStates}",
                LogTag.I
            )

        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    Log.log(TAG, "task failure : ${exception.message}", LogTag.I)
//                    exception.startResolutionForResult(this@MainActivity,
//                    REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.log(TAG, "task failure : ${exception.message}", LogTag.I)
                }
            }
        }
    }

    /**
     * 위치 업데이트 요청
     * */
    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("MissingPermission")
    private fun startLocationUpdate() {

        locationCallback = object : LocationCallback() {
            @SuppressLint("NewApi")
            override fun onLocationResult(p0: LocationResult) {
                for (location in p0.locations) {
                    val lon = location.longitude //경도
                    val lat = location.latitude //위도
                    getAddress(lat, lon)
                    Log.log(
                        TAG,
                        "LocationCallback() : ${location.toString()}, 위도 : ${lat}, 경도 : ${lon}",
                        LogTag.I
                    )
                    val p: Pair<String, String> =
                        locationConverter.convertLLToXY(lon.toFloat(), lat.toFloat())
                    mainViewModel.setGridLocation(p)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        ).addOnSuccessListener {
            Log.log(TAG, "requestLocationUpdates() success", LogTag.I)
            requestingLocationUpdates = true
        }.addOnFailureListener {
            Log.log(TAG, "requestLocationUpdates() Failure", LogTag.I)
            requestingLocationUpdates = false
        }
    }

    /**
     * 위치 좌표를 기준으로 "geoCoder"를 이용하여 주소를 알아내는 함수.
     *
     * argument
     * [lat: Double] : 위도 좌표
     * [lon: Double] : 경도 좌표
     *
     * 산출된 현재 주소(addr)는 mainViewModel의 setAddress() func의 인자로 전달된다.
     * */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun getAddress(lat: Double, lon: Double) {
        try {
            val a = geoCoder.getFromLocation(lat, lon, 1)
            a?.let {
                if (a.isNotEmpty()) {
                    val addr = a[0].getAddressLine(0)
                    mainViewModel.setAddress(addr)
                }
            }

        } catch (error: Exception) {
            Log.log(TAG, "getAddress() Failure ${error.message}}", LogTag.I)
        }
    }


    private fun showWeatherSheet() {
        sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

//    private fun getWeatherInfo(baseDate: Int) {
//        mainViewModel.getWeather(baseDate)
//    }


    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        requestingLocationUpdates = false
    }

    private fun updateValuesFromBundle(savedInstanceState: Bundle?) {
        savedInstanceState ?: return

        if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
            requestingLocationUpdates = savedInstanceState.getBoolean(
                REQUESTING_LOCATION_UPDATES_KEY
            )
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, requestingLocationUpdates)
        super.onSaveInstanceState(outState)
    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun permissionActivityResultCallback(): (permissions: Map<String, Boolean>) -> Unit =
        { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    createLocationRequest()
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    createLocationRequest()
                }
                permissions.getOrDefault(Manifest.permission.READ_CALENDAR, false) -> {

                }
                permissions.getOrDefault(Manifest.permission.WRITE_CALENDAR, false) -> {

                }
                else -> {

                }


            }
        }

    @RequiresApi(Build.VERSION_CODES.N)
    fun registerAcitivityResultLauncher() {
        requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions(),
                permissionActivityResultCallback()
            )
    }



    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
                    ||
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED -> {
                createLocationRequest()
            }

            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) ||
                    shouldShowRequestPermissionRationale(
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) -> {

            }

            else -> requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                )
            )
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestCalendarPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CALENDAR
            ) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_CALENDAR
            ) == PackageManager.PERMISSION_GRANTED -> {

                //mainViewModel.writeCalendarEvent()
            }

            shouldShowRequestPermissionRationale(Manifest.permission.READ_CALENDAR) || shouldShowRequestPermissionRationale(
                Manifest.permission.WRITE_CALENDAR
            ) -> {

            }

            else -> {
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.READ_CALENDAR,
                        Manifest.permission.WRITE_CALENDAR
                    )
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestAlarmPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {}

            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) ->{}

            else -> requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            )
        }
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val REQUESTING_LOCATION_UPDATES_KEY = "LOCATION_KEY"
    }
}