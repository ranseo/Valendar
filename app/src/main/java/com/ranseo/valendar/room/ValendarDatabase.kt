package com.ranseo.valendar.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ranseo.valendar.data.model.business.WeatherLocalModel


@Database(entities = [WeatherLocalModel::class], version = 1, exportSchema = true)
abstract class ValendarDatabase : RoomDatabase() {
    abstract fun getValendarDao() : ValendarDao
}
