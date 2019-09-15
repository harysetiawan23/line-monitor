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
import com.example.harry.linemonitor.data.LineLeakageResponse
import com.example.harry.linemonitor.data.LineMasterMap
import com.example.harry.linemonitor.data.SolvedSuccessMessage
import com.example.harry.linemonitor.view.adapter.LeakageListAdapter
import com.example.harry.linemonitor.view.contract.LeakageMasterContract
import com.example.harry.linemonitor.view.contract.SolveLeakageContract
import com.example.harry.linemonitor.view.presenter.LeakSuccessPresenter
import com.example.harry.linemonitor.view.presenter.LeakageMasterPresenter
import kotlinx.android.synthetic.main.activity_line_leakage.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.toast

class LineLeakage : AppCompatActivity(), LeakageMasterContract, LeakageListAdapter.OnItemClickListener, SolveLeakageContract {
    override fun onUpdateSuccess(data: SolvedSuccessMessage) {


    }

    //    Leakage On Item Click Listener
    override fun onHorizontalItemClick(item: String) {
        solveLeakagePresenter.updateLeakageIntoSuccess(item)
        finish()
        toast("Solved Status Checker Running")
    }


    private lateinit var leakagePresenter: LeakageMasterPresenter
    private lateinit var solveLeakagePresenter: LeakSuccessPresenter
    private lateinit var lineMaster: LineMasterMap
    private  var thisActivity = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_line_leakage)
        toolbar.title = "Leakage List"
        setSupportActionBar(toolbar)


        lineMaster = intent.getSerializableExtra("lineData") as LineMasterMap
        leakagePresenter = LeakageMasterPresenter(this, this)
        solveLeakagePresenter = LeakSuccessPresenter(this, this)



        toolbar.title = "${lineMaster.name} Leakage"
        leakage_rv.layoutManager = LinearLayoutManager(this,LinearLayout.VERTICAL,false)


    }


    override fun onResume() {
        super.onResume()
        leakagePresenter.getLeakage(lineMaster.id.toString())
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

    override fun onRetriveDataSuccess(data: List<LineLeakageResponse?>?) {
        leakage_rv.adapter = LeakageListAdapter(context = this,listener = thisActivity,leakageList = data)
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
        finish()
    }


}
