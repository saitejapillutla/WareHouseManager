package com.saitejapillutla.warehouse

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.android.synthetic.main.activity_qrscan.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
@androidx.camera.core.ExperimentalGetImage
class qrscan : AppCompatActivity() {
    private val TAG = "qrscan"
    private val CAMERA_REQUEST_CODE = 101
    private var permissions =false;
    private var modelscan =0;

    //build our camera.
    private lateinit var analyzer: qranalyzer
    private lateinit var modelqranalyzer: modelqranalyzer
    private lateinit var orderqranalyzer: orderqranalyzer
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrscan)
        cameraExecutor = Executors.newSingleThreadExecutor()
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        analyzer = qranalyzer(baseContext)
        modelqranalyzer=modelqranalyzer(baseContext)
        orderqranalyzer=orderqranalyzer(baseContext)
        qrtypeSelectDialog()

        this.window.setFlags(1024,720);

        verifyqr.setOnClickListener {
          val qrinput=  qrinput.text

            when(qrinput.length){
                10->{
                    val intent =Intent(this,modelDetails::class.java)
                    intent.putExtra("orderID",qrinput)
                    intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
                14->{
                    val intent =Intent(this,modelDetails::class.java)
                    intent.putExtra("orderID",qrinput)
                    intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)

                }
                12->{ val intent =Intent(this,modelDetails::class.java)
                    intent.putExtra("orderID",qrinput)
                    intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
                else->{MaterialAlertDialogBuilder(this)
                    .setTitle("Invalid Scan Key")
                    .setIcon(R.drawable.ic_baseline_error_outline_24)
                    .setMessage("Please verify your code once and try again !!")
                    .show()}
            }
        }
    }

    fun qrtypeSelectDialog(){
        val singleItems = arrayOf("Item QR Code", "Model QR Code", "Order QR Code")

        MaterialAlertDialogBuilder(this)
            .setTitle("Select your QR type.")
            .setIcon(R.drawable.qr_code_scanner)
            .setPositiveButton("Scan",DialogInterface.OnClickListener { dialog, id ->
                setupPermissions()
                if(permissions){
                    cameraProviderFuture.addListener(Runnable {
                        val cameraProvider = cameraProviderFuture.get()
                        bindPreview(cameraProvider)
                    }, ContextCompat.getMainExecutor(this))
                }
            })
            .setSingleChoiceItems(singleItems,0) { dialog, which ->
                Log.d(TAG, "Selected ${which}")
                modelscan = which
            }
            .setCancelable(false)
            .show()
    }
    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val preview: Preview = Preview.Builder()
            .build()
        val cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
        preview.setSurfaceProvider(previewView.surfaceProvider)

        cameraProvider.bindToLifecycle(this as LifecycleOwner,cameraSelector,preview )
        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(1280, 720))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        when(modelscan){
            0->imageAnalysis.setAnalyzer(cameraExecutor, analyzer)
            1->imageAnalysis.setAnalyzer(cameraExecutor, modelqranalyzer)
            2->imageAnalysis.setAnalyzer(cameraExecutor, orderqranalyzer)
        }
        cameraProvider.bindToLifecycle(
            this as LifecycleOwner,
            cameraSelector,
            imageAnalysis,
            preview
        )
    }
    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.CAMERA)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permission to record denied")
            makeRequest()
        }else{
            Log.d(TAG, "Permission to record Accepted")
            permissions=true
        }
    }
    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_REQUEST_CODE)
    }
    override fun onRequestPermissionsResult(requestCode: Int,permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i(TAG, "Permission has been denied by user")
                } else {
                    Log.i(TAG, "Permission has been granted by user")
                    this.permissions =true
                }
            }
        }
    }
}



