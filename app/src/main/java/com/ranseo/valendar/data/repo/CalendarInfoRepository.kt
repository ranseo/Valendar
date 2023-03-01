package com.ranseo.valendar.data.repo

import android.app.Application
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.provider.CalendarContract
import com.ranseo.valendar.data.model.business.CalendarEvent
import com.ranseo.valendar.util.Log
import com.ranseo.valendar.util.LogTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.ranseo.valendar.data.model.Result
import com.ranseo.valendar.data.model.business.CalendarInfo
import java.lang.*

class CalendarInfoRepository @Inject constructor(
    application: Application
) {
    private val contentResolver = application.contentResolver


    suspend fun queryCalendarInfo() : Result<Any> = withContext(Dispatchers.IO){
        val calendar_projection : Array<String> = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.ACCOUNT_NAME,
            CalendarContract.Calendars.ACCOUNT_TYPE,
            CalendarContract.Calendars.OWNER_ACCOUNT,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME
        )

        val uri : Uri = CalendarContract.Calendars.CONTENT_URI

        val cursor : Cursor? =
            contentResolver.query(uri,calendar_projection,null, null, null)

        when(cursor?.count) {
            null -> {
                Result.Error(NullPointerException("cursor is Null"))
            }

            0 -> {
                Result.Success("쿼리 결과, 테이블의 개수가 0 입니다.")
            }

            else -> {
                val calendarInfos = arrayListOf<CalendarInfo>()
                while(cursor.moveToNext()) {
                    val calId: Long = cursor.getLong(PROJECTION_ID_INDEX)
                    val accountName = cursor.getString(PROJECTION_ACCOUNT_NAME_INDEX)
                    val accountType = cursor.getString(PROJECTION_ACCOUNT_TYPE_INDEX)
                    val ownerAccount = cursor.getString(PROJECTION_OWNER_ACCOUNT_INDEX)
                    val displayName = cursor.getString(PROJECTION_DISPLAY_NAME_INDEX)

                    val calendarInfo = CalendarInfo(calId, accountName, accountType, ownerAccount, displayName)
                    calendarInfos.add(calendarInfo)
                }
                Result.Success(calendarInfos)
            }
        }
    }

    companion object {
        private const val TAG = "CalendarInfoRepository"

        private const val PROJECTION_ID_INDEX: Int = 0
        private const val PROJECTION_ACCOUNT_NAME_INDEX: Int = 1
        private const val PROJECTION_ACCOUNT_TYPE_INDEX: Int = 2
        private const val PROJECTION_OWNER_ACCOUNT_INDEX: Int = 3
        private const val PROJECTION_DISPLAY_NAME_INDEX: Int = 4

    }
}