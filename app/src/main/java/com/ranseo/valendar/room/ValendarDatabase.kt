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


}

