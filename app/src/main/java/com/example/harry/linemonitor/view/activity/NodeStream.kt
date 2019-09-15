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
import android.widget.SeekBar
import com.example.harry.linemonitor.R
import com.example.harry.linemonitor.data.*
import com.example.harry.linemonitor.view.contract.NodeMasterContract
import com.example.harry.linemonitor.view.presenter.NodeMasterPresenter
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.SubscriptionEventListener
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_node_stream.*
import org.jetbrains.anko.collections.forEachWithIndex
import org.jetbrains.anko.ctx
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class NodeStream : AppCompatActivity(),
        OnMapReadyCallback,
        SubscriptionEventListener,
        SeekBar.OnSeekBarChangeListener,
        GoogleMap.OnMarkerClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        LocationListener,
        NodeMasterContract {


    private lateinit var nodeMaster: NodeMaster
    private lateinit var mMap: GoogleMap
    private lateinit var pusher: Pusher

    private val PUSHER_APP_KEY = "ac63e4ba64a71a9f00a9"
    private val PUSHER_APP_CLUSTER = "ap1"
    private val CHANNEL_NAME = "record_added"
    private val EVENT_NAME = "App\\Events\\NodeRecordEvent"

    private lateinit var myLocation: LatLng
    private lateinit var locationManager: LocationManager

    private val MIN_TIME: Long = 400.toLong()
    private val MIN_DISTANCE: Float = 1000f


    private  val EDIT_ACTION_PERFORMED:Int = 1

    private lateinit var nodePresenter:NodeMasterPresenter
    private lateinit var mSocket: Socket


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_node_stream)
        nodeMaster = intent.getSerializableExtra("nodeData") as NodeMaster
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        nodePresenter = NodeMasterPresenter(this,this)



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        zoom_seekbar.setOnSeekBarChangeListener(this)

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
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this) //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER

                    } else {

                    }
                }


        my_location_fab.setOnClickListener { view ->
            var cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLocation, zoom_seekbar.progress.toFloat())
            mMap.animateCamera(cameraUpdate)
        }

        mSocket = IO.socket("http://23.101.26.206")
        mSocket.connect()
        mSocket.on("node-stat/${nodeMaster.sn}", onNodeStatMessage)
        mSocket.on("node-history/${nodeMaster.sn}", onLineGraphMessage)

        Log.i("node-history/${nodeMaster.sn}",onNodeStatMessage.toString())
        Log.i("node-stat/${nodeMaster.sn}", onNodeStatMessage.toString())
    }

    private val onNodeStatMessage = com.github.nkzawa.emitter.Emitter.Listener { args ->
        runOnUiThread(Runnable {


            val data = args[0] as JSONArray

            try {
                val jsonObject = data[0] as JSONObject
                tv_start_node_flow.text = "%.0f".format(jsonObject.getDouble("flow"))
                tv_start_node_pressure.text = "%.0f".format(jsonObject.getDouble("pressure"))


            } catch (e: JSONException) {
                return@Runnable
            }


        })
    }

    private val onLineGraphMessage = com.github.nkzawa.emitter.Emitter.Listener { args ->
        runOnUiThread(Runnable {
            try {


                val data = args[0] as JSONArray

                var startNodeFlowEntry = ArrayList<BarEntry>()
                var startNodePressureEntry = ArrayList<BarEntry>()


                var xValue = ArrayList<String>()



                for (i in (data.length() - 1) downTo 0) {


                    var recordData = data[i] as JSONObject


                    var lineSocketRecord = LineSocketRecord(
                            lineId = recordData.getString("id"),
                            days = recordData.getString("days"),
                            hours = recordData.getString("hours"),
                            minutes = recordData.getString("minutes"),
                            startFlow = recordData.getString("flow"),
                            startPressure = recordData.getString("pressure")
                    )


                    startNodeFlowEntry.add(BarEntry(lineSocketRecord.startFlow!!.toFloat(), i))



                    startNodePressureEntry.add(BarEntry(lineSocketRecord.startPressure!!.toFloat(), i))


                    xValue.add("${lineSocketRecord.hours} : ${lineSocketRecord.minutes}")
                }


                var colors = intArrayOf(R.color.google_blue, android.R.color.white)
                var index = floatArrayOf(0f, 1f)


                var startFlowDataSet = BarDataSet(startNodeFlowEntry, nodeMaster.sn)
                startFlowDataSet.color = ContextCompat.getColor(this, R.color.google_blue)
                startFlowDataSet.valueTextColor = ContextCompat.getColor(this, R.color.primary_dark_material_light)


                var startPressureDataSet = BarDataSet(startNodePressureEntry, nodeMaster.sn)
                startPressureDataSet.color = ContextCompat.getColor(this, R.color.google_blue)
                startPressureDataSet.valueTextColor = ContextCompat.getColor(this, R.color.primary_dark_material_light)


                val flowDataSets = java.util.ArrayList<IBarDataSet>()
                flowDataSets.add(startFlowDataSet)


                val pressureDataSets = java.util.ArrayList<IBarDataSet>()
                pressureDataSets.add(startPressureDataSet)


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


                var flow = BarData(xValue, flowDataSets)
                var pressure = BarData(xValue, pressureDataSets)


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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            EDIT_ACTION_PERFORMED -> {
                if(resultCode==EDIT_ACTION_PERFORMED){
                    if(data!!.getBooleanExtra("isEditPerformed",false)==true){
                        finish()
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_node_stream, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> finish()
            R.id.edit -> {
                var intent = Intent(this,EditNode::class.java)
                intent.putExtra("nodeData",nodeMaster)
                intent.putExtra("resultCode",EDIT_ACTION_PERFORMED)
                startActivityForResult(intent,EDIT_ACTION_PERFORMED)
//                startActivity<EditNode>("nodeData" to nodeMaster)
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onResume() {
        super.onResume()

        nodePresenter.refreshNodeData(nodeMaster)


    }



    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.maps_style_white))

        if (!success) {
            Log.e("Maps Style Log", "Style parsing failed.")
        }

        var nodeMarker = MarkerOptions()
                .position(LatLng(nodeMaster.lat!!.toDouble(), nodeMaster.lng!!.toDouble()))
                .title(nodeMaster.sn)

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(nodeMaster.lat!!.toDouble(), nodeMaster.lng!!.toDouble()), zoom_seekbar.progress.toFloat()))

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

    //Event Receiver
    override fun onEvent(channel: String?, event: String?, data: String?) {

        runOnUiThread {
            println("Received event with data: $data")
            val gson = Gson()
            val lineRecords = gson.fromJson(data, RealtimeData::class.java)
            Log.d("DataFromEvent", lineRecords.toString())
            updateNodRecords(lineRecords)

        }
    }

    //Event View Updater
    fun updateNodRecords(data: RealtimeData) {
        try {


            data.lineRecords?.stat?.forEach { statItem: StatItem? ->


                var lineId: Int = 0
                var isStartNode: Boolean = false
                if (statItem!!.startNodeId == nodeMaster.id) {
                    lineId = statItem.id!!
                    isStartNode = true
                } else if (statItem.endNodeId == nodeMaster.id) {
                    lineId = statItem.id!!
                    isStartNode = false
                }


                if(lineId!=0){
                    data.lineRecords.stat.forEach { statItem: StatItem? ->
                        if (lineId == statItem!!.id) {

                            if(isStartNode){
                                tv_start_node_flow.text = "%.0f".format(statItem.lastStartNodeFlow)
                                tv_start_node_pressure.text = "%.0f".format(statItem.lastStartNodePressure)
                            }else{
                                tv_start_node_flow.text = "%.0f".format(statItem.lastEndNodeFlow)
                                tv_start_node_pressure.text = "%.0f".format(statItem.lastEndNodePressure)
                            }


                        }
                    }


                    data.lineRecords.graph?.forEachWithIndex { i, graphItem ->
                        var startNodeFlowEntry = ArrayList<BarEntry>()
                        var endNodeFlowEntry = ArrayList<BarEntry>()


                        var startNodePressureEntry = ArrayList<BarEntry>()
                        var endNodePressureEntry = ArrayList<BarEntry>()


                        var xValue = ArrayList<String>()

                        if(lineId == graphItem?.lineId){


                            graphItem.data?.reversed()!!.forEachWithIndex{ i, dataItem ->
                                xValue.add("${dataItem!!.hours} : ${dataItem.minutes}")

                                if(isStartNode){
                                    startNodeFlowEntry.add(BarEntry(dataItem.startFlow!!.toFloat(),i))
                                    startNodePressureEntry.add(BarEntry(dataItem.startPressure!!.toFloat(),i))
                                }else{
                                    endNodeFlowEntry.add(BarEntry(dataItem.endFlow!!.toFloat(),i))
                                    endNodePressureEntry.add(BarEntry(dataItem.endPressure!!.toFloat(),i))
                                }


                            }


                        }
                        var colors = intArrayOf(R.color.google_blue, android.R.color.white)
                        var index = floatArrayOf(0f, 1f)



                        var startFlowDataSet = BarDataSet(startNodeFlowEntry, statItem.startNodeSN)
                        startFlowDataSet.color = ContextCompat.getColor(this, R.color.google_blue)
                        startFlowDataSet.valueTextColor = ContextCompat.getColor(this, R.color.primary_dark_material_light)


                        var endFlowDataSet = BarDataSet(endNodeFlowEntry, statItem.endNodeSN)
                        endFlowDataSet.color = ContextCompat.getColor(this, R.color.google_red)
                        endFlowDataSet.valueTextColor = ContextCompat.getColor(this, R.color.primary_dark_material_light)


                        var startPressureDataSet = BarDataSet(startNodePressureEntry, statItem.startNodeSN)
                        startPressureDataSet.color = ContextCompat.getColor(this, R.color.google_blue)
                        startPressureDataSet.valueTextColor = ContextCompat.getColor(this, R.color.primary_dark_material_light)


                        var endPressureDataSet = BarDataSet(endNodePressureEntry, statItem.endNodeSN)
                        endPressureDataSet.color = ContextCompat.getColor(this, R.color.google_red)
                        endPressureDataSet.valueTextColor = ContextCompat.getColor(this, R.color.primary_dark_material_light)


                        val flowDataSets = java.util.ArrayList<IBarDataSet>()
                        flowDataSets.add(startFlowDataSet)
                        flowDataSets.add(endFlowDataSet)

                        val pressureDataSets = java.util.ArrayList<IBarDataSet>()
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


                        var data = BarData(xValue, flowDataSets)
                        var pressure = BarData(xValue, pressureDataSets)


                        flow_chart.data = data
                        flow_chart.animateX(1000)
                        //refresh
                        flow_chart.invalidate()


                        pressure_chart.data = pressure
                        pressure_chart.animateX(1000)
                        //refresh
                        pressure_chart.invalidate()


                    }
                }


                Log.d("NodeData","Curr node Id : ${nodeMaster.id} -- LineId = ${lineId} ")
            }


    } catch (e: Exception) {

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
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(nodeMaster.lat!!.toDouble(), nodeMaster.lng!!.toDouble()), zoom_seekbar.progress.toFloat()))
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


    //Node UserData Contract
    override fun onRetriveNodeListSuccess(data: List<NodeMaster?>?) {

    }


    override fun onUpdate(data: NodeMaster) {
        nodeMaster = data
        tv_start_node_sn_.text = nodeMaster.sn
        tv_start_node_number_.text = nodeMaster.phoneNumber
        tv_node_sn.text = nodeMaster.sn
        toolbar.title = "${nodeMaster.sn} - Stat"
    }

    override fun onDeleteSuccess(data: ResponseDeleteSuccess) {

    }

    override fun onSubmitSuccess(data: NodeMaster) {

    }


}
