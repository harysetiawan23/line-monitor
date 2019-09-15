package com.example.harry.linemonitor.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import com.example.harry.linemonitor.R
import com.example.harry.linemonitor.data.NodeMaster
import com.example.harry.linemonitor.data.ResponseDeleteSuccess
import com.example.harry.linemonitor.view.adapter.NodeAdapter
import com.example.harry.linemonitor.view.contract.NodeMasterContract
import com.example.harry.linemonitor.view.presenter.NodeMasterPresenter

import kotlinx.android.synthetic.main.activity_node_list.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.startActivity

class NodeList : AppCompatActivity(),NodeMasterContract, NodeAdapter.OnItemClickListener {



    private lateinit var nodePresenter:NodeMasterPresenter
    private val thisActivity = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_node_list)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.title = "Node List"
        nodePresenter = NodeMasterPresenter(this,this)


        rv_node_list.layoutManager = LinearLayoutManager(this,LinearLayout.VERTICAL,false)
    }


    override fun onStart() {
        super.onStart()
        nodePresenter.retriveNodeFromServer()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_node_list,menu)
        return super.onCreateOptionsMenu(menu)
    }




    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home -> finish()
            R.id.add_node -> startActivity<AddNode>()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun showLoading() {
       progress_bar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progress_bar.visibility = View.GONE
    }

    override fun onRetriveNodeListSuccess(data: List<NodeMaster?>?) {

        rv_node_list.adapter = NodeAdapter(this,data,thisActivity)
    }

    override fun onError(data: String) {

    }
    override fun onSubmitSuccess(data: NodeMaster) {

    }

    override fun onUpdate(data: NodeMaster) {

    }

    override fun onDeleteSuccess(data: ResponseDeleteSuccess) {

    }


    //OnNodeSelectedListener
    override fun NodeItemClickListener(nodeData: NodeMaster) {
        startActivity<NodeStream>("nodeData" to nodeData)
    }



}
