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

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE 'calendar_event_table' ('event_id' INTEGER PRIMARY KEY NOT NULL, 'd_t_start' INTEGER NOT NULL, 'd_t_end' INTEGER NOT NULL, 'title' TEXT NOT NULL, 'description' TEXT NOT NULL, 'time_zone' TEXT NOT NULL, 'base_time' INTEGER NOT NULL, 'cal_id' INTEGER NOT NULL)")


        }
    }

    //Primary key 변동 시  Mirgration 하는 법
    //Create a new room entity(SQLite table).
    //Copy all the data from the old room entity(SQLite table) into the new room entity(SQLite table).
    //Drop the old room entity.
    //Rename the new room entity(tableName) to the older room entity that has been dropped.
    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE 'new_weather_table' ('base_date' INTEGER NOT NULL," +
                    "'base_time' INTEGER NOT NULL," +
                    "'category' TEXT NOT NULL," +
                    "'fcst_date' INTEGER NOT NULL," +
                    "'fcst_time' INTEGER NOT NULL," +
                    "'fcst_value' TEXT NOT NULL," +
                    "'nx' INTEGER NOT NULL," +
                    "'ny' INTEGER NOT NULL," +
                    "CONSTRAINT primaryKeys PRIMARY KEY ('base_date', 'base_time', 'category'))")

            database.execSQL("INSERT INTO new_weather_table (base_date," +
                    "base_time," +
                    "category," +
                    "fcst_date," +
                    "fcst_time," +
                    "fcst_value," +
                    "nx," +
                    "ny)" +
                    "SELECT base_date, base_time, category, fcst_date, fcst_time, fcst_value, nx, ny FROM weather_table")

            database.execSQL("DROP TABLE weather_table")
            database.execSQL("ALTER TABLE new_weather_table RENAME TO weather_table")
        }
    }

//    private val MIGRATION_3_4 = object : Migration(3, 4) {
//        override fun migrate(database: SupportSQLiteDatabase) {
//            database.execSQL("DROP TABLE IF EXISTS 'calendar_event_table'")
//            database.execSQL("CREATE TABLE 'calendar_event_table' ('event_id' INTEGER PRIMARY KEY NOT NULL, 'd_t_start' INTEGER NOT NULL, 'd_t_end' INTEGER NOT NULL, 'title' TEXT NOT NULL, 'description' TEXT NOT NULL, 'time_zone' TEXT NOT NULL, 'base_time' INTEGER NOT NULL, 'cal_id' INTEGER NOT NULL)")
//        }
//    }


    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): ValendarDatabase {
        return Room.databaseBuilder(
            context,
            ValendarDatabase::class.java,
            "valendar_database"
        )
            .addMigrations(MIGRATION_1_2)
            .addMigrations(MIGRATION_2_3)
//            .addMigrations(MIGRATION_3_4)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideValendarDao(database: ValendarDatabase): ValendarDao = database.getValendarDao()
}