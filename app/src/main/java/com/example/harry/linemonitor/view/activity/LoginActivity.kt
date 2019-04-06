package com.example.harry.linemonitor.view.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.harry.linemonitor.R
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        startActivity<LandingActivity>()


        login_button.onClick {
            finish()
        }
    }
}
