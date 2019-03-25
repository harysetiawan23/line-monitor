package com.example.harry.linemonitor.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.harry.linemonitor.R
import com.example.harry.linemonitor.data.LineMaster
import com.example.harry.linemonitor.data.LineMasterMap
import com.example.harry.linemonitor.view.adapter.LineListAdapter
import com.example.harry.linemonitor.view.contract.LineListContract
import com.example.harry.linemonitor.view.contract.LineMapContract
import com.example.harry.linemonitor.view.presenter.LineListPresenter
import com.example.harry.linemonitor.view.presenter.LineMapsPresenter
import kotlinx.android.synthetic.main.activity_pipe_line_list.*
import org.jetbrains.anko.ctx

class PipeLineList : AppCompatActivity(), LineMapContract {


    private lateinit var linePresenter: LineMapsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pipe_line_list)
        toolbar.title = ""
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        linePresenter = LineMapsPresenter(this)


    }


    override fun onResume() {
        super.onResume()
        linePresenter.retriveLineFromServer()
    }


    override fun showLoading() {
        progress_bar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progress_bar.visibility = View.GONE
    }

    override fun onSuccess(data: List<LineMasterMap?>?) {
        rv_pipe_line_list.adapter = LineListAdapter(data, ctx)
    }

    override fun onError(data: String) {

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_list,menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            android.R.id.home -> finish()

            R.id.new_list ->{

            }
        }

        return super.onOptionsItemSelected(item)

    }


}
