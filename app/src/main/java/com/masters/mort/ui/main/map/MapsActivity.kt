package com.masters.mort.ui.main.map

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.masters.mort.R
import com.masters.mort.databinding.ActivityMapsBinding
import com.masters.mort.ui.main.MainActivity
import com.masters.mort.utilities.Utils

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object{
        private const val DEFAULT_ZOOM = 15F
        private const val LOCATION_PERMISSION_REQUEST_CODE = 2000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapsInitializer.initialize(this, MapsInitializer.Renderer.LATEST){}
        binding = ActivityMapsBinding.inflate(layoutInflater)
        binding.backButton.setOnClickListener { backwardToMain() }
        setContentView(binding.root)
        try {
            val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        }catch(exception: Exception){
            Toast.makeText(this, "Error while creating maps", Toast.LENGTH_LONG).show()
        }
    }

    private fun backwardToMain() {
        Utils.switchActivity(
            this@MapsActivity,
            MainActivity::class.java,
            true
        )
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        mMap.isMyLocationEnabled = true
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            val userLocation = LatLng(it.latitude, it.longitude)
            mMap.addMarker(MarkerOptions().position(userLocation).title("Your current location!"))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, DEFAULT_ZOOM))
        }
    }
}