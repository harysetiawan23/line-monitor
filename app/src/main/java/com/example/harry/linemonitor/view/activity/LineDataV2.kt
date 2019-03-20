package com.example.harry.linemonitor.view.activity

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
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
import kotlinx.android.synthetic.main.activity_line_data_v2.*
import kotlinx.android.synthetic.main.content_line_data_v2.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast
import java.util.*


class LineDataV2 : AppCompatActivity(), OnMapReadyCallback, SubscriptionEventListener, LineHistoryContract {


    private lateinit var lineMaster: LineMasterMap
    private lateinit var mMap: GoogleMap
    private lateinit var lineHistoryPresenter: LineHistoryPresenter
    private lateinit var pusher: Pusher

    private val PUSHER_APP_KEY = "ac63e4ba64a71a9f00a9"
    private val PUSHER_APP_CLUSTER = "ap1"
    private val CHANNEL_NAME = "record_added"
    private val EVENT_NAME = "App\\Events\\NodeRecordEvent"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_line_data_v2)

        lineMaster = intent.getSerializableExtra("lineData") as LineMasterMap
        toolbar.title = lineMaster.name

        setSupportActionBar(toolbar)


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment



        tv_start_node_name.text = lineMaster.startNodeSN
        tv_start_node_number.text = lineMaster.startNodePhone

        tv_end_node_name.text = lineMaster.endNodeSN
        tv_end_node_number.text = lineMaster.endNodePhone


        lineHistoryPresenter = LineHistoryPresenter(this)
        lineHistoryPresenter.getLineHistory(lineMaster.id.toString())

        val options = PusherOptions()
        options.setCluster(PUSHER_APP_CLUSTER)
        pusher = Pusher(PUSHER_APP_KEY, options)


        val channel = pusher.subscribe(CHANNEL_NAME)
        channel.bind(EVENT_NAME, this)
        pusher.connect()

        mapFragment.getMapAsync(this)
    }


    override fun onResume() {
        super.onResume()


    }

    override fun onEvent(channel: String?, event: String?, data: String?) {
        runOnUiThread {
            println("Received event with data: $data")
            val gson = Gson()
            val lineRecords = gson.fromJson(data, LineRecords::class.java)

            updateNodRecords(lineRecords)
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {

//        SETUP MAP VIEW
        mMap = googleMap

        val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.maps_style_white))

        if (!success) {
            Log.e("Maps Style Log", "Style parsing failed.")
        }


        var bounds = LatLngBounds.builder()

        bounds.include(LatLng(lineMaster!!.startNodeLat!!.toDouble(), lineMaster!!.startNodeLng!!.toDouble()))
        bounds.include(LatLng(lineMaster!!.endNodeLat!!.toDouble(), lineMaster!!.endNodeLng!!.toDouble()))


        var startNodeMarker = MarkerOptions()
                .position(LatLng(lineMaster!!.startNodeLat!!.toDouble(), lineMaster!!.startNodeLng!!.toDouble()))
                .title(lineMaster!!.startNodeSN)
        var endNodeMarker = MarkerOptions().position(LatLng(lineMaster!!.endNodeLat!!.toDouble(), lineMaster!!.endNodeLng!!.toDouble()))
                .title(lineMaster!!.endNodeSN)


        var nodeList = ArrayList<LatLng>()
        nodeList.add(LatLng(lineMaster!!.startNodeLat!!.toDouble(), lineMaster!!.startNodeLng!!.toDouble()))
        nodeList.add(LatLng(lineMaster!!.endNodeLat!!.toDouble(), lineMaster!!.endNodeLng!!.toDouble()))

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.build().center, 12f))
        mMap.addPolyline(PolylineOptions().addAll(nodeList).width(12f)
                .color(Color.RED))


        mMap.addMarker(startNodeMarker)
        mMap.addMarker(endNodeMarker)
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


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

}
