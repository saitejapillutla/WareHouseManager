package com.saitejapillutla.warehouse

import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.android.synthetic.main.activity_invoice_qrgrenerator.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class invoiceQRGrenerator : AppCompatActivity() {
    val TAG ="invoiceQRGrenerator"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice_qrgrenerator)

        address.setText( intent.getCharSequenceExtra("addresss")  )
        zipcode.setText( intent.getCharSequenceExtra("azipcode")  )
        cphone.setText( intent.getCharSequenceExtra("aphone")  )
        cemail.setText( intent.getCharSequenceExtra("aemail")  )
        textView53.setText( intent.getCharSequenceExtra("amodelID")  )
        textView52.setText( intent.getCharSequenceExtra("acategory")  )
        textView56.setText( intent.getCharSequenceExtra("itemId")  )
        textView58.setText( intent.getCharSequenceExtra("uid")  )




        SetQR(imageView8, intent.getCharSequenceExtra("itemId") as String)
        SetQR(imageView9, intent.getCharSequenceExtra("uid") as String)

    }
    fun SetQR(image :ImageView,content :String){
         ////content = intent.getCharSequenceExtra("itemId")

        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content as String?, BarcodeFormat.QR_CODE, 512, 512)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
            }
        }
        image.setImageBitmap(bitmap)
    }



}