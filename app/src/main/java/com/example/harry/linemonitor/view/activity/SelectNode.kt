package com.example.harry.linemonitor.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.harry.linemonitor.R
import com.example.harry.linemonitor.data.LineMasterMap
import com.example.harry.linemonitor.data.NodeMaster
import com.example.harry.linemonitor.data.ResponseDeleteSuccess
import com.example.harry.linemonitor.view.adapter.NodeListAdapter
import com.example.harry.linemonitor.view.contract.NodeMasterContract
import com.example.harry.linemonitor.view.presenter.NodeMasterPresenter
import kotlinx.android.synthetic.main.activity_select_node.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.startActivity

class SelectNode : AppCompatActivity(), NodeMasterContract {

    private lateinit var nodePresenter: NodeMasterPresenter

    private var isStartRequired:Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_node)


        toolbar.title = "Select Node"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        nodePresenter = NodeMasterPresenter(this,this)
        isStartRequired = intent.getSerializableExtra("isStartNodeRequired") as Int
        Log.i("IsStartNode",isStartRequired.toString())
    }

    override fun onStart() {
        super.onStart()
        nodePresenter.retriveNodeFromServer()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_node_list, menu)
        return super.onCreateOptionsMenu(menu)
    }



    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
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
        rv_node_list.adapter = NodeListAdapter(data!!.filter { nodeMaster -> nodeMaster!!.isStartNode!!.equals(isStartRequired) }, this)
        rv_node_list.setOnItemClickListener { parent, view, position, id ->
            var intent = Intent()
            intent.putExtra("nodeResult", data!!.get(position))
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    override fun onError(data: String) {

    }

    override fun onSubmitSuccess(data: NodeMaster) {

    }

    override fun onUpdate(data: NodeMaster) {

    }

    override fun onDeleteSuccess(data: ResponseDeleteSuccess) {

    }


}
