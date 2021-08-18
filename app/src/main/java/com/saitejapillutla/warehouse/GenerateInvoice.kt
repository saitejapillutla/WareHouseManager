package com.saitejapillutla.warehouse

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_generate_invoice.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext

class GenerateInvoice : AppCompatActivity() {


    val TAG ="GenerateInvoice"
    val coroutineContext: CoroutineContext
        get() = Dispatchers.IO +job
    private lateinit var job: Job
    var addresss=""
    var aphone=""
    var aemail=""
    var azipcode=""
    var amodelID =""
    var acategory=""
    var aquantity=""
    var aunitsAvailable =""
    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_invoice)
        val modelID =intent.getCharSequenceExtra("modelID");
        val wareHouseID =intent.getCharSequenceExtra("wareHouseID");
        val uid =intent.getCharSequenceExtra("uid");
        job = Job()
        CoroutineScope(newSingleThreadContext("modelDetails")).launch(Dispatchers.Main){
            if (wareHouseID != null) {
              val  modelDetails= async { modelDetails(wareHouseID as String, modelID as String)}
              val orderDetails = async{ orderDetails(wareHouseID as String, uid as String)}
                val warehouseDetails =async { wareouseDetails(wareHouseID as String) }
                if((orderDetails.await()==modelDetails.await())==warehouseDetails.await()){
                    linearProgressIndicator2.visibility= View.GONE
                    scrollView2.visibility= View.VISIBLE
                    Log.d(TAG,"in side Coroutineeeeeeeeeeeeeeeeeeee")
                }
            }
        }
        generateInvoice.setOnClickListener {
        CoroutineScope(newSingleThreadContext("modelDetails")).launch(Dispatchers.Main){
            updateOrder(wareHouseID as String, uid as String)
        }}
    }
    suspend fun updateOrder(wareHouseID:String,uid :String){
        val itemId =""+ Calendar.getInstance().timeInMillis
        val data = hashMapOf(
            "status" to "packed",
            "currentPoint" to wareHouseID,
            "itemID" to itemId,
        )
        val remaining = hashMapOf(
            "unitsAvailable" to ""+(aunitsAvailable.toInt()-aquantity.toInt())

        )
        val requestref = FirebaseFirestore.getInstance()
        requestref.collection("warehouses").document(wareHouseID)
            .collection("Orders").document(uid).update(data as Map<String, Any>).addOnSuccessListener {

                requestref.collection("warehouses").document(wareHouseID)
                    .collection("models").document(amodelID)
                    .update(remaining as Map<String, Any>).addOnSuccessListener {
                    val intent = Intent(this,invoiceQRGrenerator::class.java)
                intent.putExtra("itemId",itemId)
                intent.putExtra("wareHouseID",wareHouseID)
                intent.putExtra("uid",uid)
                intent.putExtra("addresss",addresss)
                intent.putExtra("aphone",aphone)
                intent.putExtra("aemail",aemail)
                intent.putExtra("azipcode",azipcode)
                intent.putExtra("amodelID",amodelID)
                intent.putExtra("acategory",acategory)
                    startActivity(intent)
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
            }}
    }
    suspend fun modelDetails( wareHouseID:String,modelID :String):Boolean{
        val requestref = FirebaseFirestore.getInstance()
        requestref.collection("warehouses").document(wareHouseID)
            .collection("models").document(modelID).addSnapshotListener { value, error ->
                if (value != null) {
                    textView64.text= value.data?.getValue("category") as String?
                    toBeShipped_db.text= value.data?.getValue("unitsAvailable") as String?
                    textView62.text= value.data?.getValue("modelID") as String?
                    amodelID= value.data?.getValue("modelID") as String
                    acategory=value.data?.getValue("category") as String
                    aunitsAvailable= value.data?.getValue("unitsAvailable") as String

                }
            }
        return true
    }

    suspend fun orderDetails(wareHouseID:String,uid :String):Boolean{
        val requestref = FirebaseFirestore.getInstance()
        requestref.collection("warehouses").document(wareHouseID)
            .collection("Orders").document(uid).addSnapshotListener { value, error ->
                if (value != null) {
                    toBeDelivered_db.text=value.data?.getValue("quantity") as String?
                    address.setText( value.data?.getValue("address") as String?)
                    zipcode.setText( value.data?.getValue("pincode") as String?)
                    cphone.setText( value.data?.getValue("phone") as String?)
                    cemail.setText( value.data?.getValue("email") as String?)
                    textView59.text= value.data?.getValue("itemName") as String?
                    addresss= (value.data?.getValue("address")).toString()
                    aphone= (value.data?.getValue("phone")).toString()
                    aemail= (value.data?.getValue("email")).toString()
                    azipcode= (value.data?.getValue("pincode")).toString()
                    aquantity=(value.data?.getValue("quantity")).toString()

                }}
        return true
    }

    suspend fun wareouseDetails(wareHouseID:String):Boolean{
        val requestref = FirebaseFirestore.getInstance()
        requestref.collection("warehouses").document(wareHouseID).addSnapshotListener { value, error ->
            if (value != null) {
                addres.setText(value.data?.getValue("address") as String?)
                zipcoe.setText(value.data?.getValue("zipcode") as String?)
                cpone.setText(value.data?.getValue("cphone") as String?)
                ceail.setText(value.data?.getValue("cemail") as String?)
            }
        }
return true
    }
    override fun onDestroy() {
        job.cancel()
        super.onDestroy()    }
}