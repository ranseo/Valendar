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
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.ranseo.valendar.FRAGMENT_KEY_CALENDAR_EVENT
import com.ranseo.valendar.data.Event
import com.ranseo.valendar.databinding.ActivityMainBinding
import com.ranseo.valendar.ui.adapter.CalendarEventAdapter
import com.ranseo.valendar.ui.fragment.CalendarEventFragment
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
import com.ranseo.valendar.R
import com.ranseo.valendar.data.model.ui.CalendarEventUIState

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
            object : OnClickListener<CalendarEventUIState> {
                override fun <T> onClick(p0: T) {
                    
                    Log.log(TAG, "calendarEventAdapter.setOnClickListener() : ${p0}", LogTag.I)
                    when(p0) {
                        is CalendarEventUIState -> {
                            addFragment(p0)
                        }
                        else -> {
                        }
                    }
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
     * CalenarEventFragment commit
     * */
    private fun addFragment(calendarEvent:CalendarEventUIState) {
        val fragment = CalendarEventFragment()
        fragment.arguments = bundleOf(FRAGMENT_KEY_CALENDAR_EVENT to calendarEvent)
        supportFragmentManager.beginTransaction()
            .add(R.id.layout_calendar_event, fragment,TAG_FRAGMENT_CALENDAR_EVENT)
            .addToBackStack(TAG)
            .commit()


    }




    /**
     * ?????? ?????? ????????? ???????????? ?????? 'PeriodicWorkRequest<WeatherWorker>()' ??????.
     * */
    private fun gridLocationObserver() =
        Observer<Pair<String, String>> {
            mainViewModel.requestWeatherInfo(it)

        }

    /**
     * BottomSheetLayout ?????? '????????? ?????????' ????????? ????????? ?????????
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
     * ??????????????? ????????? ?????? ??????
     * */
    private fun getLastLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            try {
                val lon = location.longitude //??????
                val lat = location.latitude //??????
                getAddress(lat, lon)
                Log.log(
                    TAG,
                    "lastLocation() : ${location.toString()}, ?????? : ${lat}, ?????? : ${lon}",
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
     * ?????? ?????? ??????
     * */
    @RequiresApi(Build.VERSION_CODES.S)
    fun createLocationRequest() {
        locationRequest = LocationRequest.Builder(60 * 1000)
            .setMaxUpdateDelayMillis(60 * 1000)
            .setPriority(android.location.LocationRequest.QUALITY_BALANCED_POWER_ACCURACY)
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
     * ?????? ???????????? ??????
     * */
    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("MissingPermission")
    private fun startLocationUpdate() {

        locationCallback = object : LocationCallback() {
            @SuppressLint("NewApi")
            override fun onLocationResult(p0: LocationResult) {
                for (location in p0.locations) {
                    val lon = location.longitude //??????
                    val lat = location.latitude //??????
                    getAddress(lat, lon)
                    Log.log(
                        TAG,
                        "LocationCallback() : ${location.toString()}, ?????? : ${lat}, ?????? : ${lon}",
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
     * ?????? ????????? ???????????? "geoCoder"??? ???????????? ????????? ???????????? ??????.
     *
     * argument
     * [lat: Double] : ?????? ??????
     * [lon: Double] : ?????? ??????
     *
     * ????????? ?????? ??????(addr)??? mainViewModel??? setAddress() func??? ????????? ????????????.
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
                    //createLocationRequest()
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    //createLocationRequest()
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
        private const val TAG_FRAGMENT_CALENDAR_EVENT = "FragmentTagCalendarEvent"
    }
}