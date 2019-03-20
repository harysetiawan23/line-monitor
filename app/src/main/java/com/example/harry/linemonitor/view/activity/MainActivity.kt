package com.example.harry.linemonitor.view.activity

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import com.example.harry.linemonitor.R
import com.example.harry.linemonitor.data.LineMaster
import com.example.harry.linemonitor.view.adapter.LineListAdapter
import com.example.harry.linemonitor.view.contract.LineListContract
import com.example.harry.linemonitor.view.presenter.LineListPresenter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity(), LineListContract , AdapterView.OnItemClickListener {
    private lateinit var lineMasterList:List<LineMaster?>

    private lateinit var lineListPresenter: LineListPresenter


    //    Line Master Contract
    override fun showLoading() {
        progress_bar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progress_bar.visibility = View.GONE
    }

    override fun onSuccess(data: List<LineMaster?>?) {
        lineMasterList = data!!
        rv_line_list.adapter = LineListAdapter(data,ctx)
        rv_line_list.setHeaderDividersEnabled(true)
        rv_line_list.setOnItemClickListener(this)
    }

    override fun onError(data: String) {
        progress_bar.visibility = View.GONE
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        lineListPresenter = LineListPresenter(this)
        lineListPresenter.retriveLineFromServer()







        fab.setOnClickListener { view ->
            lineListPresenter.retriveLineFromServer()
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)

        }
    }



    //Line List On Click
    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        startActivity<LineDataV2>("lineId" to lineMasterList.get(position))
    }
}
