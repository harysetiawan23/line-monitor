package com.example.harry.linemonitor.view.activity

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import com.example.harry.linemonitor.R
import com.example.harry.linemonitor.data.LineMasterMap
import com.example.harry.linemonitor.view.adapter.HorizontalLineAdapter
import com.example.harry.linemonitor.view.contract.LineMapContract
import com.example.harry.linemonitor.view.presenter.LineMapsPresenter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_maps.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.startActivity
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, LineMapContract, HorizontalLineAdapter.OnItemClickListener, View.OnClickListener {


    private lateinit var linePresenter: LineMapsPresenter
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var listLine: ArrayList<ArrayList<LatLng>>
    private lateinit var listPin: ArrayList<MarkerOptions>
    private lateinit var thisActivity: Activity
    private lateinit var mMap: GoogleMap
    private lateinit var selectedLine: LineMasterMap

    override fun showLoading() {
        progress_bar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progress_bar.visibility = View.GONE
    }

    override fun onSuccess(data: List<LineMasterMap?>?) {
        listLine = ArrayList<ArrayList<LatLng>>()
        listPin = ArrayList<MarkerOptions>()


        rv_line_list.setAdapter(HorizontalLineAdapter(data, this, ctx))

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

        mapFragment.getMapAsync(this)
    }

    override fun onError(data: String) {

    }

    override fun onHorizontalItemClick(item: LineMasterMap) {
        var bounds = LatLngBounds.builder()

        selectedLine = item!!

        bounds.include(LatLng(item!!.startNodeLat!!.toDouble(), item!!.startNodeLng!!.toDouble()))
        bounds.include(LatLng(item!!.endNodeLat!!.toDouble(), item!!.endNodeLng!!.toDouble()))

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.build().center, 13f))


    }


    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.tv_details -> {
                Log.d("SelectedLine", selectedLine.toString())
                startActivity<LineDataV2>("lineData" to selectedLine)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        toolbar.title = ""
        setSupportActionBar(toolbar)


        linePresenter = LineMapsPresenter(this)
        thisActivity = this
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment


        rv_line_list.layoutManager = LinearLayoutManager(ctx, LinearLayout.HORIZONTAL, false)

//        tv_dismiss.setOnClickListener(this)
//        tv_details.setOnClickListener(this)
    }


    override fun onResume() {
        super.onResume()
        linePresenter.retriveLineFromServer()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        var bounds = LatLngBounds.builder()

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
            bounds.include(markerOptions.position)
        }


        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.build().center, 13f))
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return super.onCreateOptionsMenu(menu)
    }

    fun Activity.setTransparentStatusBar() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ctx.resources.getColor(R.color.transparent_header)
        }
    }

    private fun showArrange(arrange: Int): Animation {
        val animation = AnimationUtils
                .loadAnimation(ctx, R.anim.abc_slide_in_top)
        animation.duration = arrange * 1000.toLong()
        return animation
    }

    private fun fadeIn(arrange: Int): Animation {
        val animation = AnimationUtils
                .loadAnimation(ctx, R.anim.abc_fade_in)
        animation.duration = arrange * 1000.toLong()
        return animation
    }

}
