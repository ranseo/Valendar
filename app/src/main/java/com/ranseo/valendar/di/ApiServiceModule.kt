package com.ranseo.valendar.di

import com.ranseo.valendar.network.NetworkURL
import com.ranseo.valendar.network.WeatherApiService
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ApiServiceModule {

    @Provides
    fun provideBaseUrl() : String = NetworkURL.WEATHER_FORECAST_BASE_URL

    @Provides
    @Singleton
    fun provideRetrofit(moshi: Moshi) : Retrofit {
        return Retrofit.Builder()
            .baseUrl(provideBaseUrl())
            .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
            .build()
    }

    @Provides
    @Singleton
    fun provideWeatherApiService(retrofit: Retrofit) : WeatherApiService = retrofit.create(WeatherApiService::class.java)

}