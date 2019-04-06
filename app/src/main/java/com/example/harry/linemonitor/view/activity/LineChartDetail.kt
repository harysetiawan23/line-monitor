package com.example.harry.linemonitor.view.activity

import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.example.harry.linemonitor.R
import com.example.harry.linemonitor.data.LineChartRecap
import com.example.harry.linemonitor.data.LineMasterMap
import com.example.harry.linemonitor.view.contract.LienRecapContract
import com.example.harry.linemonitor.view.presenter.LineRecapPresenter
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.android.synthetic.main.activity_line_chart_detail.*
import org.jetbrains.anko.collections.forEachWithIndex
import java.util.*

class LineChartDetail : AppCompatActivity(), LienRecapContract {

    lateinit var lineMaster: LineMasterMap
    lateinit var lineRecapPresenter: LineRecapPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_line_chart_detail)

        lineMaster = intent.getSerializableExtra("lineData") as LineMasterMap
        lineRecapPresenter = LineRecapPresenter(this)
        lineRecapPresenter.retriveLineRecap(lineMaster.id.toString())

    }

    override fun showLoading() {
        progress_bar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progress_bar.visibility = View.GONE
    }

    override fun onSuccess(data: LineChartRecap) {
        //Bar Entry
        var avgStartBarEntry = ArrayList<Entry>()
        var avgEndBarEntry = ArrayList<Entry>()
        var avgXvalue = ArrayList<String>()

        var maxStartBarEntry = ArrayList<Entry>()
        var maxEndBarEntry = ArrayList<Entry>()
        var maxXvalue = ArrayList<String>()

        var medianStartBarEntry = ArrayList<Entry>()
        var medianEndBarEntry = ArrayList<Entry>()
        var medianXvalue = ArrayList<String>()


        //Bar Data
        var avgBarData = data!!.avg
        var maxBarData = data!!.max
        var medianBarData = data!!.median!!.reversed()


        //Data Getter
        avgBarData!!.startRecord!!.reversed().forEachWithIndex { i, data ->
            avgStartBarEntry.add(Entry(data!!.startFlow!!.toFloat(), i))
            avgXvalue.add("${data!!.hours} : ${data!!.minutes}")
        }


        avgBarData!!.endRecord!!.reversed().forEachWithIndex { i, data ->
            avgEndBarEntry.add(Entry(data!!.startFlow!!.toFloat(), i))
        }



        maxBarData!!.startRecord!!.reversed().forEachWithIndex { i, data ->
            maxStartBarEntry.add(Entry(data!!.startFlow!!.toFloat(), i))
            maxXvalue.add("${data!!.hours} : ${data!!.minutes}")
        }

        maxBarData!!.endRecord!!.reversed().forEachWithIndex { i, data ->
            maxEndBarEntry.add(Entry(data!!.startFlow!!.toFloat(), i))
        }


        medianBarData!!.forEachWithIndex { i, data ->
            medianStartBarEntry.add(Entry(data!!.startFlow!!.toFloat(), i))
            medianEndBarEntry.add(Entry(data!!.endFlow!!.toFloat(), i))
            medianXvalue.add("${data!!.hours} : ${data!!.minutes}")
        }


        //Chart Line Extractor
        //AVG Chart
        var avgStartDataSet = LineDataSet(avgStartBarEntry, lineMaster.startNodeSN)
        avgStartDataSet.setColor(ContextCompat.getColor(this, R.color.google_blue));
        avgStartDataSet.lineWidth = 16f
        avgStartDataSet.setCircleSize(5f);
        avgStartDataSet.setCircleColor(ContextCompat.getColor(this, R.color.google_blue));
        avgStartDataSet.setCircleColorHole(ContextCompat.getColor(this, R.color.google_blue))
        avgStartDataSet.setValueTextColor(ContextCompat.getColor(this, R.color.primary_dark_material_light));


        var avgEndDataSet = LineDataSet(avgEndBarEntry, lineMaster.endNodeSN)
        avgEndDataSet.setColor(ContextCompat.getColor(this, R.color.google_red));
        avgEndDataSet.lineWidth = 16f
        avgEndDataSet.setCircleSize(5f);
        avgEndDataSet.setCircleColor(ContextCompat.getColor(this, R.color.google_red));
        avgEndDataSet.setCircleColorHole(ContextCompat.getColor(this, R.color.google_red))
        avgEndDataSet.setValueTextColor(ContextCompat.getColor(this, R.color.primary_dark_material_light));


        //Max
        var maxStartDataSet = LineDataSet(maxStartBarEntry, lineMaster.startNodeSN)
        maxStartDataSet.setColor(ContextCompat.getColor(this, R.color.google_blue));
        maxStartDataSet.lineWidth = 16f
        maxStartDataSet.setCircleSize(5f);
        maxStartDataSet.setCircleColor(ContextCompat.getColor(this, R.color.google_blue));
        maxStartDataSet.setCircleColorHole(ContextCompat.getColor(this, R.color.google_blue))
        maxStartDataSet.setValueTextColor(ContextCompat.getColor(this, R.color.primary_dark_material_light));


        var maxEndDataSet = LineDataSet(maxEndBarEntry, lineMaster.endNodeSN)
        maxEndDataSet.setColor(ContextCompat.getColor(this, R.color.google_red));
        maxEndDataSet.lineWidth = 16f
        maxEndDataSet.setCircleSize(5f);
        maxEndDataSet.setCircleColor(ContextCompat.getColor(this, R.color.google_red));
        maxEndDataSet.setCircleColorHole(ContextCompat.getColor(this, R.color.google_red))
        maxEndDataSet.setValueTextColor(ContextCompat.getColor(this, R.color.primary_dark_material_light));


        //Median
        var medianStartDataSet = LineDataSet(medianStartBarEntry, lineMaster.startNodeSN)
        medianStartDataSet.setColor(ContextCompat.getColor(this, R.color.google_blue));
        medianStartDataSet.lineWidth = 16f
        medianStartDataSet.setCircleSize(5f);
        medianStartDataSet.setCircleColor(ContextCompat.getColor(this, R.color.google_blue));
        medianStartDataSet.setCircleColorHole(ContextCompat.getColor(this, R.color.google_blue))
        medianStartDataSet.setValueTextColor(ContextCompat.getColor(this, R.color.primary_dark_material_light));


        var medianEndDataSet = LineDataSet(medianEndBarEntry, lineMaster.endNodeSN)
        medianEndDataSet.setColor(ContextCompat.getColor(this, R.color.google_red));
        medianEndDataSet.lineWidth = 16f
        medianEndDataSet.setCircleSize(5f);
        medianEndDataSet.setCircleColor(ContextCompat.getColor(this, R.color.google_red));
        medianEndDataSet.setCircleColorHole(ContextCompat.getColor(this, R.color.google_red))
        medianEndDataSet.setValueTextColor(ContextCompat.getColor(this, R.color.primary_dark_material_light));


        val avgDataSets = java.util.ArrayList<ILineDataSet>()
        avgDataSets.add(avgStartDataSet)
        avgDataSets.add(avgEndDataSet)

        val maxDataSets = java.util.ArrayList<ILineDataSet>()
        maxDataSets.add(maxStartDataSet)
        maxDataSets.add(maxEndDataSet)


        val medianDataSets = java.util.ArrayList<ILineDataSet>()
        medianDataSets.add(medianStartDataSet)
        medianDataSets.add(medianEndDataSet)


        val l = cv_avg.legend
        l.setTextSize(16f)


        val lp = cv_avg.legend
        lp.setTextSize(16f)

        val right = cv_avg.axisRight
        right.setDrawLabels(true)
        right.textSize = 0f
        right.textColor = Color.WHITE
        right.setDrawLabels(false)
        right.setDrawAxisLine(false)
        right.setDrawGridLines(false)
        right.setDrawZeroLine(false)


        var avgData = LineData(avgXvalue, avgDataSets)
        var maxData = LineData(maxXvalue, maxDataSets)
        var medianData = LineData(medianXvalue, medianDataSets)


        cv_avg.setData(avgData);
        cv_avg.animateX(1000);
        //refresh
        cv_avg.invalidate();


        cv_max.setData(maxData);
        cv_max.animateX(1000);
        //refresh
        cv_max.invalidate();


        cv_median.setData(medianData);
        cv_median.animateX(1000);
        //refresh
        cv_median.invalidate();

    }

    override fun onError(data: String) {

    }

}
