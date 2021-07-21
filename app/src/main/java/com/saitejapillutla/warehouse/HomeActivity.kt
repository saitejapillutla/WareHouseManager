package com.saitejapillutla.warehouse

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.*
import java.io.Serializable
import kotlin.coroutines.CoroutineContext

class HomeActivity : AppCompatActivity(), CoroutineScope {
    override val coroutineContext: CoroutineContext
    get() = Dispatchers.Main + job
    private lateinit var job: Job
    private  val TAG = "HomeActivity"

    companion object{

        lateinit var userdata :HashMap<String,Any>
    }

    var uniqueID=""
    var name=""
    var email=""
    var uid=""
    var photoURL=""
    var status=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)



        var  userdata  = intent.getSerializableExtra("userdata") as Map<String,Any>
        Log.d(TAG, userdata.get("fullname") as String)

        profileBtn.setOnClickListener {
            val intent= Intent(this,ProfileActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
        }

        userdata=  intent.getSerializableExtra("userdata") as Map<String, Any>
        uniqueID=userdata.getValue("uniqueID").toString()
        membersBtn.setOnClickListener {
            val intent= Intent(this,Members::class.java)
            intent.putExtra("uniqID",userdata.getValue("uniqueID").toString())
            intent.putExtra("uid",userdata.getValue("uid").toString())
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)

        }
        job = Job()
        if(userdata.getValue("uid").toString()=="Administrator"){
            launch(Dispatchers.Main) {
                getrequestCount()

            }
            launch(Dispatchers.Main) {
                getrequestCount()

            }
        }


        card_requests.setOnClickListener {
            val intent= Intent(this,Members::class.java)
            intent.putExtra("uniqID",userdata.getValue("uniqueID").toString())
            intent.putExtra("uid",userdata.getValue("uid").toString())
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
        }

        qrscanBtn.setOnClickListener {
            val intent= Intent(this,qrscan::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
        }
    }

    override fun onStart() {
        super.onStart()

    }

    suspend fun getrequestCount(){
                var requestref = FirebaseFirestore.getInstance()
        Log.d(TAG, "${uniqueID} => ${uniqueID}")
        requestref.collection("warehouses").document(uniqueID).collection("requests").whereEqualTo("uniqueID",uniqueID)
            .get().addOnSuccessListener {
                requests.text= it.size().toString()
                for (document in it) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                }
            }.addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }


    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

}