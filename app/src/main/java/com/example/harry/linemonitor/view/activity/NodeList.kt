package com.example.harry.linemonitor.view.activity

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.harry.linemonitor.R
import com.example.harry.linemonitor.data.NodeMaster
import com.example.harry.linemonitor.view.adapter.NodeListAdapter
import com.example.harry.linemonitor.view.contract.NodeListContract
import com.example.harry.linemonitor.view.presenter.NodeListPresenter

import kotlinx.android.synthetic.main.activity_node_list.*
import org.jetbrains.anko.ctx

class NodeList : AppCompatActivity(),NodeListContract {


    private lateinit var nodePresenter:NodeListPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_node_list)
        toolbar.title = ""
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        nodePresenter = NodeListPresenter(this)
        nodePresenter.retriveNodeFromServer()


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_list,menu)
        return super.onCreateOptionsMenu(menu)
    }



    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun showLoading() {
       progress_bar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progress_bar.visibility = View.GONE
    }

    override fun onSuccess(data: List<NodeMaster?>?) {
        rv_node_list.adapter = NodeListAdapter(data,ctx)
    }

    override fun onError(data: String) {

    }

}
