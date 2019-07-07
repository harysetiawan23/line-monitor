package com.example.harry.linemonitor.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.harry.linemonitor.R
import kotlinx.android.synthetic.main.activity_profile.*


class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)


        toolbar.title = "Profile"
        setSupportActionBar(toolbar)
    }
}
