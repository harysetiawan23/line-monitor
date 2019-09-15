package com.example.harry.linemonitor.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
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
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_edit_pipe_line.*
import org.jetbrains.anko.ctx
import java.util.*

class EditPipeLine : AppCompatActivity(), LocationListener, OnMapReadyCallback, View.OnClickListener, LineMasterContract {



    private lateinit var mapFragment: SupportMapFragment
    private lateinit var mMap: GoogleMap
    private lateinit var locationManager: LocationManager
    private val MIN_TIME: Long = 400.toLong()
    private val MIN_DISTANCE: Float = 1000f
    private lateinit var lineMaster:LineMasterMap
    private lateinit var bounds: LatLngBounds.Builder

    private lateinit var lineMasterPresenter: LineMasterPresenter
    private var REQUEST_CODE: Int = 0


    private val REQURST_START_NODE_DATA = 1
    private val REQURST_END_NODE_DATA = 0

    private lateinit var updateLineMaster:LineMaster
    private lateinit var startNode: NodeMaster
    private lateinit var endNode: NodeMaster


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
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this) //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER

                    } else {

                    }
                }

        lineMaster = intent.getSerializableExtra("lineData") as LineMasterMap
        lineMasterPresenter = LineMasterPresenter(this,this)

        REQUEST_CODE = intent.getIntExtra("resultCode", 1)

        updateLineMaster = LineMaster()

        et_pipeline_diameter.setText(lineMaster.diameter.toString())
        et_pipeline_end.setText(lineMaster.end.toString())
        et_pipeline_length.setText(lineMaster.distance.toString())
        et_pipeline_manufacture.setText(lineMaster.manufacture.toString())
        et_pipeline_name.setText(lineMaster.name)
        et_pipeline_start.setText(lineMaster.start)
        et_pipeline_thickness.setText(lineMaster.thicknes.toString())

        tv_start_node_sn.text = lineMaster.startNodeSN
        tv_start_node_phone.text = lineMaster.startNodePhone
        tv_start_node_lat.text = lineMaster.startNodeLat
        tv_start_node_lng.text = lineMaster.startNodeLng

        tv_end_node_sn.text = lineMaster.endNodeSN
        tv_end_node_phone.text = lineMaster.endNodePhone
        tv_end_node_lat.text = lineMaster.endNodeLat
        tv_end_node_lng.text = lineMaster.endNodeLng

        et_pressure_leak_duration.setText((lineMaster.pressureCheckDuration!!).toString())
        et_pressure_leak_ratio.setText(Math.floor(lineMaster.pressureLeakage!!.toDouble() * 100).toString())
        et_flow_rate_ratio.setText((lineMaster.flowLeakageTreshold!!.toDouble() * 100).toString())

        updateLineMaster.startNodeId = lineMaster.startNodeId.toString()
        updateLineMaster.endNodeId = lineMaster.endNodeId.toString()


        delete_line.setOnClickListener(this)
        submit_button.setOnClickListener(this)
        tv_end_node.setOnClickListener(this)
        tv_start_node.setOnClickListener(this)
    }


    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap!!

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


    //    Button Click
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.delete_line -> {
                try {
                    var alertDialog = AlertDialog.Builder(this)
                    alertDialog.setTitle("Delete Pipeline")
                    alertDialog.setMessage("Are you sure to delete this line?")

                    var intent = Intent()
                    intent.putExtra("isEditPerformed", true)


                    alertDialog.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                        lineMasterPresenter.deleteLineMaster(lineMaster.id.toString())
                        setResult(REQUEST_CODE, intent)
                        dialog.dismiss()
                        finish()
                    })


                    alertDialog.setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->
                        dialog.dismiss()
                    })

                    var dialogData: AlertDialog = alertDialog.create()

                    dialogData.show()
                    dialogData.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(this.getColor(R.color.google_blue))
                    dialogData.getButton(AlertDialog.BUTTON_NEGATIVE)
                    dialogData.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(this.getColor(R.color.google_blue))

                    dialogData.show()

                } catch (e: Exception) {

                }

            }

            R.id.tv_start_node -> {
                var intent = Intent(this, SelectNode::class.java)
                intent.putExtra("isSelectMode", true)
                startActivityForResult(intent, REQURST_START_NODE_DATA)
            }

            R.id.tv_end_node -> {
                var intent = Intent(this, SelectNode::class.java)
                intent.putExtra("isSelectMode", true)
                startActivityForResult(intent, REQURST_END_NODE_DATA)
            }

            R.id.submit_button -> {

                updateLineMaster.id = lineMaster.id
                updateLineMaster.name = et_pipeline_name.text.toString()
                updateLineMaster.start = et_pipeline_start.text.toString()
                updateLineMaster.end = et_pipeline_end.text.toString()
                updateLineMaster.diameter = et_pipeline_diameter.text.toString()
                updateLineMaster.distance = et_pipeline_length.text.toString()
                updateLineMaster.thicknes = et_pipeline_thickness.text.toString()
                updateLineMaster.manufacture = et_pipeline_manufacture.text.toString()
                updateLineMaster.flowLeakageTreshold = (et_flow_rate_ratio.text.toString().toDouble() / 100).toString()
                updateLineMaster.pressureCheckDuration = et_pressure_leak_duration.text.toString()
                updateLineMaster.pressureLeakage = (et_pressure_leak_ratio.text.toString().toDouble() / 100).toString()

                lineMasterPresenter.updateLineMaster(updateLineMaster)

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQURST_START_NODE_DATA -> {
                if (resultCode == Activity.RESULT_OK) {
                    startNode = data?.getSerializableExtra("nodeResult") as NodeMaster

                    updateLineMaster.startNodeId = startNode.id.toString()

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

                    updateLineMaster.endNodeId = endNode.id.toString()

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


    //    Location Contract
    override fun onLocationChanged(location: Location?) {

    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    override fun onProviderEnabled(provider: String?) {

    }

    override fun onProviderDisabled(provider: String?) {

    }

    //    Line Master View Contract
    override fun showLoading() {
        progress_bar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progress_bar.visibility = View.GONE
    }

    override fun onRetriveDataSuccess(data: List<LineMasterMap?>?) {

    }

    override fun onPostSuccess(data: LineMaster?) {

    }

    override fun onDeleteSuccess(data: String) {

    }

    override fun onError(data: String) {

    }

    override fun onRefreshData(data: LineMasterMap?) {
        lineMaster = data!!
        onResume()
    }

    override fun onUpdateSuccess(data: LineMaster?) {
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

}
