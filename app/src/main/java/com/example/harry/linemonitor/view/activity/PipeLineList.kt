package com.example.harry.linemonitor.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import com.example.harry.linemonitor.R
import com.example.harry.linemonitor.data.LineMaster
import com.example.harry.linemonitor.data.LineMasterMap
import com.example.harry.linemonitor.view.adapter.PipelineListAdapter
import com.example.harry.linemonitor.view.contract.LineMasterContract
import com.example.harry.linemonitor.view.presenter.LineMasterPresenter
import kotlinx.android.synthetic.main.activity_pipe_line_list.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.startActivity

class PipeLineList : AppCompatActivity(), LineMasterContract, PipelineListAdapter.OnItemClickListener {



    private val thisActivity = this
    private lateinit var linePresenter: LineMasterPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pipe_line_list)
        toolbar.title = "Pipeline List"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        linePresenter = LineMasterPresenter(this,this)
        rv_pipe_line_list.layoutManager = LinearLayoutManager(this,LinearLayout.VERTICAL,false)

    }


    override fun onResume() {
        super.onResume()
        linePresenter.retriveLineFromServer()
    }



//    Line Master Contract

    override fun showLoading() {
        progress_bar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progress_bar.visibility = View.GONE
    }

    override fun onRetriveDataSuccess(data: List<LineMasterMap?>?) {
        rv_pipe_line_list.adapter = PipelineListAdapter(this,data,thisActivity)
    }

    override fun onPostSuccess(data: LineMaster?) {

    }

    override fun onDeleteSuccess(data: String) {

    }

    override fun onError(data: String) {

    }

    override fun onRefreshData(data: LineMasterMap?) {

    }

    override fun onUpdateSuccess(data: LineMaster?) {

    }







    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_line_list, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onHorizontalItemClick(item: LineMasterMap) {

    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            android.R.id.home -> finish()

            R.id.add_pipeline -> {
                startActivity<AddPipeline>()
            }
        }

        return super.onOptionsItemSelected(item)

    }


}
