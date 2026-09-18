package com.arj4ng.hourbit.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/*
  ╔════════════════════════════════════════════════════════╗
  ║  █████╗ ██████╗      ██╗ ██╗  ██╗ ███╗   ██╗  ██████╗  ║
  ║ ██╔══██╗██╔══██╗     ██║ ██║  ██║ ████╗  ██║ ██╔════╝  ║
  ║ ███████║██████╔╝     ██║ ███████║ ██╔██╗ ██║ ██║  ███╗ ║
  ║ ██╔══██║██╔══██╗██   ██║ ╚════██║ ██║╚██╗██║ ██║   ██║ ║
  ║ ██║  ██║██║  ██║╚█████╔╝      ██║ ██║ ╚████║ ╚██████╔╝ ║
  ║ ╚═╝  ╚═╝╚═╝  ╚═╝ ╚════╝       ╚═╝ ╚═╝  ╚═══╝  ╚═════╝  ║
  ╚═════════════════════════════════════════ [ DEV TAG ] ══╝
*/


// Creates the Retrofit instance used for weather API requests.
object RetrofitClient {
    private const val BASE_URL = "https://api.open-meteo.com/" // base address used for all weather API requests
    val weatherApi: WeatherApi by lazy { // only gets created the first time you used

        // start configuring the Retrofit client
        Retrofit.Builder()
            .baseUrl(BASE_URL) // API base URL
            .addConverterFactory(GsonConverterFactory.create()) // convert JSON responses into Kotlin data classes
            .build()
            .create(WeatherApi::class.java) // create the actual WeatherApi implementation
    }
}