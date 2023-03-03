package com.ranseo.valendar.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ranseo.valendar.room.ValendarDao
import com.ranseo.valendar.room.ValendarDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object RoomDatabaseModule {

    private val MIGRATION_1_2 = object: Migration(1,2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE 'calendar_event_table' ('event_id' INTEGER, 'd_t_start' INTEGER, 'd_t_end' INTEGER, 'title' TEXT, 'description' TEXT, 'time_zone' TEXT, 'base_time' INTEGER, 'cad_id' INTEGER, PRIMARY KEY('event_id'))")
        }
    }

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context:Context
    ) : ValendarDatabase{
        return Room.databaseBuilder(
            context,
            ValendarDatabase::class.java,
            "valendar_database"
        )
            .addMigrations(MIGRATION_1_2)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideValendarDao(database:ValendarDatabase) : ValendarDao = database.getValendarDao()
}