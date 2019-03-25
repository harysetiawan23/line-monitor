package com.example.harry.linemonitor.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import com.arlib.floatingsearchview.FloatingSearchView
import com.example.harry.linemonitor.R
import com.example.harry.linemonitor.data.LineMasterMap
import com.example.harry.linemonitor.view.adapter.HorizontalLineAdapter
import com.example.harry.linemonitor.view.contract.LineMapContract
import com.example.harry.linemonitor.view.presenter.LineMapsPresenter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_landing.*
import kotlinx.android.synthetic.main.activity_maps.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.startActivity
import java.util.*

class LandingActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, LineMapContract, HorizontalLineAdapter.OnItemClickListener,
        View.OnClickListener, SeekBar.OnSeekBarChangeListener,
        LocationListener, FloatingSearchView.OnQueryChangeListener {


    private lateinit var linePresenter: LineMapsPresenter
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var listLine: ArrayList<ArrayList<LatLng>>
    private lateinit var listPin: ArrayList<MarkerOptions>
    private lateinit var thisActivity: Activity
    private lateinit var mMap: GoogleMap
    private lateinit var selectedLine: LineMasterMap
    private lateinit var locationManager: LocationManager
    private val MIN_TIME: Long = 400.toLong()
    private val MIN_DISTANCE: Float = 1000f
    private lateinit var myLatLongBounds: LatLngBounds
    private lateinit var myLocation: LatLng
    private lateinit var lineAdapter: HorizontalLineAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)
        toolbar.title = ""
        setSupportActionBar(toolbar)

        thisActivity = this
        val toggle = ActionBarDrawerToggle(thisActivity, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        zoom_seekbar.setOnSeekBarChangeListener(this)
        toolbar.setNavigationIcon(ctx.getDrawable(R.drawable.ic_menu_button_of_three_horizontal_lines))

        linePresenter = LineMapsPresenter(this)
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment


        rv_line_list.layoutManager = LinearLayoutManager(ctx, LinearLayout.HORIZONTAL, false)



        my_location_fab.setOnClickListener { view ->
            var cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLocation, zoom_seekbar.progress.toFloat());
            mMap.animateCamera(cameraUpdate);
        }


        nav_view.setNavigationItemSelectedListener(this)


        floating_search_view.setOnQueryChangeListener(this)

    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        var bounds = LatLngBounds.builder()

        val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.maps_style_white))

        if (!success) {
            Log.e("Maps Style Log", "Style parsing failed.")
        }


        listLine.forEach { lineList: ArrayList<LatLng> ->
            var polyLine = mMap.addPolyline(PolylineOptions().addAll(lineList)
                    .width(12f)
                    .color(Color.RED)
            )

            polyLine.isClickable = true


        }

        listPin.forEach { markerOptions: MarkerOptions ->
            mMap.addMarker(markerOptions)
            bounds.include(markerOptions.position)
        }

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {


            mMap.isMyLocationEnabled = true


        } else {

        }



        mMap.isTrafficEnabled = true
        mMap.isBuildingsEnabled = true

        mMap.uiSettings.isMyLocationButtonEnabled = true

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.build().center, zoom_seekbar.progress.toFloat()))
        myLatLongBounds = bounds.build()
    }


    override fun showLoading() {
        progress_bar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progress_bar.visibility = View.GONE
    }

    @SuppressLint("MissingPermission")
    override fun onSuccess(data: List<LineMasterMap?>?) {
        listLine = ArrayList<ArrayList<LatLng>>()
        listPin = ArrayList<MarkerOptions>()

        lineAdapter = HorizontalLineAdapter(data, this, ctx)
        rv_line_list.setAdapter(lineAdapter)

        data?.forEach { lineData: LineMasterMap? ->
            var line = ArrayList<LatLng>()

            line.add(LatLng(lineData!!.startNodeLat!!.toDouble(), lineData!!.startNodeLng!!.toDouble()))
            line.add(LatLng(lineData!!.endNodeLat!!.toDouble(), lineData!!.endNodeLng!!.toDouble()))
            listLine.add(line)


            var markerOptionStart = MarkerOptions()
                    .position(LatLng(lineData!!.startNodeLat!!.toDouble(), lineData!!.startNodeLng!!.toDouble()))
                    .title(lineData!!.startNodeSN)

            var markerOptionEnd = MarkerOptions()
                    .position(LatLng(lineData!!.endNodeLat!!.toDouble(), lineData!!.endNodeLng!!.toDouble()))
                    .title(lineData!!.endNodeSN)

            listPin.add(markerOptionStart)
            listPin.add(markerOptionEnd)
        }


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



        mapFragment.getMapAsync(this)


        floating_search_view.setOnQueryChangeListener(FloatingSearchView.OnQueryChangeListener { oldQuery, newQuery ->
            lineAdapter.filter.filter(newQuery)
            lineAdapter.notifyDataSetChanged()
        })
    }

    override fun onError(data: String) {

    }

    override fun onHorizontalItemClick(item: LineMasterMap) {
        var bounds = LatLngBounds.builder()

        selectedLine = item!!

        bounds.include(LatLng(item!!.startNodeLat!!.toDouble(), item!!.startNodeLng!!.toDouble()))
        bounds.include(LatLng(item!!.endNodeLat!!.toDouble(), item!!.endNodeLng!!.toDouble()))

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.build().center, 13f))


    }


    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.tv_details -> {
                Log.d("SelectedLine", selectedLine.toString())
                startActivity<PipelineStream>("lineData" to selectedLine)
            }
        }
    }


    override fun onResume() {
        super.onResume()
        linePresenter.retriveLineFromServer()
    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.landing, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_node_list -> {
                startActivity<NodeList>()
            }
            R.id.nav_line_list -> {
                startActivity<PipeLineList>()
            }


        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }


    //Location Listener
    override fun onLocationChanged(location: Location?) {
        var latLng = LatLng(location!!.getLatitude(), location!!.getLongitude());
        myLocation = latLng
        locationManager.removeUpdates(this);
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    override fun onProviderEnabled(provider: String?) {

    }

    override fun onProviderDisabled(provider: String?) {

    }


    //Seekbar Listener

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        var cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLatLongBounds.center, progress.toFloat());
        mMap.animateCamera(cameraUpdate);
        locationManager.removeUpdates(this);
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {

    }

    //On Search Listener
    override fun onSearchTextChanged(oldQuery: String?, newQuery: String?) {

    }


}
