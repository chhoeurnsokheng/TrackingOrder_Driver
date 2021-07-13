package com.example.myapplication

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Math.*

class MainActivity : AppCompatActivity() {

    private var mapFragment: SupportMapFragment? = null

    private var googleMap: GoogleMap? = null

    private val TAG: String = "MAIN_ACTIVITY"


    private var driverMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        mapFragment = supportFragmentManager.findFragmentById(R.id.google_map_order_track) as SupportMapFragment?

        mapFragment?.getMapAsync { googleMap ->
            this.googleMap = googleMap
            listenLocationOfDriver()
        }
    }

    private fun listenLocationOfDriver() {
        val dbRef = FirebaseDatabase.getInstance().reference
        dbRef.child("driver_points").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, "onDataChange: $snapshot")

                val value = snapshot.value as HashMap<*, *>

                val latitude = (value["latitude"] ?: 0.0).toString().toDouble()
                val longitude = (value["longitude"] ?: 0.0).toString().toDouble()

                val latLng = LatLng(latitude, longitude)

                googleMap?.let { gMap ->
                    if (driverMarker == null) {
                        driverMarker = gMap.addMarker(
                            MarkerOptions()
                                .position(latLng)
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_driver))
                                .anchor(0.5f, 0.5f)
                                .flat(true)
                        )
                    } else {
                        driverMarker?.position?.let { oldPosition ->
                            val bearing = bearingBetweenLocations(oldPosition, latLng)
                            rotateMarker(driverMarker!!, bearing.toFloat())
                            animateMarker(gMap, driverMarker!!, latLng, false)
                        }

                    }

                    gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0f))
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    fun bearingBetweenLocations(latLng1: LatLng, latLng2: LatLng): Double {
        val lat1 = latLng1.latitude
        val lng1 = latLng1.longitude
        val lat2 = latLng2.latitude
        val lng2 = latLng2.longitude
        val fLat: Double = degreeToRadians(lat1)
        val fLong: Double = degreeToRadians(lng1)
        val tLat: Double = degreeToRadians(lat2)
        val tLong: Double = degreeToRadians(lng2)
        val dLon = tLong - fLong
        val degree: Double = radiansToDegree(
            atan2(
                sin(dLon) * cos(tLat),
                cos(fLat) * sin(tLat) - sin(fLat) * cos(tLat) * cos(dLon)
            )
        )
        return if (degree >= 0) {
            degree
        } else {
            360 + degree
        }
    }

    private fun degreeToRadians(latLong: Double): Double {
        return Math.PI * latLong / 180.0
    }

    private fun radiansToDegree(latLong: Double): Double {
        return latLong * 180.0 / Math.PI
    }

    internal var isMarkerRotating = false
    fun rotateMarker(marker: Marker, toRotation: Float) {
        if (!isMarkerRotating) {
            val handler = Handler(Looper.getMainLooper())
            val start = SystemClock.uptimeMillis()
            val startRotation = marker.rotation
            val duration: Long = 1000
            val interpolator = LinearInterpolator()
            handler.post(object : Runnable {
                override fun run() {
                    isMarkerRotating = true
                    val elapsed = SystemClock.uptimeMillis() - start
                    val t = interpolator.getInterpolation(elapsed.toFloat() / duration)
                    val rot = t * toRotation + (1 - t) * startRotation
                    marker.rotation = if (-rot > 180) rot / 2 else rot
                    if (t < 1.0) {
                        handler.postDelayed(this, 16)
                    } else {
                        isMarkerRotating = false
                    }
                }
            })
        }
    }


    internal var isDriverMarkerMoving = false
    fun animateMarker(
        googleMap: GoogleMap,
        driverMarker: Marker,
        toPosition: LatLng,
        hideMarker: Boolean
    ) {
        if (!isDriverMarkerMoving) {
            val start = SystemClock.uptimeMillis()
            val proj = googleMap.projection
            val startPoint = proj.toScreenLocation(driverMarker.position)
            val startLatLng = proj.fromScreenLocation(startPoint)
            val duration: Long = 2000

            val interpolator = LinearInterpolator()

            val driverMarkerHandler = Handler(Looper.getMainLooper())
            driverMarkerHandler.post(object : Runnable {
                override fun run() {
                    isDriverMarkerMoving = true
                    val elapsed = SystemClock.uptimeMillis() - start
                    val t = interpolator.getInterpolation(elapsed.toFloat() / duration)
                    val lng = t * toPosition.longitude + (1 - t) * startLatLng.longitude
                    val lat = t * toPosition.latitude + (1 - t) * startLatLng.latitude
                    driverMarker.position = LatLng(lat, lng)

                    if (t < 1.0) {
                        driverMarkerHandler.postDelayed(this, 16)
                    } else {
                        driverMarker.isVisible = !hideMarker
                        isDriverMarkerMoving = false
                    }
                }
            })
        }
    }

}