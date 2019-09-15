package com.example.harry.linemonitor.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.harry.linemonitor.R
import com.example.harry.linemonitor.data.RegisterResponse
import com.example.harry.linemonitor.helper.PreferencesUtility
import com.example.harry.linemonitor.view.contract.UserRegisterContact
import com.example.harry.linemonitor.view.presenter.UserMasterPresenter
import com.example.harry.linemonitor.view.presenter.UserRegister
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.startActivity

class Register : AppCompatActivity(), UserRegisterContact, View.OnClickListener {
    private lateinit var registerPresenter: UserRegister
    private lateinit var thisActivity: UserRegister


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        progress_bar.visibility = View.GONE

        signup_button.setOnClickListener(this)

        registerPresenter = UserRegister(this,this)
        login_button.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.signup_button -> {
                Log.d("REGISTER", "${et_user_name.text} - ${et_user_email.text} - ${et_user_password.text} $et_user_c_password")

                var username = et_user_name.text.toString()
                var email = et_user_email.text.toString()
                var password = et_user_password.text.toString()
                var c_password  = et_user_c_password.text.toString()

                registerPresenter.userRegister(email,password,c_password,username)

            }

            R.id.login_button -> {
                finish()
                startActivity<LoginActivity>()
            }
        }
    }


    override fun showLoading() {
        progress_bar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progress_bar.visibility = View.GONE
    }

    override fun onRegisterSuccess(data: RegisterResponse?) {
        Toast.makeText(this, "SUCCESS", Toast.LENGTH_SHORT).show()
        Log.d("REGISTRATIONDATA",data.toString())

        PreferencesUtility.saveUsetDataFromRegister(this,data!!)
        startActivity<LandingActivity>()
        finish()
    }

    override fun onError(data: String?) { Toast.makeText(this, "ERROR + $data", Toast.LENGTH_SHORT).show()
        Log.d("TEST_ERROR",data.toString())
    }

}
