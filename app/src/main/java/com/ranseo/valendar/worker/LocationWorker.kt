package com.ranseo.valendar.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject


class LocationWorker @AssistedInject constructor(
    @Assisted appContext: Context, @Assisted params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {


        return Result.success()
    }
}

