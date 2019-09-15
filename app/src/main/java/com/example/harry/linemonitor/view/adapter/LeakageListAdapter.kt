package com.example.harry.linemonitor.view.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.harry.linemonitor.R
import com.example.harry.linemonitor.data.LineData
import com.example.harry.linemonitor.data.LineLeakageResponse
import org.jetbrains.anko.find
import java.text.SimpleDateFormat


class LeakageListAdapter(context: Context, leakageList: List<LineLeakageResponse?>?, private val listener: OnItemClickListener) : RecyclerView.Adapter<LeakageListAdapter.PipelineItemViewHolder>() {
    private val context = context
    private val pipelineList = leakageList!!


    //    on CLick Interface

    interface OnItemClickListener {
        fun onHorizontalItemClick(item: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): PipelineItemViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.timeline_leakage_item, parent, false)
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
        holder.bind(pipelineList.get(position)!!, listener, context)
    }


    class PipelineItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var leakageDay: TextView
        var leakageDate: TextView
        var leakageTime: TextView
        var lineName: TextView
        var notSolvedButton: Button
        var solvedButton: Button


        init {
            lineName = itemView.find(R.id.timeline_line_name)
            leakageDay = itemView.find(R.id.timeline_day)
            leakageDate = itemView.find(R.id.timeline_date)
            leakageTime = itemView.find(R.id.timeline_time)
            notSolvedButton = itemView.find(R.id.btn_acton_not_solved)
            solvedButton = itemView.find(R.id.btn_acton_solved)

        }


        fun bind(item: LineLeakageResponse, listener: OnItemClickListener, context: Context) {

            val inFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            val dateSource = inFormat.parse(item.createdAt.toString())

            val getDay = SimpleDateFormat("EEEE")
            val getDate = SimpleDateFormat("dd-MM-yyyy")
            val getTime = SimpleDateFormat("hh:mm")

            val dateName = getDay.format(dateSource)
            val dateString = getDate.format(dateSource)
            val time = getTime.format(dateSource)


            leakageDay.text = dateName
            leakageDate.text = dateString
            leakageTime.text = time
            lineName.text = item.lineData!!.name!!

            if (item.solved!! == 1) {
                notSolvedButton.visibility = View.GONE
                solvedButton.visibility = View.VISIBLE
            } else {
                notSolvedButton.visibility = View.VISIBLE
                solvedButton.visibility = View.GONE

                notSolvedButton.setOnClickListener(View.OnClickListener { v: View? ->
                    listener.onHorizontalItemClick(item.leakId!!)

                })
            }


        }

    }
}


