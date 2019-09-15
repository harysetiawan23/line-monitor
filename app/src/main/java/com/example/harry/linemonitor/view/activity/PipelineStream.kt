package com.example.harry.linemonitor.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import com.example.harry.linemonitor.R
import com.example.harry.linemonitor.data.*
import com.example.harry.linemonitor.view.contract.LineMasterContract
import com.example.harry.linemonitor.view.presenter.LineMasterPresenter
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
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
import org.jetbrains.anko.collections.forEachWithIndex
import org.jetbrains.anko.ctx
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.textColor
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class PipelineStream : AppCompatActivity(), OnMapReadyCallback, SubscriptionEventListener, SeekBar.OnSeekBarChangeListener, GoogleMap.OnMarkerClickListener, ActivityCompat.OnRequestPermissionsResultCallback, LocationListener, LineMasterContract {



    private lateinit var lineMaster: LineMasterMap
    private lateinit var lineMasterNew: LineMasterMap
    private lateinit var mMap: GoogleMap
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

    private val EDIT_ACTION_PERFORMED: Int = 1

    private lateinit var lineMasterPresenter: LineMasterPresenter
    private lateinit var mapFragment: SupportMapFragment

    private lateinit var mSocket: Socket






    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pipeline_stream)

        lineMaster = intent.getSerializableExtra("lineData") as LineMasterMap

        toolbar.title = "${lineMaster.name} - Statistic"
        setSupportActionBar(toolbar)

