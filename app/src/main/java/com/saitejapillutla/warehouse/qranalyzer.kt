package com.saitejapillutla.warehouse
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentManager
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.lang.IndexOutOfBoundsException

@androidx.camera.core.ExperimentalGetImage
class qranalyzer(val context: Context) : ImageAnalysis.Analyzer {
    private val TAG = "qrscan"
    override fun analyze(imageProxy: ImageProxy) {
        scanBarcode(imageProxy)
    }
    @SuppressLint("UnsafeExperimentalUsageError")
    private fun scanBarcode(imageProxy: ImageProxy) {
        imageProxy.image?.let { image ->
            val inputImage = InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)
            val scanner = BarcodeScanning.getClient()
            scanner.process(inputImage)
                .addOnCompleteListener {
                    imageProxy.close()
                    if (it.isSuccessful) {
                        readBarcodeData(it.result as List<Barcode>)
                    } else {
                        it.exception?.printStackTrace()
                    }
                }
        }
    }
    private fun readBarcodeData(barcodes: List<Barcode>) {
        try{
            Log.d(TAG,"helloooooo working")
            Log.d(TAG,barcodes[0].format.toString())
            Log.d(TAG,barcodes[0].rawValue .toString())
            Log.d(TAG,"helloooooo ${barcodes[0].valueType}")
            val intent= Intent(context,itemdetails::class.java)
            intent.putExtra("orderID",barcodes[0].rawValue .toString())
            intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)

        }catch (e :IndexOutOfBoundsException){

            Log.d(TAG,"NO BAR CODE FOUND!!!!!")
        }

//        for (barcode in barcodes) {
//            when (barcode.valueType) {
//                Barcode.FORMAT_QR_CODE -> {
//                    //if (!bottomSheet.isAdded){
//                        //bottomSheet.show(fragmentManager, "")
//                    //bottomSheet.updateURL(barcode.url?.url.toString())}
//                    Log.d(TAG,"helloooooo ${barcode.valueType}")
//                    Log.d(TAG,barcode.rawValue .toString())
//                }
//            }
//        }



    }
}


@androidx.camera.core.ExperimentalGetImage
class modelqranalyzer(val context: Context) : ImageAnalysis.Analyzer {
    private val TAG = "qrscan"
    override fun analyze(imageProxy: ImageProxy) {
        scanBarcode(imageProxy)
    }
    @SuppressLint("UnsafeExperimentalUsageError")
    private fun scanBarcode(imageProxy: ImageProxy) {
        imageProxy.image?.let { image ->
            val inputImage = InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)
            val scanner = BarcodeScanning.getClient()
            scanner.process(inputImage)
                .addOnCompleteListener {
                    imageProxy.close()
                    if (it.isSuccessful) {
                        readBarcodeData(it.result as List<Barcode>)
                    } else {
                        it.exception?.printStackTrace()
                    }
                }
        }
    }
    private fun readBarcodeData(barcodes: List<Barcode>) {
        try{
            Log.d(TAG,"helloooooo working")
            Log.d(TAG,barcodes[0].format.toString())
            Log.d(TAG,barcodes[0].rawValue .toString())
            Log.d(TAG,"helloooooo ${barcodes[0].valueType}")
            val intent= Intent(context,modelDetails::class.java)
            intent.putExtra("orderID",barcodes[0].valueType.toString())
            context.startActivity(intent)
        }catch (e :IndexOutOfBoundsException){

            Log.d(TAG,"NO BAR CODE FOUND!!!!!")
        }


//        for (barcode in barcodes) {
//            when (barcode.valueType) {
//                Barcode.FORMAT_QR_CODE -> {
//                    //if (!bottomSheet.isAdded){
//                        //bottomSheet.show(fragmentManager, "")
//                    //bottomSheet.updateURL(barcode.url?.url.toString())}
//                    Log.d(TAG,"helloooooo ${barcode.valueType}")
//                    Log.d(TAG,barcode.rawValue .toString())
//                }
//            }
//        }
    }
}


@androidx.camera.core.ExperimentalGetImage
class orderqranalyzer( val context: Context) : ImageAnalysis.Analyzer {
    private val TAG = "qrscan"
    override fun analyze(imageProxy: ImageProxy) {
        scanBarcode(imageProxy)
    }
    @SuppressLint("UnsafeExperimentalUsageError")
    private fun scanBarcode(imageProxy: ImageProxy) {
        imageProxy.image?.let { image ->
            val inputImage = InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)
            val scanner = BarcodeScanning.getClient()
            scanner.process(inputImage)
                .addOnCompleteListener {
                    imageProxy.close()
                    if (it.isSuccessful) {
                        readBarcodeData(it.result as List<Barcode>)
                    } else {
                        it.exception?.printStackTrace()
                    }
                }
        }
    }
    private fun readBarcodeData(barcodes: List<Barcode>) {
        try{
            Log.d(TAG,"helloooooo working")
            Log.d(TAG,barcodes[0].format.toString())
           // Log.d(TAG,barcodes[0].rawValue .toString())
            val codeValue=barcodes[0].valueType
            Log.d(TAG,"helloooooo ${codeValue}")
            val intent= Intent(context,orderDetails::class.java)
            intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("orderID",barcodes[0].valueType.toString())
            context.startActivity(intent)
            //overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
        }catch (e :IndexOutOfBoundsException){

            Log.d(TAG,"NO BAR CODE FOUND!!!!!")
        }


//        for (barcode in barcodes) {
//            when (barcode.valueType) {
//                Barcode.FORMAT_QR_CODE -> {
//                    //if (!bottomSheet.isAdded){
//                        //bottomSheet.show(fragmentManager, "")
//                    //bottomSheet.updateURL(barcode.url?.url.toString())}
//                    Log.d(TAG,"helloooooo ${barcode.valueType}")
//                    Log.d(TAG,barcode.rawValue .toString())
//                }
//            }
//        }
    }
}

