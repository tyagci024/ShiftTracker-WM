package com.example.shifttracker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.shifttracker.model.TimeValue
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.commons.net.ntp.NTPUDPClient
import org.apache.commons.net.ntp.TimeInfo
import java.net.InetAddress
import java.text.SimpleDateFormat
import java.util.Date

class LocationMonitoringService(val appContext:Context, workerParameters: WorkerParameters):Worker(appContext,workerParameters) {
    private lateinit var auth: FirebaseAuth
    private var userLocation: android.location.Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient



    override fun doWork(): Result {
        // Kullanıcının mevcut konumunu al

     getUserLocationControl()

        return Result.success()
    }

    private fun getUserLocationControl() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(appContext)
        if (ContextCompat.checkSelfPermission(
                appContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: android.location.Location? ->
                    userLocation = location
                    val desiredLatitude = 37.8504236
                    val desiredLongitude = 27.8421963 //,

                    // Kullanıcı konumu alındıysa ve istenilen konuma uzaksa
                    if (userLocation != null && isLocationCloseEnough(desiredLatitude, desiredLongitude)){
                        logOutFirestore()

                    }


                }
        }
    }
    private fun isLocationCloseEnough(desiredLatitude: Double, desiredLongitude: Double): Boolean {
        val tolerance = 1.6// Uygun bir tolerans değeri belirleyin
        return userLocation?.let { location ->
            (Math.abs(desiredLatitude - location.latitude) > tolerance) &&
                    (Math.abs(desiredLongitude - location.longitude) > tolerance)
        } ?: false
    }

    fun logOutFirestore() {
        auth = FirebaseAuth.getInstance()

        CoroutineScope(Dispatchers.IO).launch {
            val time = getNtpTime()
            val date = Date(time)
            val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
            val formattedDate = dateFormat.format(date)

            println("Formatlanmış tarih: $formattedDate")

            val db = FirebaseFirestore.getInstance()
            val guncelZaman = formattedDate
            val user = auth.currentUser
            val email = user?.email ?: ""

            val userCollectionRef = db.collection(email)
            userCollectionRef.add(TimeValue(guncelZaman.toString(),"çıkış",System.currentTimeMillis()))
                .addOnSuccessListener { documentReference ->
                    println("Firestore'a eklendi")
                }
                .addOnFailureListener { e ->
                    println("Firestore'a eklenirken hata oluştu: $e")
                }
        }
    }

    private suspend fun getNtpTime(): Long {
        return withContext(Dispatchers.IO) {
            // Ağ işlemleri burada yapılır
            try {
                val client = NTPUDPClient()
                val address = InetAddress.getByName("pool.ntp.org")
                val info: TimeInfo = client.getTime(address)
                info.computeDetails()
                info.message.receiveTimeStamp.time
            } catch (e: Exception) {
                e.printStackTrace()
                0L
            }
        }
    }

}