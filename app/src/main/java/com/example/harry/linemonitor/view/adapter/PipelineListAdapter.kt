package com.example.harry.linemonitor.view.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.amulyakhare.textdrawable.TextDrawable
import com.example.harry.linemonitor.R
import com.example.harry.linemonitor.data.LineMasterMap
import com.example.harry.linemonitor.view.activity.PipelineStream
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity
import java.util.*

class PipelineListAdapter(context: Context, pipelineList: List<LineMasterMap?>?, private val listener: OnItemClickListener) : RecyclerView.Adapter<PipelineListAdapter.PipelineItemViewHolder>() {
    private val context = context
    private val pipelineList = pipelineList!!


    //    on CLick Interface

    interface OnItemClickListener {
        fun onHorizontalItemClick(item: LineMasterMap)
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): PipelineItemViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.pipeline_list_item, parent, false)
        return PipelineItemViewHolder(v)
    }

    override fun getItemCount(): Int {
        if (pipelineList.size == 0) {
            return 0
        } else {
            return pipelineList.size
        }
    }

    override fun onBindViewHolder(holder: PipelineItemViewHolder, position: Int) {
        holder.bind(pipelineList!!.get(position)!!, listener, context)
    }


    class PipelineItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var lineName: TextView
        var lineDistance: ImageView
        var lineDiameter: TextView
        var lineThickness: TextView
        var lineManufacture: TextView
        var lineDrawable: ImageView
        var line_pin: ImageView
        var lineDesc: TextView
        var lineAction: LinearLayout


        init {
            lineName = itemView.find(R.id.tv_line_name)
            lineDistance = itemView.find(R.id.iv_line_distance)
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
                    .buildRoundRect(item!!.distance.toString(), getRandomColor(context, 1), 62)


            val animation = AnimationUtils
                    .loadAnimation(context, R.anim.abc_fade_in)
            animation.duration = position * 500.toLong()
            line_pin.startAnimation(animation)


            lineDistance.setImageDrawable(fontDrawable)


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


