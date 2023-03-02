package com.ranseo.valendar.room

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.impl.Migration_1_2
import com.ranseo.valendar.data.model.business.CalendarEventLocalModel
import com.ranseo.valendar.data.model.business.WeatherLocalModel


@Database(entities = [WeatherLocalModel::class, CalendarEventLocalModel::class], version = 2, exportSchema = true, autoMigrations = [])
abstract class ValendarDatabase : RoomDatabase() {
    abstract fun getValendarDao() : ValendarDao

    val Migration_1_2 = object: Migration(1,2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE 'calendar_event_table' ('event_id' INTEGER, 'd_t_start' INTEGER, 'd_t_end' INTEGER, 'title' TEXT, 'description' TEXT, 'time_zone' TEXT, 'cad_id' INTEGER, PRIMARY KEY('event_id'))")
        }

    }
}
