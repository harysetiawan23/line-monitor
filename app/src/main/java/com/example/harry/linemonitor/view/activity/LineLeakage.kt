package com.example.harry.linemonitor.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import com.example.harry.linemonitor.R
import com.example.harry.linemonitor.data.LeakageMaster
import com.example.harry.linemonitor.data.LineMasterMap
import com.example.harry.linemonitor.view.adapter.LeakageListAdapter
import com.example.harry.linemonitor.view.contract.LeakageMasterContract
import com.example.harry.linemonitor.view.presenter.LeakageMasterPresenter
import kotlinx.android.synthetic.main.activity_line_leakage.*
import org.jetbrains.anko.ctx

class LineLeakage : AppCompatActivity(), LeakageMasterContract,LeakageListAdapter.OnItemClickListener {
//    Leakage On Item Click Listener
    override fun onHorizontalItemClick(item: LineMasterMap) {

    }


    private lateinit var leakagePresenter: LeakageMasterPresenter
    private lateinit var lineMaster: LineMasterMap
    private  var thisActivity = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_line_leakage)
        toolbar.title = "Leakage List"
        setSupportActionBar(toolbar)


        lineMaster = intent.getSerializableExtra("lineData") as LineMasterMap
        leakagePresenter = LeakageMasterPresenter(ctx, this)

        leakagePresenter.getLeakage(lineMaster.id.toString())

        toolbar.title = "${lineMaster!!.name} Leakage"
        leakage_rv.layoutManager = LinearLayoutManager(ctx,LinearLayout.VERTICAL,false) as RecyclerView.LayoutManager


    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)

    }


    //    Leakage View Contract
    override fun showLoading() {
        progress_bar.visibility = View.VISIBLE

    }

    override fun hideLoading() {
        progress_bar.visibility = View.GONE
    }

    override fun onRetriveDataSuccess(data: List<LeakageMaster?>?) {
        leakage_rv.adapter = LeakageListAdapter(context = ctx,listener = thisActivity,leakageList = data)
    }

    override fun onRefreshData(data: LeakageMaster?) {

    }

    override fun onUpdateSuccess(data: LeakageMaster?) {

    }

    override fun onPostSuccess(data: LeakageMaster?) {

    }

    override fun onDeleteSuccess(data: String) {

    }

    override fun onError(data: String) {

    }


}
