package com.ranseo.valendar.worker

import android.app.ForegroundServiceStartNotAllowedException
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.ranseo.valendar.R
import com.ranseo.valendar.WORKER_KEY_GRID_X
import com.ranseo.valendar.WORKER_KEY_GRID_Y
import com.ranseo.valendar.data.model.business.CalendarEventLocalModel
import com.ranseo.valendar.data.repo.WeatherAndCalendarEventRepository
import com.ranseo.valendar.util.FcstBaseTime
import com.ranseo.valendar.util.Log
import com.ranseo.valendar.util.LogTag
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Date
import java.text.SimpleDateFormat
import com.ranseo.valendar.data.model.Result as MyResult

@HiltWorker
class WeatherWorker @AssistedInject constructor
    (@Assisted appContext:Context, @Assisted params: WorkerParameters, private val weatherAndCalendarEventRepository: WeatherAndCalendarEventRepository)
    : CoroutineWorker(appContext, params) {


    private val notificationManager =
        appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val notificationId = 24

    @RequiresApi(Build.VERSION_CODES.S)
    override suspend fun doWork(): Result = withContext(Dispatchers.IO){
        try {
            //inputData.keyValueMap[WORKER_KEY_GRID] as Pair<String,String>? ?: return Result.failure()
            //notificationManager.notify(notificationId, createNotification("일기 예보 요청중"))
            Log.log(TAG, "doWork() Success", LogTag.I)
            val nx : String  =inputData.getString(WORKER_KEY_GRID_X) ?: "1"
            val ny : String = inputData.getString(WORKER_KEY_GRID_Y) ?: "1"

            val nowDate = Date(System.currentTimeMillis())
            val baseDate = SimpleDateFormat("yyyyMMdd").format(nowDate)
            val baseTime = FcstBaseTime.getFcstBaseTime(SimpleDateFormat("kkmm").format(nowDate))


            when(val res= weatherAndCalendarEventRepository.getWeatherRemoteAndInsertWeatherLocalAndThenInsertCalendarEventBusiness(
                14,
                1,
                baseDate,
                baseTime,
                nx,
                ny)) {
                is MyResult.Success<CalendarEventLocalModel> -> {
                    val result = "일기 예보 요청 결과" +
                            "\n${res.data.baseTime}\n${res.data.description}"

                    //setForeground(createForegroundInfo(result))
                    notificationManager.notify(notificationId, createNotification(result))
                    Log.log(TAG, "notify() Success : ${res.data}", LogTag.I)
                }

                is MyResult.Error -> {
                    val tmp = "일기 예보 요청 중"
                    notificationManager.notify(notificationId, createNotification(tmp))
                    Log.log(TAG, "notify() failure : ${res.exception}", LogTag.I)
                }
            }

            Log.log(TAG, "doWork() Success", LogTag.I)
            val outputData : Data = workDataOf(WORKER_KEY_GRID_X to "SUCCESS")
            Result.success(outputData)
        } catch (error:Exception) {
            Log.log(TAG, "doWork() failure : ${error.message}", LogTag.I)
            Result.failure()
        } catch (error:ForegroundServiceStartNotAllowedException) {
            Log.log(TAG, "setForeground() error :${error.message}", LogTag.I)
            Result.failure()
        }
    }

    private fun createNotification(content:String) : Notification {
        val id = applicationContext.getString(R.string.notification_channel_id)
        val title = applicationContext.getString(R.string.notification_title)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(id)
        }

        return NotificationCompat.Builder(applicationContext, id)
            .setContentTitle(title)
            .setTicker(title)
            .setContentText("오늘 날씨")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(content))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(false)
            .setVibrate(longArrayOf(0))
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .build()
    }

    private fun createForegroundInfo(progress: String) : ForegroundInfo {
        val notification = createNotification(progress)
        return ForegroundInfo(notificationId, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createChannel(notificationChannelId:String) {
        NotificationChannel(
            notificationChannelId,
            "일기 예보 알림",
            NotificationManager.IMPORTANCE_MIN
        ).let {
            it.description = "일기 예보 알림 서비스"
            it.enableLights(true)
            it.lightColor= Color.BLUE
            it.enableVibration(false)
            it.vibrationPattern = longArrayOf(0)
            notificationManager.createNotificationChannel(it)
        }
    }
    companion object {
        private const val TAG = "WeatherWorker"
    }

}