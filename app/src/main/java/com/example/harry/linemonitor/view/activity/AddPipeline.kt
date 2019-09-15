package com.example.harry.linemonitor.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.example.harry.linemonitor.R
import com.example.harry.linemonitor.data.LineMaster
import com.example.harry.linemonitor.data.LineMasterMap
import com.example.harry.linemonitor.data.NodeMaster
import com.example.harry.linemonitor.view.contract.LineMasterContract
import com.example.harry.linemonitor.view.presenter.LineMasterPresenter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_add_pipeline.*
import kotlinx.android.synthetic.main.activity_add_pipeline.progress_bar
import kotlinx.android.synthetic.main.activity_add_pipeline.submit_button
import kotlinx.android.synthetic.main.activity_add_pipeline.toolbar
import kotlinx.android.synthetic.main.activity_add_pipeline.tv_end_node
import kotlinx.android.synthetic.main.activity_add_pipeline.tv_end_node_lat
import kotlinx.android.synthetic.main.activity_add_pipeline.tv_end_node_lng
import kotlinx.android.synthetic.main.activity_add_pipeline.tv_end_node_phone
import kotlinx.android.synthetic.main.activity_add_pipeline.tv_end_node_sn
import kotlinx.android.synthetic.main.activity_add_pipeline.tv_start_node
import kotlinx.android.synthetic.main.activity_add_pipeline.tv_start_node_lat
import kotlinx.android.synthetic.main.activity_add_pipeline.tv_start_node_lng
import kotlinx.android.synthetic.main.activity_add_pipeline.tv_start_node_phone
import kotlinx.android.synthetic.main.activity_add_pipeline.tv_start_node_sn
import org.jetbrains.anko.ctx

class AddPipeline : AppCompatActivity(), View.OnClickListener, OnMapReadyCallback, LocationListener, LineMasterContract {



    private val REQURST_START_NODE_DATA = 1
    private val REQURST_END_NODE_DATA = 0


    private lateinit var mapFragment: SupportMapFragment
    private lateinit var mMap: GoogleMap

    private lateinit var startNode: NodeMaster
    private lateinit var endNode: NodeMaster

    private lateinit var locationManager: LocationManager

    private val MIN_TIME: Long = 400.toLong()
    private val MIN_DISTANCE: Float = 1000f

    private lateinit var bounds: LatLngBounds.Builder

    private lateinit var lineMaster: LineMaster
    private lateinit var lineMasterPresenter: LineMasterPresenter


    @SuppressLint("MissingPermission", "CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_pipeline)

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
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this) //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER

                    } else {

                    }
                }





        tv_start_node.setOnClickListener(this)
        tv_end_node.setOnClickListener(this)
        toolbar.title = "Add Pipeline"
        setSupportActionBar(toolbar)


        progress_bar.visibility = View.GONE
        bounds = LatLngBounds.builder()
        submit_button.setOnClickListener(this)


        lineMaster = LineMaster()

        lineMasterPresenter = LineMasterPresenter(this,this)

    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap!!


        var bounds = LatLngBounds.builder()

        val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.maps_style_white))

        if (!success) {
            Log.e("Maps Style Log", "Style parsing failed.")
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
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_start_node -> {
                var intent = Intent(this, SelectNode::class.java)
                intent.putExtra("isSelectMode", true)
                intent.putExtra("isStartNodeRequired",1)
                startActivityForResult(intent, REQURST_START_NODE_DATA)
            }

            R.id.tv_end_node -> {
                var intent = Intent(this, SelectNode::class.java)
                intent.putExtra("isSelectMode", true)
                intent.putExtra("isStartNodeRequired",0)
                startActivityForResult(intent, REQURST_END_NODE_DATA)
            }

            R.id.submit_button -> {
                lineMaster.name = et_line_name.text.toString()
                lineMaster.start = et_start.text.toString()
                lineMaster.end = et_end.text.toString()
                lineMaster.diameter = et_line_diameter.text.toString()
                lineMaster.distance = et_line_distance.text.toString()
                lineMaster.thicknes = et_line_thickness.text.toString()
                lineMaster.manufacture = et_line_manufacture.text.toString()
                lineMaster.flowLeakageTreshold = (et_flow_rate_ratio.text.toString().toDouble()/100).toString()
                lineMaster.pressureCheckDuration = et_pressure_leak_duration.text.toString()
                lineMaster.pressureLeakage = (et_pressure_leak_ratio.text.toString().toDouble()/100).toString()
                lineMasterPresenter.postLineMaster(lineMaster)

            }

        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQURST_START_NODE_DATA -> {
                if (resultCode == Activity.RESULT_OK) {
                    startNode = data?.getSerializableExtra("nodeResult") as NodeMaster

                    lineMaster.startNodeId = startNode.id.toString()

                    tv_start_node_sn.text = startNode.sn
                    tv_start_node_phone.text = startNode.phoneNumber
                    tv_start_node_lat.text = startNode.lat
                    tv_start_node_lng.text = startNode.lng


                    bounds.include(LatLng(startNode.lat!!.toDouble(), startNode.lat!!.toDouble()))
                    var startNodeMarker = MarkerOptions().position(LatLng(startNode.lat!!.toDouble(), startNode.lng!!.toDouble()))
                            .title(startNode.sn)
                            .flat(true)


                    mMap.addMarker(startNodeMarker)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(startNode.lat!!.toDouble(), startNode.lng!!.toDouble()), 8f))

                }

            }

            REQURST_END_NODE_DATA -> {
                if (resultCode == Activity.RESULT_OK) {
                    endNode = data?.getSerializableExtra("nodeResult") as NodeMaster

                    lineMaster.endNodeId = endNode.id.toString()

                    tv_end_node_sn.text = endNode.sn
                    tv_end_node_phone.text = endNode.phoneNumber
                    tv_end_node_lat.text = endNode.lat
                    tv_end_node_lng.text = endNode.lng

                    bounds.include(LatLng(endNode.lat!!.toDouble(), endNode.lng!!.toDouble()))
                    var endNodeMarker = MarkerOptions().position(LatLng(endNode.lat!!.toDouble(), endNode.lng!!.toDouble()))
                            .title(endNode.sn)
                            .flat(true)

                    mMap.addMarker(endNodeMarker)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(endNode.lat!!.toDouble(), endNode.lng!!.toDouble()), 8f))
                }


            }
        }
    }

    //    Location Change Listener
    override fun onLocationChanged(location: Location?) {

    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    override fun onProviderEnabled(provider: String?) {

    }

    override fun onProviderDisabled(provider: String?) {

    }

    //    Line Master Contract
    override fun showLoading() {
        progress_bar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progress_bar.visibility = View.GONE

    }

    override fun onRetriveDataSuccess(data: List<LineMasterMap?>?) {

    }

    override fun onPostSuccess(data: LineMaster?) {
        finish()
    }

    override fun onDeleteSuccess(data: String) {

    }

    override fun onError(data: String) {

    }

    override fun onRefreshData(data: LineMasterMap?) {

    }

    override fun onUpdateSuccess(data: LineMaster?) {

    }
}
