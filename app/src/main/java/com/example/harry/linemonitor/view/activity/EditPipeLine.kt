package com.example.harry.linemonitor.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.view.MenuItem
import com.example.harry.linemonitor.R
import com.example.harry.linemonitor.data.LineMasterMap
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.tbruyelle.rxpermissions2.RxPermissions

import kotlinx.android.synthetic.main.activity_edit_pipe_line.*
import java.util.ArrayList

class EditPipeLine : AppCompatActivity() , LocationListener, OnMapReadyCallback {
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var mMap: GoogleMap
    private lateinit var locationManager: LocationManager
    private val MIN_TIME: Long = 400.toLong()
    private val MIN_DISTANCE: Float = 1000f
    private lateinit var lineMaster:LineMasterMap
    private lateinit var bounds: LatLngBounds.Builder


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_pipe_line)

        toolbar.title = ""

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val rxPermissions = RxPermissions(this)
        rxPermissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION) // ask single or multiple permission once
                .subscribe { granted ->
                    if (granted!!) {
                        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this); //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER

                    } else {

                    }
                }

        lineMaster = intent.getSerializableExtra("lineData") as LineMasterMap


        et_pipeline_diameter.setText(lineMaster.diameter.toString())
        et_pipeline_end.setText(lineMaster.end.toString())
        et_pipeline_length.setText(lineMaster.distance.toString())
        et_pipeline_manufacture.setText(lineMaster.manufacture.toString())
        et_pipeline_name.setText(lineMaster.name)
        et_pipeline_start.setText(lineMaster.start)
        et_pipeline_thickness.setText(lineMaster.thicknes.toString())
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap!!

        bounds = LatLngBounds.builder()

        bounds.include(LatLng(lineMaster!!.startNodeLat!!.toDouble(), lineMaster!!.startNodeLng!!.toDouble()))
        bounds.include(LatLng(lineMaster!!.endNodeLat!!.toDouble(), lineMaster!!.endNodeLng!!.toDouble()))


        var startNodeMarker = MarkerOptions()
                .position(LatLng(lineMaster!!.startNodeLat!!.toDouble(), lineMaster!!.startNodeLng!!.toDouble()))
                .title(lineMaster!!.startNodeSN)
        var endNodeMarker = MarkerOptions()
                .position(LatLng(lineMaster!!.endNodeLat!!.toDouble(), lineMaster!!.endNodeLng!!.toDouble()))
                .title(lineMaster!!.endNodeSN)


        var nodeList = ArrayList<LatLng>()
        nodeList.add(LatLng(lineMaster!!.startNodeLat!!.toDouble(), lineMaster!!.startNodeLng!!.toDouble()))
        nodeList.add(LatLng(lineMaster!!.endNodeLat!!.toDouble(), lineMaster!!.endNodeLng!!.toDouble()))

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.build().center, 13f))
        mMap.addPolyline(PolylineOptions().addAll(nodeList).width(12f)
                .color(Color.RED))

        mMap.isTrafficEnabled = true
        mMap.isBuildingsEnabled = true



        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {


            mMap.isMyLocationEnabled = true


        } else {

        }
        mMap.addMarker(startNodeMarker)
        mMap.addMarker(endNodeMarker)
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.uiSettings.isScrollGesturesEnabled = false



    }


    override fun onLocationChanged(location: Location?) {

    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    override fun onProviderEnabled(provider: String?) {

    }

    override fun onProviderDisabled(provider: String?) {

    }






    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

}
