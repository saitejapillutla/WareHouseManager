package com.saitejapillutla.warehouse

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_all_models.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.*
import java.io.Serializable
import kotlin.coroutines.CoroutineContext

class HomeActivity : AppCompatActivity(), CoroutineScope {
    override val coroutineContext: CoroutineContext
    get() = Dispatchers.Main + job
    private lateinit var job: Job
    private  val TAG = "HomeActivity"

    var uniqueID=""
    var name=""
    var email=""
    var uid=""
    var photoURL=""
    var status=""
    var role=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        try {
            val userdata = intent.getSerializableExtra("userdata") as Map<String, Any>
            uniqueID = userdata.getValue("uniqueID").toString()
            uid = userdata.getValue("uid").toString()
            role = userdata.getValue("role").toString()
            savedInstanceState?.putCharSequence("uniqueID", uniqueID)
            savedInstanceState?.putCharSequence("uid", uid)
            savedInstanceState?.putCharSequence("role", role)
        }catch (e: NullPointerException){
            uniqueID =savedInstanceState?.getCharSequence("uniqueID").toString()
            uid=savedInstanceState?.getCharSequence("uid").toString()
            role=savedInstanceState?.getCharSequence("role").toString()
        }
        profileBtn.setOnClickListener {
            val intent= Intent(this,ProfileActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
        }
        membersBtn.setOnClickListener {
            val intent= Intent(this,Members::class.java)
            intent.putExtra("uniqID",uniqueID)
            intent.putExtra("uid",uid)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
        }
        job = Job()
        if(intent.getIntExtra("orderupdated",0)==1){
            Snackbar.make(home_activity_constraint, "Order Created", Snackbar.LENGTH_LONG).setBackgroundTint(resources.getColor(
                R.color.purple_200
            )).show()}
        if(role=="Administrator"){
            launch(Dispatchers.Main) {
               async {  getrequestCount() }
               async {  getOrderDetails() }
            }

        }
        if((role=="Administrator")||(role=="Employee")){
            addingLayout.visibility=View.VISIBLE
        }else{
            addingLayout.visibility=View.GONE
        }
        card_requests.setOnClickListener {
            val intent= Intent(this,Members::class.java)
            intent.putExtra("uniqID",uniqueID)
            intent.putExtra("uid",uid)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
        }
        qrscanBtn.setOnClickListener {
            val intent= Intent(this,qrscan::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
        }
        addModel.setOnClickListener {
            val intent= Intent(this,createModel::class.java)
            intent.putExtra("uniqID",uniqueID)
            intent.putExtra("uid",uid)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
        }
        allmodelsopen.setOnClickListener {
            val intent= Intent(this,com.saitejapillutla.warehouse.allModels::class.java)
            intent.putExtra("uniqID",uniqueID)
            intent.putExtra("uid",uid)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
        }
        addOrder.setOnClickListener {
            val intent = Intent(this,com.saitejapillutla.warehouse.addOrder::class.java)
            intent.putExtra("uniqueID",uniqueID)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
        }
        toBeInvoiced.setOnClickListener {
            val intent = Intent(this,InvoicesList::class.java)
            intent.putExtra("uniqueID",uniqueID)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
        }
    }
    suspend fun getOrderDetails(){
        val requestref = FirebaseFirestore.getInstance()
        requestref.collection("warehouses").document(uniqueID)
            .collection("Orders").addSnapshotListener { value, error ->
                if (value != null) {
                    var invoiced=0
                    var shipped=0
                    var delivered=0
                    for(doc in value){
                        Log.d(TAG,doc.data.getValue("warehouse").toString())
                        if (doc.data.getValue("warehouse").toString()==uniqueID){
                            invoiced ++
                        }
                        if (doc.data.getValue("status").toString()=="packed"){
                            shipped++
                        }
                        if((doc.data.getValue("currentPoint").toString()==uniqueID)){
                            delivered++
                        }
                    }
                toBeInvoiced_db.text= invoiced.toString()
                toBeShipped_db.text=shipped.toString()
                toBeDelivered_db.text=delivered.toString()}
        }
    }
    override fun onStart() {
        super.onStart()
    }
    suspend fun getrequestCount(){
                val requestref = FirebaseFirestore.getInstance()
        requestref.collection("warehouses").document(uniqueID).collection("requests").whereEqualTo("uniqueID",uniqueID)
            .addSnapshotListener { it, error ->

                requests.text= it?.size().toString()
                card_requests.visibility=View.VISIBLE
            }    }
    override fun onDestroy() {
        job.cancel()
        super.onDestroy()    }


}