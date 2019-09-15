package com.example.harry.linemonitor.view.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.example.harry.linemonitor.R
import com.example.harry.linemonitor.data.NodeMaster
import org.jetbrains.anko.find

class NodeAdapter(context: Context, nodeData: List<NodeMaster?>?, private val listener: OnItemClickListener) : RecyclerView.Adapter<NodeAdapter.ViewHolder>() {
    private val context = context
    private val nodeData = nodeData


    interface OnItemClickListener {
        fun NodeItemClickListener(nodeData: NodeMaster)
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.node_list_item, parent,false))
    }

    override fun getItemCount(): Int {
        if (nodeData!!.size == 0) {
            return 0
        } else {
            return nodeData.size
        }
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, p1: Int) {
        viewHolder.bind(nodeData!!.get(p1)!!, listener)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nodeSn: TextView
        private val nodePhone: TextView
        private val nodeLat: TextView
        private val nodeLng: TextView
        private val nodeCv: LinearLayout

        init {
            nodeSn = itemView.find(R.id.tv_node_sn)
            nodePhone = itemView.find(R.id.tv_node_phone)
            nodeLat = itemView.find(R.id.tv_node_lat)
            nodeLng = itemView.find(R.id.tv_node_lng)
            nodeCv = itemView.find(R.id.cv_node_card)

        }


        fun bind(nodeData: NodeMaster, listener: OnItemClickListener) {
            nodeSn.text = nodeData.sn
            nodePhone.text = nodeData.phoneNumber
            nodeLat.text = nodeData.lat
            nodeLng.text = nodeData.lng


            itemView.setOnClickListener { listener.NodeItemClickListener(nodeData) }
        }


    }

}

