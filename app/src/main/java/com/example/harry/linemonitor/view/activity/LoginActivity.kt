package com.example.harry.linemonitor.view.activity

import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.example.harry.linemonitor.R
import com.example.harry.linemonitor.data.LoginResponse
import com.example.harry.linemonitor.helper.PreferencesUtility
import com.example.harry.linemonitor.view.contract.UserMasterContract
import com.example.harry.linemonitor.view.presenter.UserMasterPresenter
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity

class LoginActivity : AppCompatActivity(), UserMasterContract{


    private lateinit var userMasterPresenter:UserMasterPresenter
    private lateinit var userData:LoginResponse
    private lateinit var thisActivity:UserMasterContract


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        userData = PreferencesUtility.getUserData(this)

        try {

            if (!userData.token!!.isEmpty()) {
                startActivity<LandingActivity>()
                finish()
            }
        } catch (e: Exception) {

        }


        thisActivity = this


        progress_bar.visibility = View.GONE
        userMasterPresenter = UserMasterPresenter(this)

        login_button.onClick {
            var email = et_user_email.text.toString()
            var password = et_user_password.text.toString()


            userMasterPresenter.userLogin(thisActivity,email,password,FirebaseInstanceId.getInstance().token!!)


        }

        register_button.onClick{
            startActivity<Register>()
            finish()
        }
    }


    override fun showLoading() {
        progress_bar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progress_bar.visibility = View.GONE
    }

    override fun onLoginSuccess(data: LoginResponse?) {
        if (data!!.id.toString().isEmpty()) {

            var alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("Login")
            alertDialog.setMessage(data.token)

            alertDialog.setNegativeButton("Dismiss", DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
            })

            var dialogData: AlertDialog =  alertDialog.create()

            dialogData.show()
            dialogData.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(this.getColor(R.color.google_blue))
            dialogData.getButton(AlertDialog.BUTTON_NEGATIVE)
            dialogData.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(this.getColor(R.color.google_blue))

            dialogData.show()
        }else{
            PreferencesUtility.saveUserData(this,data)
            startActivity<LandingActivity>()
            finish()
        }
        Log.d("USER_DATA",data.toString())
    }

    override fun onError(data: String?) {

        try {
            Log.e("LoginError", data.toString())
//            var alertDialog = AlertDialog.Builder(ctx)
//            alertDialog.setTitle("Login")
//            alertDialog.setMessage(data)
//
//            alertDialog.setNegativeButton("Dismiss", DialogInterface.OnClickListener { dialog, which ->
//                dialog.dismiss()
//            })
//
//            var dialogData: AlertDialog = alertDialog.create()
//
//            dialogData.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ctx.getColor(R.color.google_blue))
//            dialogData.show()
        } catch (e: Exception) {
            Log.e("LoginError", e.toString())
        }



    }

}
