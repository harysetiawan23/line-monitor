package com.example.harry.linemonitor.view.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView
import org.jetbrains.anko.ctx
import android.media.ToneGenerator
import android.media.AudioManager
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.encoder.QRCode


class Scanner : AppCompatActivity(), ZXingScannerView.ResultHandler {

    private lateinit var mScannerView: ZXingScannerView
    private lateinit var REQUEST_CODE:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mScannerView = ZXingScannerView(ctx)

        setContentView(mScannerView)
        REQUEST_CODE = intent.getStringExtra("requestCode")



        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE.toInt())
            } else {

            }
        } else {

        }





    }

    public override fun onResume() {
        super.onResume()
        mScannerView.setResultHandler(this) // Register ourselves as a handler for scan results.
        mScannerView.startCamera()          // Start camera on resume
    }

    public override fun onPause() {
        super.onPause()
        mScannerView.stopCamera()           // Stop camera on pause
    }


    override fun handleResult(rawResult: Result?) {
        // Do something with the result here
        mScannerView.stopCamera()
        val toneGen1 = ToneGenerator(AudioManager.STREAM_ALARM, 100)
        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150)

        Log.d("SCANRESULT", "REQUEST CODE = $REQUEST_CODE")
        Log.d("SCANRESULT", rawResult!!.getText()); // Prints scan results
        Log.d("SCANRESULT", rawResult!!.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)



        mScannerView.resumeCameraPreview(this);

        val intent = Intent()
        var message = rawResult!!.getText()
        intent.putExtra("SCAN_RESULT", message)
        setResult(REQUEST_CODE.toInt(), intent)
        finish()//finishing activity
    }


}
