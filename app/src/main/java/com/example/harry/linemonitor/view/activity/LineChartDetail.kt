package com.example.harry.linemonitor.view.activity

import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.example.harry.linemonitor.R
import com.example.harry.linemonitor.data.LineChartRecap
import com.example.harry.linemonitor.data.LineMasterMap
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.android.synthetic.main.activity_line_chart_detail.*
import org.jetbrains.anko.collections.forEachWithIndex
import java.util.*

class LineChartDetail : AppCompatActivity() {

    lateinit var lineMaster: LineMasterMap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_line_chart_detail)

        lineMaster = intent.getSerializableExtra("lineData") as LineMasterMap


    }



}
