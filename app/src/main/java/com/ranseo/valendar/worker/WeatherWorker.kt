package com.ranseo.valendar.worker

import android.app.ForegroundServiceStartNotAllowedException
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.ranseo.valendar.WORKER_KEY_GRID
import com.ranseo.valendar.data.repo.WeatherRepository
import com.ranseo.valendar.util.FcstBaseTime
import com.ranseo.valendar.util.Log
import com.ranseo.valendar.util.LogTag
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Date
import java.text.SimpleDateFormat
import com.ranseo.valendar.R
import com.ranseo.valendar.WORKER_KEY_GRID_X
import com.ranseo.valendar.WORKER_KEY_GRID_Y

@HiltWorker
class WeatherWorker @AssistedInject constructor
    (@Assisted appContext:Context, @Assisted params: WorkerParameters, private val weatherRepository: WeatherRepository)
    : CoroutineWorker(appContext, params) {


    private val notificationManager =
        appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val notificationId = 24

    @RequiresApi(Build.VERSION_CODES.S)
    override suspend fun doWork(): Result = withContext(Dispatchers.IO){
        try {
            //inputData.keyValueMap[WORKER_KEY_GRID] as Pair<String,String>? ?: return Result.failure()

            val nx : String  =inputData.getString(WORKER_KEY_GRID_X) ?: "1"
            val ny : String = inputData.getString(WORKER_KEY_GRID_Y) ?: "1"

            val nowDate = Date(System.currentTimeMillis())
            val baseDate = SimpleDateFormat("yyyyMMdd").format(nowDate)
            val baseTime = FcstBaseTime.getFcstBaseTime(SimpleDateFormat("kkmm").format(nowDate))


            weatherRepository.getWeatherRemoteModel(14,1, baseDate, baseTime, nx , ny)

            val progress = "일기 예보 읽는 중"
            //setForeground(createForegroundInfo(progress))

            notificationManager.notify(notificationId, createNotification(progress))

            Log.log(TAG, "doWork() Success", LogTag.I)
            val outputData : Data = workDataOf(WORKER_KEY_GRID_X to "SUCCESS")
            Result.success(outputData)
        } catch (error:Exception) {
            Log.log(TAG, "doWork() failure : ${error.message}", LogTag.I)
            Result.failure()
        } catch (error:ForegroundServiceStartNotAllowedException) {
            Log.log(TAG, "setForeground() error :${error.message}", LogTag.I)
            Result.retry()
        }
    }

    private fun createNotification(progress:String) : Notification {
        val id = applicationContext.getString(R.string.notification_channel_id)
        val title = applicationContext.getString(R.string.notification_title)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(id)
        }

        return NotificationCompat.Builder(applicationContext, id)
            .setContentTitle(title)
            .setTicker(title)
            .setContentText(progress)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(false)
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
            NotificationManager.IMPORTANCE_HIGH
        ).let {
            it.description = "일기 예보 알림 서비스"
            it.enableLights(true)
            it.lightColor= Color.BLUE
            it.enableVibration(true)
            it.vibrationPattern = longArrayOf(100,200,100,200,100,200)
            notificationManager.createNotificationChannel(it)
        }
    }
    companion object {
        private const val TAG = "WeatherWorker"
    }

}