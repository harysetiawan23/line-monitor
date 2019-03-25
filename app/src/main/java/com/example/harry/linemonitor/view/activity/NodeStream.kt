package com.example.harry.linemonitor.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import com.example.harry.linemonitor.R
import com.example.harry.linemonitor.data.LineHistory
import com.example.harry.linemonitor.data.LineRecords
import com.example.harry.linemonitor.data.LineRecordsItem
import com.example.harry.linemonitor.data.NodeMaster
import com.example.harry.linemonitor.view.contract.LineHistoryContract
import com.example.harry.linemonitor.view.presenter.LineHistoryPresenter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.SubscriptionEventListener
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_node_stream.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast


class NodeStream : AppCompatActivity(), OnMapReadyCallback, SubscriptionEventListener, LineHistoryContract, SeekBar.OnSeekBarChangeListener, GoogleMap.OnMarkerClickListener, ActivityCompat.OnRequestPermissionsResultCallback, LocationListener {


    private lateinit var nodeMaster: NodeMaster
    private lateinit var mMap: GoogleMap
    private lateinit var lineHistoryPresenter: LineHistoryPresenter
    private lateinit var pusher: Pusher

    private val PUSHER_APP_KEY = "ac63e4ba64a71a9f00a9"
    private val PUSHER_APP_CLUSTER = "ap1"
    private val CHANNEL_NAME = "record_added"
    private val EVENT_NAME = "App\\Events\\NodeRecordEvent"

    private lateinit var rxPermission: RuntimePermission
    private lateinit var bounds: LatLngBounds.Builder
    private lateinit var myLatLongBounds: LatLngBounds
    private lateinit var myLocation: LatLng
    private lateinit var locationManager: LocationManager

    private val MIN_TIME: Long = 400.toLong()
    private val MIN_DISTANCE: Float = 1000f


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_node_stream)
        toolbar.title = ""
        setSupportActionBar(toolbar)


        nodeMaster = intent.getSerializableExtra("nodeData") as NodeMaster

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        tv_start_node_name.text = nodeMaster.sn


        tv_start_node_sn_.text = nodeMaster.sn
        tv_start_node_number_.text = nodeMaster.phoneNumber



        zoom_seekbar.setOnSeekBarChangeListener(this)


        lineHistoryPresenter = LineHistoryPresenter(this)
        lineHistoryPresenter.getLineHistory(nodeMaster.id.toString())

        val options = PusherOptions()
        options.setCluster(PUSHER_APP_CLUSTER)
        pusher = Pusher(PUSHER_APP_KEY, options)


        val channel = pusher.subscribe(CHANNEL_NAME)
        channel.bind(EVENT_NAME, this)
        pusher.connect()

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


        my_location_fab.setOnClickListener { view ->
            var cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLocation, zoom_seekbar.progress.toFloat());
            mMap.animateCamera(cameraUpdate);
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_pipeline, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> finish()
            R.id.edit_pipeline -> startActivity<EditPipeLine>("nodeDetail" to nodeMaster)
        }
        return super.onOptionsItemSelected(item)
    }


    private fun showArrange(arrange: Int): Animation {
        val animation = AnimationUtils
                .loadAnimation(ctx, R.anim.abc_fade_in)
        animation.duration = arrange * 1000.toLong()

        return animation
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.maps_style_white))

        if (!success) {
            Log.e("Maps Style Log", "Style parsing failed.")
        }

        bounds = LatLngBounds.builder()
        var nodeMarker = MarkerOptions()
                .position(LatLng(nodeMaster!!.lat!!.toDouble(), nodeMaster!!.lng!!.toDouble()))
                .title(nodeMaster!!.sn)

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(nodeMaster!!.lat!!.toDouble(), nodeMaster!!.lng!!.toDouble()), zoom_seekbar.progress.toFloat()))

        mMap.isTrafficEnabled = true
        mMap.isBuildingsEnabled = true



        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {


            mMap.isMyLocationEnabled = true


        } else {

        }


        mMap.setOnMarkerClickListener(this)


        mMap.addMarker(nodeMarker)

        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.uiSettings.isScrollGesturesEnabled = false
    }

    override fun onEvent(channel: String?, event: String?, data: String?) {
        runOnUiThread {
            println("Received event with data: $data")
            val gson = Gson()
            val lineRecords = gson.fromJson(data, LineRecords::class.java)

            updateNodRecords(lineRecords)
        }
    }

    fun updateNodRecords(data: LineRecords) {
        try {
            data.lineRecords?.forEach { lineRecordData: LineRecordsItem? ->
                if (nodeMaster.id == lineRecordData!!.lineId) {
                    tv_start_node_flow.text = "%.0f".format(lineRecordData!!.lastStartNodeFlow)
                    tv_start_node_pressure.text = "%.1f".format(lineRecordData!!.lastStartNodePressure)

                }
            }
        } catch (e: Exception) {

        }

    }

    override fun onSuccess(data: List<LineHistory?>?) {

        Log.d("LineHistoryData", data.toString())
        Log.d("LineHistoryDataCount", data!!.size.toString())

        var startNodeData = data.filter { lineHistory -> lineHistory!!.isStartNode!!.equals(1) }
        var endNodeData = data.filter { lineHistory -> lineHistory!!.isStartNode!!.equals(0) }

        Log.d("LineHistoryDataStart", startNodeData.size.toString())
        Log.d("LineHistoryDataEnd", endNodeData.size.toString())

        toast("Success")


        for ((i, datum) in startNodeData!!.withIndex()) {
            Log.d("LineHistoryData", datum.toString())

        }


    }


    override fun onError(data: String) {

    }

    override fun showLoading() {
        progress_bar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progress_bar.visibility = View.GONE
    }


    //Seekbar Listener

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.build().center, zoom_seekbar.progress.toFloat()))
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {

    }


    //Marker CLick

    override fun onMarkerClick(marker: Marker?): Boolean {
        var gmapsIntentUri = Uri.parse("google.navigation:q=${marker!!.position.latitude},${marker!!.position.longitude}")
        var mapIntent = Intent(Intent.ACTION_VIEW, gmapsIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);

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


}
