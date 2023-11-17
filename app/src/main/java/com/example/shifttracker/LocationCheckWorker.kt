package com.example.shifttracker

import android.content.Context
import android.location.LocationManager
import android.widget.Toast
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.shifttracker.model.TimeValue
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

class LocationCheckWorker(val appContext : Context, workerParameters: WorkerParameters) : Worker(appContext,workerParameters) {
    private lateinit var auth: FirebaseAuth

    override fun doWork(): Result {

        val locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (!isGpsEnabled){
            shiftLogOuttoFirestore()
        }
        return Result.success()
    }

    fun shiftLogOuttoFirestore() {
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