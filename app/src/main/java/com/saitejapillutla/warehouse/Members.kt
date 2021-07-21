package com.saitejapillutla.warehouse

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.pacific.adapter.RecyclerAdapter
import kotlinx.android.synthetic.main.activity_create_user.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_members.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class Members : AppCompatActivity() {

    companion object{
        fun removeRecyclerItem( pos :Int){

        }
    }
    private  val TAG = "Members"
    val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
    private lateinit var job: Job
    val adapter = RecyclerAdapter()
    val memberadapter = RecyclerAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_members)
        job = Job()
        GlobalScope.launch(Dispatchers.IO) {
            getrequests()
            getmembers()
        }
        requestRecycler.adapter=adapter
        memberrecycler.adapter=memberadapter
        memberScroll.visibility= View.VISIBLE
        membersLoad.visibility= View.GONE
    }
    suspend fun getrequests(){
       var uniqID= intent.getStringExtra("uniqID")
        var requestref = FirebaseFirestore.getInstance()
        requestref.collection("warehouses").document(uniqID as String).collection("requests")
            .get().addOnSuccessListener {
                //requests.text= it.size().toString()
                for (document in it) {
                    val request=request(document.data.getValue("fullname") as String,document.data.getValue("email") as String,
                        document.data.getValue("photoURL") as String,document.data.getValue("uid") as String,this,uniqID )
                    adapter.add(request)
                }
            }.addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)

            }
    }




    suspend fun getmembers(){
        var uniqID= intent.getStringExtra("uniqID")
        var uid= intent.getStringExtra("uid")
        var role =""
        var requestref = FirebaseFirestore.getInstance()
        requestref.collection("users").document(uid as String).get().addOnSuccessListener {
            role=it.data?.getValue("role") as String

            Log.d(TAG, role)

        requestref.collection("warehouses").document(uniqID as String).collection("members")
            .addSnapshotListener { it, error ->
                Log.d(TAG, it.toString())
                for (document in it!!) {
                    val member=member(document.data.getValue("fullname") as String,document.data.getValue("email") as String,
                        document.data.getValue("photoURL") as String,uid as String,
                        this,uniqID, role.toString()
                    )
                    Log.d(TAG,uid as String)
                    Log.d(TAG,uniqID as String)
                    memberadapter.add(member)
                    pendingrequests.visibility=View.VISIBLE
                    Log.d(TAG, member.toString())
                }

            } }


    }
    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }
}