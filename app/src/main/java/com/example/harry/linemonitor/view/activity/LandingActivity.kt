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
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import com.example.harry.linemonitor.R
import com.example.harry.linemonitor.data.LineMaster
import com.example.harry.linemonitor.data.LineMasterMap
import com.example.harry.linemonitor.data.LoginResponse
import com.example.harry.linemonitor.helper.PreferencesUtility
import com.example.harry.linemonitor.view.adapter.HorizontalLineAdapter
import com.example.harry.linemonitor.view.contract.LineMasterContract
import com.example.harry.linemonitor.view.presenter.LineMasterPresenter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_landing.*
import kotlinx.android.synthetic.main.activity_maps.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.find
import org.jetbrains.anko.startActivity
import java.util.*

class LandingActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, LineMasterContract, HorizontalLineAdapter.OnItemClickListener,
        View.OnClickListener, SeekBar.OnSeekBarChangeListener,
        MaterialSearchView.OnQueryTextListener,
        MaterialSearchView.SearchViewListener,
        LocationListener {


    private lateinit var linePresenter: LineMasterPresenter
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
    private var currBounds = LatLngBounds.builder()
    private lateinit var activeUserProfile: LoginResponse


    private lateinit var userName: TextView
    private lateinit var userEmail: TextView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)
        toolbar.title = "Home"
        setSupportActionBar(toolbar)

        thisActivity = this
        val toggle = ActionBarDrawerToggle(thisActivity, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        zoom_seekbar.setOnSeekBarChangeListener(this)
        toolbar.setNavigationIcon(ctx.getDrawable(R.drawable.ic_menu))

        linePresenter = LineMasterPresenter(ctx, this)
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        rv_line_list.layoutManager = LinearLayoutManager(ctx, LinearLayout.HORIZONTAL, false) as RecyclerView.LayoutManager?


        my_location_fab.setOnClickListener { view ->
            var cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLocation, zoom_seekbar.progress.toFloat());
            mMap.animateCamera(cameraUpdate);
        }


        nav_view.setNavigationItemSelectedListener(this)
        linePresenter.retriveLineFromServer()

        activeUserProfile = PreferencesUtility.getUserData(ctx)
        var navigationView: NavigationView = find(R.id.nav_view)
        var navHeader = navigationView.getHeaderView(0)

        userName = navHeader.find(R.id.userName)
        userEmail = navHeader.find(R.id.userEmail)


        userName.text = activeUserProfile.data!!.name
        userEmail.text = activeUserProfile.data!!.email



        FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w("Token", "getInstanceId failed", task.exception)
                        return@OnCompleteListener
                    }

                    // Get new Instance ID token
                    val token = task.result!!.token

                    // Log and toast
                    val msg = getString(R.string.fcm_token, token)
                    Log.d("Token", msg)
                })
    }


    override fun onMapReady(googleMap: GoogleMap) {

        try {
            mMap = googleMap

            var mapBounds = LatLngBounds.builder()

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
                mapBounds.include(markerOptions.position)
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


            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mapBounds.build().center, zoom_seekbar.progress.toFloat()))
            myLatLongBounds = mapBounds.build()
            currBounds = mapBounds
        } catch (e: Exception) {

        }

    }


    override fun showLoading() {
        progress_bar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progress_bar.visibility = View.GONE
    }

    //    Line Master Contract
    @SuppressLint("MissingPermission")
    override fun onRetriveDataSuccess(data: List<LineMasterMap?>?) {
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


    }

    override fun onPostSuccess(data: LineMaster?) {

    }

    override fun onDeleteSuccess(data: String) {

    }



    override fun onError(data: String) {

    }

    override fun onRefreshData(data: LineMasterMap?) {

    }

    override fun onUpdateSuccess(data: LineMaster?) {

    }


    //Horizontal Line List Item Click Listener
    override fun onHorizontalItemClick(item: LineMasterMap) {
        var selectedBounds = LatLngBounds.builder()

        selectedLine = item!!


        selectedBounds.include(LatLng(item!!.startNodeLat!!.toDouble(), item!!.startNodeLng!!.toDouble()))
        selectedBounds.include(LatLng(item!!.endNodeLat!!.toDouble(), item!!.endNodeLng!!.toDouble()))

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currBounds.build().center, 13f))

        currBounds = selectedBounds

    }


    //Widget On Click Action
    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.tv_details -> {
                Log.d("SelectedLine", selectedLine.toString())
                startActivity<PipelineStream>("lineData" to selectedLine)
            }
        }
    }


    //Activity Resume
    override fun onResume() {
        super.onResume()
        linePresenter.retriveLineFromServer()
    }


    //On Back pressed
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    //Options menu builder
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.landing, menu)
        var searchMenu: MenuItem = menu.findItem(R.id.search_line)
        search_view.setMenuItem(searchMenu)
        search_view.setOnQueryTextListener(this)
        search_view.setOnSearchViewListener(this)

        return true
    }


    //Option menu action
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {

            else -> return super.onOptionsItemSelected(item)
        }
    }


    //Navigation menu action
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_node_list -> {
                startActivity<NodeList>()
            }
            R.id.nav_line_list -> {
                startActivity<PipeLineList>()
            }
            R.id.nav_logout -> {
                PreferencesUtility.logout(ctx)
                finish()
                startActivity<LoginActivity>()
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
        var cameraUpdate = CameraUpdateFactory.newLatLngZoom(currBounds.build().center, progress.toFloat());
        mMap.animateCamera(cameraUpdate);
        locationManager.removeUpdates(this);
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {

    }

    //On Search Listener
    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query!!.isEmpty()) {

        }
        lineAdapter.filter.filter(query)
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

    override fun onSearchViewClosed() {
        onResume()
    }

    override fun onSearchViewShown() {

    }


}
