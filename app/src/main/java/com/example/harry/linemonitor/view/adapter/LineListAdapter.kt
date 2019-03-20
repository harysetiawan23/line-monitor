package com.example.harry.linemonitor.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.amulyakhare.textdrawable.TextDrawable
import com.example.harry.linemonitor.R
import com.example.harry.linemonitor.data.LineMaster
import java.util.*


class LineListAdapter(data: List<LineMaster?>?, context:Context) : BaseAdapter() {
    var context = context
    var lineList = data

    private class ViewHolder {

        internal var lineName: TextView? = null
        internal var lineDesc: TextView? = null
        internal var lineImg: ImageView? = null


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
        //Line Data
        var lineData = lineList!!.get(position)

        var convertView = convertView
        // Get the data item for this position
        val dataModel = getItem(position)
        // Check if an existing view is being reused, otherwise inflate the view
        val viewHolder: ViewHolder // view lookup cache stored in tag

        val result: View

        if (convertView == null) {

            viewHolder = ViewHolder()
            val inflater = LayoutInflater.from(context)

            convertView = inflater.inflate(R.layout.home_item, parent, false)


            viewHolder.lineName = convertView!!.findViewById(R.id.tv_line_name) as TextView
            viewHolder.lineDesc = convertView.findViewById(R.id.tv_line_desc) as TextView
            viewHolder.lineImg = convertView.findViewById(R.id.iv_line_thumb) as ImageView

            result = convertView

            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
            result = convertView
        }


        val animation = AnimationUtils
                .loadAnimation(context, R.anim.abc_fade_in)
        animation.duration = position*1000.toLong()
        result.startAnimation(animation)


        val rand = Random()
        val fontDrawable = TextDrawable.builder()
                .buildRoundRect(lineData!!.name!!.get(0).toString(), viewHolder.getRandomColor(context, rand.nextInt(4)), 62)


        viewHolder.lineName!!.text = lineData!!.name
        viewHolder.lineDesc!!.text = "${lineData!!.start} - ${lineData!!.end}"
        viewHolder.lineImg!!.setImageDrawable(fontDrawable)

        return convertView
    }

    override fun getItem(position: Int): Any {
        return lineList!!.get(position)!!
    }

    override fun getItemId(position: Int): Long {
        return lineList!!.get(position)!!.id!!.toLong()
    }

    override fun getCount(): Int {
        if (lineList == null) {
            return 0
        }
        return lineList!!.size
    }

}

//
//class LineListAdapter(dataSet: List<LineMaster?>?, mContext: Context) : ArrayAdapter<LineMaster>(mContext, R.layout.home_item, dataSet), View.OnClickListener {
//
//    private var lastPosition = -1
//    var lineListData = dataSet
//
//    // View lookup cache
//    private class ViewHolder {
//
//        internal var lineName: TextView? = null
//        internal var lineDesc: TextView? = null
//        internal var lineImg: ImageView? = null
//
//
//        fun getRandomColor(ctx: Context, index: Int): Int {
//            val colors = ArrayList<Int>()
//            colors.add(ctx.resources.getColor(R.color.google_blue))
//            colors.add(ctx.resources.getColor(R.color.google_red))
//            colors.add(ctx.resources.getColor(R.color.google_yellow))
//            colors.add(ctx.resources.getColor(R.color.google_green))
//            return colors[index]
//        }
//    }
//
//    override fun onClick(v: View) {
//
//        val position = v.tag as Int
//        val `object` = getItem(position)
//        val dataModel = `object` as LineMaster
//
////        when (v.id) {
////            R.id.item_info -> Snackbar.make(v, "Release date " + dataModel!!.getFeature(), Snackbar.LENGTH_LONG)
////                    .setAction("No action", null).show()
////        }
//    }
//
//    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//        var convertView = convertView
//        // Get the data item for this position
//        val dataModel = getItem(position)
//        // Check if an existing view is being reused, otherwise inflate the view
//        val viewHolder: ViewHolder // view lookup cache stored in tag
//
//        val result: View
//
//        if (convertView == null) {
//
//            viewHolder = ViewHolder()
//            val inflater = LayoutInflater.from(context)
//
//            convertView = inflater.inflate(R.layout.home_item, parent, false)
//
//
//            viewHolder.lineName = convertView!!.findViewById(R.id.tv_line_name) as TextView
//            viewHolder.lineDesc = convertView.findViewById(R.id.tv_line_desc) as TextView
//            viewHolder.lineImg = convertView.findViewById(R.id.iv_line_thumb) as ImageView
//
//            result = convertView
//
//            convertView.tag = viewHolder
//        } else {
//            viewHolder = convertView.tag as ViewHolder
//            result = convertView
//        }
//
//
//        val animation = AnimationUtils
//                .loadAnimation(context, R.anim.abc_fade_in)
//        animation.duration = position*1000.toLong()
//        result.startAnimation(animation)
//
//
//        val rand = Random()
//        val fontDrawable = TextDrawable.builder()
//                .buildRoundRect(lineListData!!.get(position)!!.name!!.get(0).toString(), viewHolder.getRandomColor(context, rand.nextInt(4)), 62)
//
//
//        viewHolder.lineName!!.text = lineListData!!.get(position)!!.name
//        viewHolder.lineDesc!!.text = "${lineListData!!.get(position)!!.start} - ${lineListData!!.get(position)!!.end}"
//        viewHolder.lineImg!!.setImageDrawable(fontDrawable)
//
//        return convertView
//    }
//}
//