//        setTransparentStatusBar()
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment


        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        lineMasterPresenter = LineMasterPresenter(this, this)

        zoom_seekbar.setOnSeekBarChangeListener(this)


        val options = PusherOptions()
        options.setCluster(PUSHER_APP_CLUSTER)
        pusher = Pusher(PUSHER_APP_KEY, options)


        val channel = pusher.subscribe(CHANNEL_NAME)
        channel.bind(EVENT_NAME, this)
        pusher.connect()

        tv_start_node_name.text = lineMaster.startNodeSN
        tv_start_node_sn_.text = lineMaster.startNodeSN
        tv_start_node_number_.text = lineMaster.startNodePhone
        tv_end_node_name.text = lineMaster.endNodeSN
        tv_end_node_sn_.text = lineMaster.endNodeSN
        tv_end_node_number_.text = lineMaster.endNodePhone

        mapFragment.getMapAsync(this)

        val rxPermissions = RxPermissions(this)
        rxPermissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION) // ask single or multiple permission once
                .subscribe { granted ->
                    if (granted!!) {
                        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this) //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER

                    } else {

                    }
                }


        my_location_fab.setOnClickListener { view ->
            try {
                var cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLocation, zoom_seekbar.progress.toFloat())
                mMap.animateCamera(cameraUpdate)
            } catch (e: Exception) {

            }

        }


        my_pipeline_fab.onClick {
            try {
                var cameraUpdate = CameraUpdateFactory.newLatLngZoom(bounds.build().center, zoom_seekbar.progress.toFloat())
                mMap.animateCamera(cameraUpdate)
            } catch (e: Exception) {

            }

        }


        progress_bar.visibility = View.GONE

        lineMasterNew = LineMasterMap()

        mSocket = IO.socket("http://23.101.26.206")
        mSocket.connect()
        mSocket.on("line-stat/${lineMaster.id}", onLineStatMessage)
        mSocket.on("line-record/${lineMaster.id}", onLineGraphMessage)


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_pipeline, menu)
        return super.onCreateOptionsMenu(menu)
    }


    private val onLineStatMessage = com.github.nkzawa.emitter.Emitter.Listener { args ->
        runOnUiThread(Runnable {


            val data = args[0] as JSONObject

            try {

                tv_start_node_flow.text = "%.0f".format(data.getDouble("lastStartNodeFlow"))
                tv_start_node_pressure.text = "%.0f".format(data.getDouble("lastStartNodePressure"))



                tv_end_node_flow.text = "%.0f".format(data.getDouble("lastEndNodeFlow"))
                tv_end_node_pressure.text = "%.1f".format(data.getDouble("lastEndNodePressure"))


                val flowReduce = "%.0f".format(data.getDouble("startFlow")-data.getDouble("endFlow"))
                val pressureReduce = "%.0f".format(data.getDouble("startPressure")-data.getDouble("endPressure"))

                if (data.getDouble("flowRatio") > 0) {
                    tv_flow_rate_loss.text = "%.0f".format(data.getDouble("flowRatio") * 100) + " % \n( ${flowReduce} L/m )"
                    tv_flow_rate_loss.textColor = this.resources.getColor(R.color.google_red)
                    iv_flow_sign.setImageDrawable(this.resources.getDrawable(R.drawable.ic_caret_down))
                } else if (data.getDouble("flowRatio") < 0) {
                    tv_flow_rate_loss.text = "%.0f".format(data.getDouble("flowRatio") * 100) + " % \n( ${flowReduce} L/m  )"
                    tv_flow_rate_loss.textColor = this.resources.getColor(R.color.google_green)
                    iv_flow_sign.setImageDrawable(this.resources.getDrawable(R.drawable.ic_caret_arrow_up))
                } else {
                    iv_flow_sign.setImageDrawable(this.resources.getDrawable(R.drawable.ic_updown))
                    tv_flow_rate_loss.text = "%.0f".format(data.getDouble("flowRatio") * 100) + " % \n( ${flowReduce} L/m  )"
                    tv_flow_rate_loss.textColor = this.resources.getColor(R.color.background)
                }


                if (data.getDouble("pressureRatio") > 0) {
                    tv_pressure_loss.text = "%.0f".format(data.getDouble("pressureRatio") * 100) + " % \n( ${pressureReduce} bars )"
                    tv_pressure_loss.textColor = this.resources.getColor(R.color.google_red)
                    iv_pressure_sign.setImageDrawable(this.resources.getDrawable(R.drawable.ic_caret_down))
                } else if (data.getDouble("pressureRatio") < 0) {
                    tv_pressure_loss.text = "%.0f".format(data.getDouble("pressureRatio") * 100) + " % \n( ${pressureReduce} bars )"
                    tv_pressure_loss.textColor = this.resources.getColor(R.color.google_green)
                    iv_pressure_sign.setImageDrawable(this.resources.getDrawable(R.drawable.ic_caret_arrow_up))
                } else {
                    iv_pressure_sign.setImageDrawable(this.resources.getDrawable(R.drawable.ic_updown))
                    tv_pressure_loss.text = "%.0f".format(data.getDouble("pressureRatio") * 100) +" % \n( ${pressureReduce} bars )"
                    tv_pressure_loss.textColor = this.resources.getColor(R.color.background)
                }


            } catch (e: JSONException) {
                return@Runnable
            }


        })
    }

    private val onLineGraphMessage = com.github.nkzawa.emitter.Emitter.Listener { args ->
        runOnUiThread(Runnable {
            try {


                val data = args[0] as JSONArray

                var startNodeFlowEntry = ArrayList<Entry>()
                var endNodeFlowEntry = ArrayList<Entry>()


                var startNodePressureEntry = ArrayList<Entry>()
                var endNodePressureEntry = ArrayList<Entry>()


                var xValue = ArrayList<String>()



                for (i in (data.length() - 1) downTo 0) {


                    var recordData = data[i] as JSONObject

                    var lineSocketRecord = LineSocketRecord(
                            lineId = recordData.getString("lineId"),
                            days = recordData.getString("days"),
                            endFlow = recordData.getString("endFlow"),
                            endPressure = recordData.getString("endPressure"),
                            hours = recordData.getString("hours"),
                            minutes = recordData.getString("minutes"),
                            startFlow = recordData.getString("startFlow"),
                            startPressure = recordData.getString("startPressure")
                    )


                    startNodeFlowEntry.add(Entry(lineSocketRecord.startFlow!!.toFloat(), i))
                    endNodeFlowEntry.add(Entry(lineSocketRecord.endFlow!!.toFloat(), i))


                    startNodePressureEntry.add(Entry(lineSocketRecord.startPressure!!.toFloat(), i))
                    endNodePressureEntry.add(Entry(lineSocketRecord.endPressure!!.toFloat(), i))

                    xValue.add("${lineSocketRecord.hours} : ${lineSocketRecord.minutes}")
                }


                var colors = intArrayOf(R.color.google_blue, android.R.color.white)
                var index = floatArrayOf(0f, 1f)


                var startFlowDataSet = LineDataSet(startNodeFlowEntry, lineMaster.startNodeSN)
                startFlowDataSet.color = ContextCompat.getColor(this, R.color.google_blue)
                startFlowDataSet.setDrawCircles(false)
                startFlowDataSet.setDrawFilled(true)
                startFlowDataSet.fillColor = ContextCompat.getColor(this, R.color.google_blue)
                startFlowDataSet.valueTextColor = ContextCompat.getColor(this, R.color.primary_dark_material_light)


                var endFlowDataSet = LineDataSet(endNodeFlowEntry, lineMaster.endNodeSN)
                endFlowDataSet.color = ContextCompat.getColor(this, R.color.google_red)
                endFlowDataSet.setDrawCircles(false)
                endFlowDataSet.setDrawFilled(true)
                endFlowDataSet.fillColor = ContextCompat.getColor(this, R.color.google_red)
                endFlowDataSet.valueTextColor = ContextCompat.getColor(this, R.color.primary_dark_material_light)


                var startPressureDataSet = LineDataSet(startNodePressureEntry, lineMaster.startNodeSN)
                startPressureDataSet.color = ContextCompat.getColor(this, R.color.google_blue)
                startPressureDataSet.setDrawCircles(false)
                startPressureDataSet.setDrawFilled(true)
                startPressureDataSet.fillColor = ContextCompat.getColor(this, R.color.google_blue)
                startPressureDataSet.valueTextColor = ContextCompat.getColor(this, R.color.primary_dark_material_light)


                var endPressureDataSet = LineDataSet(endNodePressureEntry, lineMaster.endNodeSN)
                endPressureDataSet.color = ContextCompat.getColor(this, R.color.google_red)
                endPressureDataSet.setDrawCircles(false)
                endPressureDataSet.setDrawFilled(true)
                endPressureDataSet.fillColor = ContextCompat.getColor(this, R.color.google_red)
                endPressureDataSet.valueTextColor = ContextCompat.getColor(this, R.color.primary_dark_material_light)


                val flowDataSets = java.util.ArrayList<ILineDataSet>()
                flowDataSets.add(startFlowDataSet)
                flowDataSets.add(endFlowDataSet)

                val pressureDataSets = java.util.ArrayList<ILineDataSet>()
                pressureDataSets.add(startPressureDataSet)
                pressureDataSets.add(endPressureDataSet)


                val l = flow_chart.legend
                l.textSize = 16f


                val lp = pressure_chart.legend
                lp.textSize = 16f

                val right = flow_chart.axisRight
                right.setDrawLabels(true)
                right.textSize = 0f
                right.textColor = Color.WHITE
                right.setDrawLabels(false)
                right.setDrawAxisLine(false)
                right.setDrawGridLines(false)
                right.setDrawZeroLine(false)


                var flow = LineData(xValue, flowDataSets)
                var pressure = LineData(xValue, pressureDataSets)


                flow_chart.data = flow
                flow_chart.animateX(0)
                flow_chart.animateY(0)

                //refresh
                flow_chart.invalidate()
                flow_chart.animateX(0)
                flow_chart.animateY(0)


                pressure_chart.data = pressure
                pressure_chart.animateX(0)
                pressure_chart.animateY(0)
                //refresh
                pressure_chart.invalidate()
                pressure_chart.animateX(0)
                pressure_chart.animateY(0)


            } catch (e: java.lang.Exception) {
                Log.e("ERROR", e.toString())
            }


        })
    }




    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> finish()
            R.id.edit_pipeline -> {

                var intent = Intent(this, EditPipeLine::class.java)
                intent.putExtra("lineData", lineMaster)
                intent.putExtra("resultCode", EDIT_ACTION_PERFORMED)
                startActivityForResult(intent, EDIT_ACTION_PERFORMED)
            }
            R.id.leakage_pipeline -> startActivity<LineLeakage>("lineData" to lineMaster)
        }
        return super.onOptionsItemSelected(item)
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

        bounds.include(LatLng(lineMaster.startNodeLat!!.toDouble(), lineMaster.startNodeLng!!.toDouble()))
        bounds.include(LatLng(lineMaster.endNodeLat!!.toDouble(), lineMaster.endNodeLng!!.toDouble()))


        var startNodeMarker = MarkerOptions()
                .position(LatLng(lineMaster.startNodeLat!!.toDouble(), lineMaster.startNodeLng!!.toDouble()))
                .title(lineMaster.startNodeSN)
        var endNodeMarker = MarkerOptions()
                .position(LatLng(lineMaster.endNodeLat!!.toDouble(), lineMaster.endNodeLng!!.toDouble()))
                .title(lineMaster.endNodeSN)


        var nodeList = ArrayList<LatLng>()
        nodeList.add(LatLng(lineMaster.startNodeLat!!.toDouble(), lineMaster.startNodeLng!!.toDouble()))
        nodeList.add(LatLng(lineMaster.endNodeLat!!.toDouble(), lineMaster.endNodeLng!!.toDouble()))

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

    //Event Receiver
    override fun onEvent(channel: String?, event: String?, data: String?) {

        runOnUiThread {
            println("Received event with data: $data")
            val gson = Gson()
            val lineRecords = gson.fromJson(data, RealtimeData::class.java)

            updateNodRecords(lineRecords)

        }
    }

    override fun onStart() {
        super.onStart()
        lineMasterPresenter.refreshLineMaster(lineMaster.id.toString())
    }

    //Event View Updater
    fun updateNodRecords(data: RealtimeData) {
        try {
            data.lineRecords?.stat?.forEach { statItem: StatItem? ->
                if (lineMasterNew.id == statItem!!.id) {


                    tv_start_node_flow.text = "%.0f".format(statItem.lastStartNodeFlow)
                    tv_start_node_pressure.text = "%.0f".format(statItem.lastStartNodePressure)


                    tv_end_node_flow.text = "%.0f".format(statItem.lastEndNodeFlow)
                    tv_end_node_pressure.text = "%.1f".format(statItem.lastEndNodePressure)



                    if (statItem.flowRatio!! > 0) {
                        tv_flow_rate_loss.text = "%.0f".format(statItem.flowRatio!! * 100) + " %"
                        tv_flow_rate_loss.textColor = this.resources.getColor(R.color.google_red)
                        iv_flow_sign.setImageDrawable(this.resources.getDrawable(R.drawable.ic_caret_down))
                    } else if (statItem.flowRatio!! < 0) {
                        tv_flow_rate_loss.text = "%.0f".format(statItem.flowRatio!! * 100) + " %"
                        tv_flow_rate_loss.textColor = this.resources.getColor(R.color.google_green)
                        iv_flow_sign.setImageDrawable(this.resources.getDrawable(R.drawable.ic_caret_arrow_up))
                    } else {
                        iv_flow_sign.setImageDrawable(this.resources.getDrawable(R.drawable.ic_updown))
                        tv_flow_rate_loss.text = "%.0f".format(statItem.flowRatio!! * 100) + " %"
                        tv_flow_rate_loss.textColor = this.resources.getColor(R.color.background)
                    }


                    if (statItem.pressureRatio!! > 0) {
                        tv_pressure_loss.text = "%.0f".format(statItem.pressureRatio!! * 100) + " %"
                        tv_pressure_loss.textColor = this.resources.getColor(R.color.google_red)
                        iv_pressure_sign.setImageDrawable(this.resources.getDrawable(R.drawable.ic_caret_down))
                    } else if (statItem.pressureRatio!! < 0) {
                        tv_pressure_loss.text = "%.0f".format(statItem.pressureRatio!! * 100) + " %"
                        tv_pressure_loss.textColor = this.resources.getColor(R.color.google_green)
                        iv_pressure_sign.setImageDrawable(this.resources.getDrawable(R.drawable.ic_caret_arrow_up))
                    } else {
                        iv_pressure_sign.setImageDrawable(this.resources.getDrawable(R.drawable.ic_updown))
                        tv_pressure_loss.text = "%.0f".format(statItem.pressureRatio!! * 100) + " %"
                        tv_pressure_loss.textColor = this.resources.getColor(R.color.background)
                    }


                }
            }


            data.lineRecords?.graph?.forEachWithIndex { i, graphItem ->


            }
        } catch (e: Exception) {

        }

    }


    fun Activity.setTransparentStatusBar() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.TRANSPARENT
        }
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
        var gmapsIntentUri = Uri.parse("google.navigation:q=${marker!!.position.latitude},${marker.position.longitude}")
        var mapIntent = Intent(Intent.ACTION_VIEW, gmapsIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)

        return true
    }


    //Location Listener
    override fun onLocationChanged(location: Location?) {
        var latLng = LatLng(location!!.latitude, location.longitude)
        myLocation = latLng
        locationManager.removeUpdates(this)
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    override fun onProviderEnabled(provider: String?) {

    }

    override fun onProviderDisabled(provider: String?) {

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            EDIT_ACTION_PERFORMED -> {
                if (resultCode == EDIT_ACTION_PERFORMED) {
                    if (data!!.getBooleanExtra("isEditPerformed", false) == true) {
                        finish()
                    }
                }
            }
        }
    }




    //Line Master View Contract
    override fun showLoading() {
        progress_bar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progress_bar.visibility = View.GONE
    }

    override fun onRetriveDataSuccess(data: List<LineMasterMap?>?) {

    }

    override fun onRefreshData(data: LineMasterMap?) {

    }

    override fun onUpdateSuccess(data: LineMaster?) {

    }

    override fun onPostSuccess(data: LineMaster?) {

    }

    override fun onDeleteSuccess(data: String) {

    }

    override fun onError(data: String) {

    }

}
