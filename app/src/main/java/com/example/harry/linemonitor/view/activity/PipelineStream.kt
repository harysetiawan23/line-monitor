package com.example.harry.linemonitor.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
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
import com.example.harry.linemonitor.data.LineMasterMap
import com.example.harry.linemonitor.data.LineRecords
import com.example.harry.linemonitor.data.LineRecordsItem
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
import kotlinx.android.synthetic.main.activity_pipeline_stream.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast
import java.util.*


class PipelineStream : AppCompatActivity(), OnMapReadyCallback, SubscriptionEventListener, LineHistoryContract, SeekBar.OnSeekBarChangeListener, GoogleMap.OnMarkerClickListener, ActivityCompat.OnRequestPermissionsResultCallback, LocationListener {


    private lateinit var lineMaster: LineMasterMap
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
        setContentView(R.layout.activity_pipeline_stream)
        toolbar.title = ""
        setSupportActionBar(toolbar)


        lineMaster = intent.getSerializableExtra("lineData") as LineMasterMap

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        tv_start_node_name.text = lineMaster.startNodeSN


        tv_start_node_sn_.text = lineMaster.startNodeSN
        tv_start_node_number_.text = lineMaster.startNodePhone

        tv_end_node_name.text = lineMaster.endNodeSN


        tv_end_node_sn_.text = lineMaster.endNodeSN
        tv_end_node_number_.text = lineMaster.endNodePhone


        zoom_seekbar.setOnSeekBarChangeListener(this)


        lineHistoryPresenter = LineHistoryPresenter(this)
        lineHistoryPresenter.getLineHistory(lineMaster.id.toString())

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
            R.id.edit_pipeline -> startActivity<EditPipeLine>("lineDetails" to lineMaster)
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

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.build().center, zoom_seekbar.progress.toFloat()))
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


        mMap.setOnMarkerClickListener(this)




        mMap.addMarker(startNodeMarker)
        mMap.addMarker(endNodeMarker)
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
                if (lineMaster.id == lineRecordData!!.lineId) {
                    tv_start_node_flow.text = "%.0f".format(lineRecordData!!.lastStartNodeFlow)
                    tv_start_node_pressure.text = "%.1f".format(lineRecordData!!.lastStartNodePressure)


                    tv_end_node_flow.text = "%.0f".format(lineRecordData!!.lastEndNodeFlow)
                    tv_end_node_pressure.text = "%.1f".format(lineRecordData!!.lastEndNodePressure)


                    if (lineRecordData!!.flowRatio!! > 0) {
                        tv_flow_rate_loss.text = "%.0f".format(lineRecordData!!.flowRatio!! * 100) + " %"
                        tv_flow_rate_loss.textColor = ctx.resources.getColor(R.color.google_red)
                        iv_flow_sign.setImageDrawable(ctx.resources.getDrawable(R.drawable.ic_caret_down))
                    } else if (lineRecordData!!.flowRatio!! < 0) {
                        tv_flow_rate_loss.text = "%.0f".format(lineRecordData!!.flowRatio!! * 100) + " %"
                        tv_flow_rate_loss.textColor = ctx.resources.getColor(R.color.google_green)
                        iv_flow_sign.setImageDrawable(ctx.resources.getDrawable(R.drawable.ic_caret_arrow_up))
                    } else {
                        iv_flow_sign.setImageDrawable(ctx.resources.getDrawable(R.drawable.ic_updown))
                        tv_flow_rate_loss.text = "%.0f".format(lineRecordData!!.flowRatio!! * 100) + " %"
                    }


                    if (lineRecordData!!.pressureRatio!! > 0) {
                        tv_pressure_loss.text = "%.0f".format(lineRecordData!!.pressureRatio!! * 100) + " %"
                        tv_pressure_loss.textColor = ctx.resources.getColor(R.color.google_red)
                        iv_pressure_sign.setImageDrawable(ctx.resources.getDrawable(R.drawable.ic_caret_down))
                    } else if (lineRecordData!!.pressureRatio!! < 0) {
                        tv_pressure_loss.text = "%.0f".format(lineRecordData!!.pressureRatio!! * 100) + " %"
                        tv_pressure_loss.textColor = ctx.resources.getColor(R.color.google_green)
                        iv_pressure_sign.setImageDrawable(ctx.resources.getDrawable(R.drawable.ic_caret_arrow_up))
                    } else {
                        iv_pressure_sign.setImageDrawable(ctx.resources.getDrawable(R.drawable.ic_updown))
                        tv_pressure_loss.text = "%.0f".format(lineRecordData!!.pressureRatio!! * 100) + " %"
                    }


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
