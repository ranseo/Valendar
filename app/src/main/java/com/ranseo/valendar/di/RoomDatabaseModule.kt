package com.ranseo.valendar.di

import android.content.Context
import androidx.room.Room
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
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideValendarDao(database:ValendarDatabase) : ValendarDao = database.getValendarDao()
}