package com.example.harry.linemonitor.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.example.harry.linemonitor.R
import com.example.harry.linemonitor.data.NodeMaster
import com.example.harry.linemonitor.data.ResponseDeleteSuccess
import com.example.harry.linemonitor.view.contract.NodeMasterContract
import com.example.harry.linemonitor.view.presenter.NodeMasterPresenter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_edit_node.*
import org.jetbrains.anko.ctx

class EditNode : AppCompatActivity(), LocationListener, OnMapReadyCallback, NodeMasterContract, View.OnClickListener {




    private lateinit var mapFragment: SupportMapFragment
    private lateinit var mMap: GoogleMap
    private lateinit var nodeMaster: NodeMaster
    private lateinit var locationManager: LocationManager
    private val MIN_TIME: Long = 400.toLong()
    private val MIN_DISTANCE: Float = 1000f
    private lateinit var nodeMasterPresenter: NodeMasterPresenter
    private var REQUEST_CODE:Int = 0


    @SuppressLint("MissingPermission", "CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_node)


        toolbar.title = "Edit Node"
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


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

        nodeMaster = intent.getSerializableExtra("nodeData") as NodeMaster
        REQUEST_CODE = intent.getIntExtra("resultCode",1)
        nodeMasterPresenter = NodeMasterPresenter(this,this)

        et_node_lat.setText(nodeMaster.lat.toString())
        et_node_lng.setText(nodeMaster.lng.toString())
        et_node_sn.setText(nodeMaster.sn.toString())
        et_node_number.setText(nodeMaster.phoneNumber.toString())
        et_flow_rate_model.setText(nodeMaster.flowRateModel.toString())
        et_flow_rate_konstanta.setText(nodeMaster.liquidFlowKonstanta.toString())
        et_pressure_model.setText(nodeMaster.pressureTranducerModel.toString())
        et_pressure_offset.setText(nodeMaster.pressOffset.toString())

        togle_start_node.isChecked = nodeMaster.isStartNode == 1

        progress_bar.visibility = View.GONE


        save_node.setOnClickListener(this)
        delete_node.setOnClickListener(this)

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
        mMap.addMarker(MarkerOptions().position(LatLng(nodeMaster.lat!!.toDouble(), nodeMaster.lng!!.toDouble())))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(nodeMaster.lat!!.toDouble(), nodeMaster.lng!!.toDouble()), 13f))
    }

    //    On Click
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.save_node -> {
                nodeMaster.sn = et_node_sn.text.toString()
                nodeMaster.lat = et_node_lat.text.toString()
                nodeMaster.lng = et_node_lng.text.toString()
                nodeMaster.phoneNumber = et_node_number.text.toString()
                nodeMaster.flowRateModel = et_flow_rate_model.text.toString()
                nodeMaster.liquidFlowKonstanta = et_flow_rate_konstanta.text.toString().toDouble()
                nodeMaster.pressureTranducerModel = et_pressure_model.text.toString()
                nodeMaster.pressOffset = et_pressure_offset.text.toString().toDouble()
                if(togle_start_node.isChecked){
                    nodeMaster.isStartNode = 1
                }else{
                    nodeMaster.isStartNode = 0
                }


                nodeMasterPresenter.updateNodeData(nodeMaster)

            }

            R.id.delete_node -> {

                var alertDialog = AlertDialog.Builder(this)
                alertDialog.setTitle("Delete Node")
                alertDialog.setMessage("Are you sure to delete this node?")

                var intent = Intent()
                intent.putExtra("isEditPerformed",true)


                alertDialog.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                    nodeMasterPresenter.deleteNodeData(nodeMaster)
                    setResult(REQUEST_CODE,intent)
                    dialog.dismiss()
                    finish()
                })


                alertDialog.setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                })

                var dialogData:AlertDialog =  alertDialog.create()

                dialogData.show()
                dialogData.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(this.getColor(R.color.google_blue))
                dialogData.getButton(AlertDialog.BUTTON_NEGATIVE)
                dialogData.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(this.getColor(R.color.google_blue))

                dialogData.show()
            }

        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }


    //    Location
    override fun onLocationChanged(location: Location?) {

    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    override fun onProviderEnabled(provider: String?) {

    }

    override fun onProviderDisabled(provider: String?) {

    }


    //Node Master Contract
    override fun showLoading() {
        progress_bar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progress_bar.visibility = View.GONE
    }

    override fun onRetriveNodeListSuccess(data: List<NodeMaster?>?) {

    }

    override fun onUpdate(data: NodeMaster) {
        finish()
    }

    override fun onSubmitSuccess(data: NodeMaster) {

    }

    override fun onDeleteSuccess(data: ResponseDeleteSuccess) {

    }

    override fun onError(data: String) {
        var alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Error")
        alertDialog.setMessage(data)

        alertDialog.create().show()
    }
}
