package com.arj4ng.hourbit.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

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


// define the weather API request used by Retrofit
interface WeatherApi { // describe the HTTP request Retrofit can make
    @GET("v1/forecast") // send a GET request to the forecast endpoint

    suspend fun getCurrentWeather( // async weather request

        // add latitude and longitude as URL query parameters
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,

        // request only the current temperature and weather code
        @Query("current") current: String = "temperature_2m,weather_code"

    ): WeatherResponse // retrofit convert the JSON response into WeatherResponse
}