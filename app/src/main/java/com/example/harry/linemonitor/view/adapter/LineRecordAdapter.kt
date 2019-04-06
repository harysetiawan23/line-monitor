package com.example.harry.linemonitor.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.harry.linemonitor.R
import com.example.harry.linemonitor.data.LineRecap
import java.util.*


class LineRecordAdapter(data: List<LineRecap?>?, context: Context) : BaseAdapter() {
    var context = context
    var recordList = data

    private class ViewHolder {

        internal var startFlow: TextView? = null
        internal var endFlow: TextView? = null
        internal var startPressure: TextView? = null
        internal var endPressure: TextView? = null
        internal var time: TextView? = null


        fun getRandomColor(ctx: Context, index: Int): Int {
            val colors = ArrayList<Int>()
            colors.add(ctx.resources.getColor(R.color.google_blue))
            colors.add(ctx.resources.getColor(R.color.google_red))
            colors.add(ctx.resources.getColor(R.color.google_yellow))
            colors.add(ctx.resources.getColor(R.color.google_green))
            return colors[index]
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var lineData = recordList!!.get(position)


        var convertView = convertView

        val dataModel = getItem(position)

        val viewHolder: ViewHolder // view lookup cache stored in tag

        val result: View

        if (convertView == null) {

            viewHolder = ViewHolder()
            val inflater = LayoutInflater.from(context)

            convertView = inflater.inflate(R.layout.item_rv_line_records, parent, false)


            viewHolder.startFlow = convertView!!.findViewById(R.id.tv_start_flow) as TextView
            viewHolder.startPressure = convertView.findViewById(R.id.tv_start_pressure) as TextView
            viewHolder.endFlow = convertView.findViewById(R.id.tv_end_flow) as TextView
            viewHolder.endPressure = convertView.findViewById(R.id.tv_end_pressure) as TextView
            viewHolder.time = convertView.findViewById(R.id.tv_line_record_minute) as TextView

            result = convertView

            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
            result = convertView
        }


        val animation = AnimationUtils
                .loadAnimation(context, R.anim.abc_fade_in)
        animation.duration = position * 50.toLong()
        result.startAnimation(animation)


        viewHolder.time!!.text = "${lineData!!.hours} : ${lineData!!.minutes}"
        viewHolder.startFlow!!.text = lineData!!.startFlow.toString()
        viewHolder.startPressure!!.text = "%.2f".format(lineData!!.startPressure)

        viewHolder.endFlow!!.text = lineData!!.endFlow.toString()
        viewHolder.endPressure!!.text = "%.2f".format(lineData!!.endPressure)

        return convertView
    }

    override fun getItem(position: Int): Any {
        return recordList!!.get(position)!!
    }

    override fun getItemId(position: Int): Long {
        return recordList!!.get(position)!!.lineId!!.toLong()
    }

    override fun getCount(): Int {
        if (recordList == null) {
            return 0
        }
        return recordList!!.size
    }

}
