package com.arj4ng.hourbit.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.os.Build
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

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


// helper class that provides device location
class LocationProvider(
    private val context: Context
) {
    private val fusedLocationClient = // client used to access device location
        LocationServices.getFusedLocationProviderClient(context) // android/google client used to access device location
    @SuppressLint("MissingPermission") // permission is checked before calling this function

    // get location asynchronously without blocking the UI
    suspend fun getCurrentLocation(): Pair<Double, Double>? { // return latitude + longitude or null if no location is available

        // first try the last known location
        val lastLocation = fusedLocationClient.lastLocation.await()

        if (lastLocation != null) {
            return Pair(
                lastLocation.latitude,
                lastLocation.longitude
            )
        }

        val location = fusedLocationClient // requests a fresh device location instead of only using cached location
            .getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            )
            .await()
            ?: return null // wait for the last known location. exit if none exists

        // return the coordinates as latitude and longitude
        return Pair(
            location.latitude,
            location.longitude
        )
    }

    suspend fun getCityName(
        latitude: Double,
        longitude: Double
    ): String? {

        val geocoder =
            Geocoder(
                context,
                Locale.getDefault()
            )

        return try {

            if (
                Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.TIRAMISU
            ) {

                suspendCancellableCoroutine { continuation ->

                    geocoder.getFromLocation(
                        latitude,
                        longitude,
                        1
                    ) { addresses ->

                        val address =
                            addresses.firstOrNull()

                        continuation.resume(
                            address?.locality
                                ?: address?.subAdminArea
                                ?: address?.adminArea
                        )
                    }
                }

            } else {

                withContext(Dispatchers.IO) {

                    @Suppress("DEPRECATION")
                    val address =
                        geocoder.getFromLocation(
                            latitude,
                            longitude,
                            1
                        )?.firstOrNull()

                    address?.locality
                        ?: address?.subAdminArea
                        ?: address?.adminArea
                }
            }

        } catch (e: Exception) {
            null
        }
    }

}