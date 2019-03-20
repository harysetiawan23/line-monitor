package com.example.harry.linemonitor.view.activity

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
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
import kotlinx.android.synthetic.main.activity_landing.*
import kotlinx.android.synthetic.main.activity_maps.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.startActivity
import java.util.ArrayList

class LandingActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, LineMapContract, HorizontalLineAdapter.OnItemClickListener, View.OnClickListener {


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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)
        toolbar.title = ""
        toolbar.navigationIcon = resources.getDrawable(R.drawable.ic_menu)
        setSupportActionBar(toolbar)


        linePresenter = LineMapsPresenter(this)
        thisActivity = this
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment


        rv_line_list.layoutManager = LinearLayoutManager(ctx, LinearLayout.HORIZONTAL, false)



        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(thisActivity, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)

        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }



    override fun onResume() {
        super.onResume()
        linePresenter.retriveLineFromServer()
    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.landing, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
