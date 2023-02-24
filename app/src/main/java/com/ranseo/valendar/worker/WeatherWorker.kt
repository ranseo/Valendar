package com.ranseo.valendar.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.ranseo.valendar.WORKER_KEY_GRID
import com.ranseo.valendar.data.repo.WeatherRepository
import com.ranseo.valendar.util.FcstBaseTime
import com.ranseo.valendar.util.Log
import com.ranseo.valendar.util.LogTag
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.sql.Date
import java.text.SimpleDateFormat
import javax.inject.Inject

@HiltWorker
class WeatherWorker @AssistedInject constructor
    (@Assisted appContext:Context, @Assisted params: WorkerParameters, private val weatherRepository: WeatherRepository)
    : CoroutineWorker(appContext, params) {



    override suspend fun doWork(): Result {
        return try {
            //inputData.keyValueMap[WORKER_KEY_GRID] as Pair<String,String>? ?: return Result.failure()

            val (nx : String ,ny : String) =inputData.getString(WORKER_KEY_GRID)?.split("//") ?: listOf("1","1")
            val nowDate = Date(System.currentTimeMillis())
            val baseDate = SimpleDateFormat("yyyyMMdd").format(nowDate)
            val baseTime = FcstBaseTime.getFcstBaseTime(SimpleDateFormat("kkmm").format(nowDate))
            weatherRepository.getWeatherRemoteModel(14,1, baseDate, baseTime, nx , ny)

            Log.log(TAG, "doWork() Success", LogTag.I)
            val outputData : Data = workDataOf(WORKER_KEY_GRID to "SUCCESS")
            Result.success(outputData)
        } catch (error:Exception) {
            Log.log(TAG, "doWork() failure : ${error.message}", LogTag.I)
            Result.failure()
        }
    }

    companion object {
        private const val TAG = "WeatherWorker"
    }

}