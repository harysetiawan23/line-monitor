package com.example.harry.linemonitor.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.harry.linemonitor.R
import com.example.harry.linemonitor.data.NodeMaster
import com.example.harry.linemonitor.data.ResponseDeleteSuccess
import com.example.harry.linemonitor.view.contract.NodeMasterContract
import com.example.harry.linemonitor.view.presenter.NodeMasterPresenter
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_add_node.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.find

class AddNode : AppCompatActivity(), View.OnClickListener, NodeMasterContract, LocationListener {


    private lateinit var nodePresenter: NodeMasterPresenter

    private lateinit var context: Context
    private lateinit var nodeMaster: NodeMaster
    private lateinit var locationManager: LocationManager
    private val MIN_TIME: Long = 400.toLong()
    private val MIN_DISTANCE: Float = 1000f

    private val NODE_CONFIG_REQUEST = 1
    private val FLOW_CONFIG_REQUEST = 2
    private val PRESSURE_CONFIG_REQUEST = 3

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_node)

        toolbar.title = "Add Node"
        setSupportActionBar(toolbar)

        context = this

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        progress_bar.visibility = View.GONE

        nodePresenter = NodeMasterPresenter(this,this)

        btn_manual_flow_rate_sensor.setOnClickListener(this)
        btn_manual_node_config.setOnClickListener(this)
        btn_manual_pressure_sensor_config.setOnClickListener(this)

        scan_qr_node_config.setOnClickListener(this)
        scan_qr_flow_rate.setOnClickListener(this)
        scan_qr_pressure.setOnClickListener(this)


        submit_button.setOnClickListener(this)

        nodeMaster = NodeMaster()

        progress_bar.visibility = View.GONE


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

    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }


    //    Click Action
    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.btn_manual_node_config -> {


//                Component Definition
                var dialogBuilder = AlertDialog.Builder(this)
                var dialogLayout = LayoutInflater.from(this).inflate(R.layout.add_node_config_dialog, null)
                var firstLabel = dialogLayout.find<TextView>(R.id.first_label)
                var secondLabel = dialogLayout.find<TextView>(R.id.second_label)
                var firstEditext = dialogLayout.find<TextView>(R.id.first_editext)
                var secondEditext = dialogLayout.find<TextView>(R.id.second_editext)
                var submitButton = dialogLayout.find<Button>(R.id.submit_button)


//                View Definition
                dialogBuilder.setTitle("Node Config")
                dialogBuilder.setView(dialogLayout)

                firstLabel.text = "Node SN"
                secondLabel.text = "Phone Number"


                var dialog = dialogBuilder.create()
                dialog.show()


//                View Action
                submitButton.setOnClickListener { v: View? ->

                    try {
                        if (firstEditext.text.isEmpty() || secondEditext.text.isEmpty()) {
                            var alertDialog = AlertDialog.Builder(this)
                            alertDialog.setTitle("Field(s) must not empty")
                            alertDialog.setNegativeButton("Dismiss", DialogInterface.OnClickListener { dialog, which ->
                                dialog.dismiss()
                            })

                            alertDialog.show()

                        }
                        nodeMaster.sn = firstEditext.text.toString()
                        nodeMaster.phoneNumber = secondEditext.text.toString()


                        add_node_serial_number.text = "SN : ${nodeMaster.sn}"
                        add_node_node_phone_number.text = "NUMBER : ${nodeMaster.phoneNumber}"

                        add_node_node_lat.text = "LAT : ${nodeMaster.lat}"
                        add_node_node_lng.text = "LNG : ${nodeMaster.lng}"


                        Log.d("NodeDataGet", nodeMaster.toString())
                        dialog.dismiss()
                    } catch (e: Exception) {

                    }


                }

            }

            R.id.btn_manual_flow_rate_sensor -> {

//                Component Definition
                var dialogBuilder = AlertDialog.Builder(this)
                var dialogLayout = LayoutInflater.from(this).inflate(R.layout.add_node_config_dialog, null)
                var firstLabel = dialogLayout.find<TextView>(R.id.first_label)
                var secondLabel = dialogLayout.find<TextView>(R.id.second_label)
                var firstEditext = dialogLayout.find<TextView>(R.id.first_editext)
                var secondEditext = dialogLayout.find<TextView>(R.id.second_editext)
                var submitButton = dialogLayout.find<Button>(R.id.submit_button)


//                View Definition
                dialogBuilder.setTitle("Flow Rate Config")
                dialogBuilder.setView(dialogLayout)

                firstLabel.text = "Sensor Model"
                secondLabel.text = "Callibration Factor"


                var dialog = dialogBuilder.create()
                dialog.show()


//                View Action
                submitButton.setOnClickListener { v: View? ->
                    try {
                        if (firstEditext.text.isEmpty() || secondEditext.text.isEmpty()) {
                            var alertDialog = AlertDialog.Builder(this)
                            alertDialog.setTitle("Field(s) must not empty")
                            alertDialog.setNegativeButton("Dismiss", DialogInterface.OnClickListener { dialog, which ->
                                dialog.dismiss()
                            })

                            alertDialog.show()

                        }

                        nodeMaster.flowRateModel = firstEditext.text.toString()
                        nodeMaster.liquidFlowKonstanta = secondEditext.text.toString().toDouble()


                        add_node_flow_rate_model.text = "MODEL : ${nodeMaster.flowRateModel}"
                        add_node_callibration_factor.text = "CALLUBRATION FACTOT : ${nodeMaster.liquidFlowKonstanta}"


                        Log.d("NodeDataGet", nodeMaster.toString())
                        dialog.dismiss()
                    } catch (e: java.lang.Exception) {

                    }


                }

            }

            R.id.btn_manual_pressure_sensor_config -> {

//                Component Definition
                var dialogBuilder = AlertDialog.Builder(this)
                var dialogLayout = LayoutInflater.from(this).inflate(R.layout.add_node_config_dialog, null)
                var firstLabel = dialogLayout.find<TextView>(R.id.first_label)
                var secondLabel = dialogLayout.find<TextView>(R.id.second_label)
                var firstEditext = dialogLayout.find<TextView>(R.id.first_editext)
                var secondEditext = dialogLayout.find<TextView>(R.id.second_editext)
                var submitButton = dialogLayout.find<Button>(R.id.submit_button)


//                View Definition
                dialogBuilder.setTitle("Flow Rate Config")
                dialogBuilder.setView(dialogLayout)

                firstLabel.text = "Sensor Model"
                secondLabel.text = "Offset"


                var dialog = dialogBuilder.create()
                dialog.show()


//                View Action
                submitButton.setOnClickListener { v: View? ->

                    try {
                        if (firstEditext.text.isEmpty() || secondEditext.text.isEmpty()) {
                            var alertDialog = AlertDialog.Builder(this)
                            alertDialog.setTitle("Field(s) must not empty")
                            alertDialog.setNegativeButton("Dismiss", DialogInterface.OnClickListener { dialog, which ->
                                dialog.dismiss()
                            })

                            alertDialog.show()

                        }

                        nodeMaster.pressureTranducerModel = firstEditext.text.toString()
                        nodeMaster.pressOffset = secondEditext.text.toString().toDouble()


                        add_node_pressure_model.text = "MODEL : ${nodeMaster.pressureTranducerModel}"
                        add_node_pressure_offset.text = "OFFSET : ${nodeMaster.pressOffset}"


                        Log.d("NodeDataGet", nodeMaster.toString())
                        dialog.dismiss()


                    } catch (e: java.lang.Exception) {

                    }


                }

            }

            R.id.scan_qr_node_config -> {

                var intent = Intent(this, Scanner::class.java)
                intent.putExtra("requestCode", NODE_CONFIG_REQUEST.toString())
                startActivityForResult(intent, NODE_CONFIG_REQUEST)
            }

            R.id.scan_qr_flow_rate -> {

                var intent = Intent(this, Scanner::class.java)
                intent.putExtra("requestCode", FLOW_CONFIG_REQUEST.toString())
                startActivityForResult(intent, FLOW_CONFIG_REQUEST)
            }

            R.id.scan_qr_pressure -> {

                var intent = Intent(this, Scanner::class.java)
                intent.putExtra("requestCode", PRESSURE_CONFIG_REQUEST.toString())
                startActivityForResult(intent, PRESSURE_CONFIG_REQUEST)
            }

            R.id.submit_button -> {
                if (togle_start_node.isChecked) {
                    nodeMaster.isStartNode = 1
                } else {
                    nodeMaster.isStartNode = 0
                }

                try{

                    if (!nodeMaster.sn.isNullOrEmpty() || !nodeMaster.phoneNumber.isNullOrEmpty() || nodeMaster.liquidFlowKonstanta!!.toFloat() <= 0 || nodeMaster.pressOffset!!.toFloat() <= 0) {
                        nodePresenter.storeNode(nodeMaster)
                    }else{
                        var alertDialog = AlertDialog.Builder(this)
                        alertDialog.setTitle("Attention")
                        alertDialog.setMessage("Please scan Node's, Water Flow's and Pressure Tranducer's  QR to fill all the information")
                        alertDialog.setNegativeButton("Dismiss", DialogInterface.OnClickListener { dialog, which ->
                            dialog.dismiss()
                        })

                        alertDialog.create().show()
                    }

                }catch (e:Exception){

                }




            }


        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (resultCode) {
            NODE_CONFIG_REQUEST -> {
                var value = data!!.getStringExtra("SCAN_RESULT")
                var valueList: List<String> = data.getStringExtra("SCAN_RESULT").split("/")
                Log.d("SCANRESULT", "DATA RECEIVED : ${value}")

                nodeMaster.sn = valueList[0]
                nodeMaster.phoneNumber = valueList[1]


                add_node_serial_number.text = "SN : ${nodeMaster.sn}"
                add_node_node_phone_number.text = "NUMBER : ${nodeMaster.phoneNumber}"

                add_node_node_lat.text = "LAT : ${nodeMaster.lat}"
                add_node_node_lng.text = "LNG : ${nodeMaster.lng}"

            }

            FLOW_CONFIG_REQUEST -> {
                var value = data!!.getStringExtra("SCAN_RESULT")
                var valueList: List<String> = data.getStringExtra("SCAN_RESULT").split("/")
                Log.d("SCANRESULT", "DATA RECEIVED : ${value}")


                nodeMaster.flowRateModel = valueList[0]
                nodeMaster.liquidFlowKonstanta = valueList[1].toDouble()


                add_node_flow_rate_model.text = "MODEL : ${nodeMaster.flowRateModel}"
                add_node_callibration_factor.text = "CALLUBRATION FACTOT : ${nodeMaster.liquidFlowKonstanta}"


            }

            PRESSURE_CONFIG_REQUEST -> {
                var value = data!!.getStringExtra("SCAN_RESULT")
                var valueList: List<String> = data.getStringExtra("SCAN_RESULT").split("/")
                Log.d("SCANRESULT", "DATA RECEIVED : ${value}")


                nodeMaster.pressureTranducerModel = valueList[0]
                nodeMaster.pressOffset = valueList[1].toDouble()


                add_node_pressure_model.text = "MODEL : ${nodeMaster.pressureTranducerModel}"
                add_node_pressure_offset.text = "OFFSET : ${nodeMaster.pressOffset}"

            }
        }
    }


    //    Node Master Listener
    override fun showLoading() {
        progress_bar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progress_bar.visibility = View.GONE

    }

    override fun onRetriveNodeListSuccess(data: List<NodeMaster?>?) {

    }

    override fun onSubmitSuccess(data: NodeMaster) {
        finish()
    }

    override fun onUpdate(data: NodeMaster) {

    }

    override fun onDeleteSuccess(data: ResponseDeleteSuccess) {

    }


    override fun onError(data: String) {
        Log.e("NodeStoreError", data.toString())
        var alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Error Post UserData")
        alertDialog.setMessage(data)
        alertDialog.setNegativeButton("Dismiss", DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
        })

        alertDialog.create().show()
    }


    //    Locaton Listener
    override fun onLocationChanged(location: Location?) {

        nodeMaster.lat = location!!.latitude.toString()
        nodeMaster.lng = location.longitude.toString()

        locationManager.removeUpdates(this)

    }


    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    override fun onProviderEnabled(provider: String?) {

    }

    override fun onProviderDisabled(provider: String?) {

    }

}
