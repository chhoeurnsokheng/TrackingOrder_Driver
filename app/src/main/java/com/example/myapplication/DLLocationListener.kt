package com.example.myapplication

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*

//class DLLocationListener : Service(), LocationListener {
//
//    private val TAG = "DL_LOCATION"
//
//    private var location: Location? = null
//
//    private var locationRequest: LocationRequest? = null
//
//
//    private var dbRef: DatabaseReference? = null
//
//
//    override fun onBind(p0: Intent?): IBinder? {
//        return null
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//
//        dbRef = FirebaseDatabase.getInstance().reference
//
//        if (Build.VERSION.SDK_INT >= 26) {
//            val CHANNEL_ID = "smt_location"
//            val channel = NotificationChannel(
//                CHANNEL_ID,
//                getString(R.string.app_name) + " using your location",
//                NotificationManager.IMPORTANCE_DEFAULT
//            )
//            (Objects.requireNonNull(getSystemService(Context.NOTIFICATION_SERVICE)) as NotificationManager).createNotificationChannel(
//                channel
//            )
//            val notification =
//                NotificationCompat.Builder(this, CHANNEL_ID)
//                    .setContentTitle("")
//                    .setContentText("").build()
//            startForeground(1, notification)
//        }
//    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        startLocationUpdate()
//        return super.onStartCommand(intent, flags, startId)
//    }
//
//
//    private fun startLocationUpdate() {
//        locationRequest = LocationRequest()
//        locationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//        locationRequest?.interval = 5000
//        locationRequest?.fastestInterval = 2500
//
//        val builder = LocationSettingsRequest.Builder()
//        locationRequest?.let { locReq ->
//            builder.addLocationRequest(locReq)
//            val locationSettingRequest = builder.build()
//
//            val locationSetting = LocationServices.getSettingsClient(this)
//
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(
//                    this,
//                    Manifest.permission.ACCESS_COARSE_LOCATION
//                )
//                == PackageManager.PERMISSION_GRANTED
//            ) {
//                getFusedLocationProviderClient(this).requestLocationUpdates(
//                    locReq,
//                    object : LocationCallback() {
//                        override fun onLocationResult(p0: LocationResult?) {
//                            p0?.lastLocation?.let { lastLocation ->
//                                onLocationChanged(lastLocation)
//                            }
//                        }
//                    },
//                    Looper.getMainLooper()
//                )
//            }
//        }
//
//    }
//
//    override fun onLocationChanged(p0: Location) {
//        Log.d(TAG, "onLocationChanged:${p0.latitude}, ${p0.longitude}")
//        val myLocation = MyLocation(latitude = p0.latitude, longitude = p0.longitude)
//        dbRef?.child("driver_points")?.setValue(myLocation)
//    }
//
//    class MyLocation(latitude: Double, longitude: Double) {
//
//    }
 //}