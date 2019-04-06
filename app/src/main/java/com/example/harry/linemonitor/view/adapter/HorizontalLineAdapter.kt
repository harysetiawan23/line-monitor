package com.example.harry.linemonitor.view.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import com.amulyakhare.textdrawable.TextDrawable
import com.example.harry.linemonitor.R
import com.example.harry.linemonitor.data.LineMasterMap
import com.example.harry.linemonitor.view.activity.PipelineStream
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.util.*
import kotlin.collections.ArrayList

class HorizontalLineAdapter(data: List<LineMasterMap?>?, private val listener: OnItemClickListener, context: Context) : RecyclerView.Adapter<HorizontalLineAdapter.viewHolder>(), Filterable {


    private var lineListFiltered = data
    private var lineList = data
    private var context = context

    interface OnItemClickListener {
        fun onHorizontalItemClick(item: LineMasterMap)
    }


    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): viewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.home_item_big_invert, parent, false)
        return viewHolder(v)
    }

    override fun getItemCount(): Int {
        if (lineList.isNullOrEmpty()) {
            return 0
        }
        return lineList!!.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.bind(lineList!!.get(position)!!, listener, context)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): Filter.FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    lineListFiltered = lineList
                } else {
                    var filteredList: ArrayList<LineMasterMap?>? = ArrayList()
                    context.toast(charString.toLowerCase())
                    for (row in lineList!!) {
                        if (row!!.name!!.toLowerCase().contains(charString.toLowerCase()) || row!!.startNodeSN!!.toLowerCase().contains(charString.toLowerCase()) || row!!.endNodeSN!!.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList!!.add(row)
                        }
                    }

                    lineListFiltered = filteredList
                }

                val filterResults = Filter.FilterResults()
                filterResults.values = lineListFiltered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: Filter.FilterResults) {
                lineList = filterResults.values as List<LineMasterMap>
                notifyDataSetChanged()
            }
        }
    }

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var lineName: TextView
        var lineDistance: TextView
        var lineDiameter: TextView
        var lineThickness: TextView
        var lineManufacture: TextView
        var lineDrawable: ImageView
        var line_pin: ImageView
        var lineDesc: TextView
        var lineAction: LinearLayout

        init {
            lineName = itemView.find(R.id.tv_line_name)
            lineDistance = itemView.find(R.id.tv_line_distance)
            lineDiameter = itemView.find(R.id.tv_line_diameter)
            lineManufacture = itemView.find(R.id.tv_line_manufacture)
            lineThickness = itemView.find(R.id.tv_line_thickness)
            lineDrawable = itemView.find(R.id.fontDrawable)
            line_pin = itemView.find(R.id.iv_line_pin)
            lineDesc = itemView.find(R.id.tv_line_desc)
            lineAction = itemView.find(R.id.tv_details)

        }


        fun bind(item: LineMasterMap, listener: OnItemClickListener, context: Context) {
            lineName.text = item.name
            itemView.setOnClickListener { listener.onHorizontalItemClick(item) }
            lineDistance.text = "${item!!.distance.toString()}"
            lineDiameter.text = "${item!!.diameter.toString()} inch"
            lineThickness.text = "${item!!.thicknes.toString()} inch"
            lineManufacture.text = "${item!!.manufacture.toString()}"
            lineDesc.text = "${item!!.start} - ${item!!.end}"


            val rand = Random()
            val currRandom = rand.nextInt(4)
            val nextRandom = rand.nextInt(4)

            var currColor = 0
            if (currRandom != nextRandom) {
                currColor = nextRandom
            }

            val fontDrawable = TextDrawable.builder()
                    .buildRoundRect(item!!.name!!.get(0).toString(), getRandomColor(context, currColor), 62)


            val animation = AnimationUtils
                    .loadAnimation(context, R.anim.abc_fade_in)
            animation.duration = position * 500.toLong()
            line_pin.startAnimation(animation)


            lineDrawable.setImageDrawable(fontDrawable)


            lineAction.onClick {
                context.startActivity<PipelineStream>("lineData" to item)
            }


        }


        fun getRandomColor(ctx: Context, index: Int): Int {
            val colors = ArrayList<Int>()
            colors.add(ctx.resources.getColor(R.color.google_blue))
            colors.add(ctx.resources.getColor(R.color.google_red))
            colors.add(ctx.resources.getColor(R.color.google_yellow))
            colors.add(ctx.resources.getColor(R.color.google_green))
            return colors[index]
        }
    }

}