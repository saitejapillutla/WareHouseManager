package com.saitejapillutla.warehouse

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pacific.adapter.RecyclerAdapter
import kotlinx.android.synthetic.main.activity_all_models.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class allModels : AppCompatActivity() {
    val TAG ="allModelsActivity"
var size=9
    val coroutineContext: CoroutineContext
    get() = Dispatchers.IO +job
    private lateinit var job: Job
    val adapter = RecyclerAdapter()
    @ObsoleteCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_models)
        if(intent.getIntExtra("updated",0)==1){
        Snackbar.make(all_models_details, "Updated", Snackbar.LENGTH_LONG).setBackgroundTint(resources.getColor( R.color.purple_500))
            .show()}
        job = Job()

        val warehouseID =intent.getStringExtra("uniqID")
        CoroutineScope(newSingleThreadContext("retrivemodels")).launch(Dispatchers.IO){
            if (warehouseID != null) {
                retrivemodels(warehouseID)
            }
        }
        modelsRecycler.adapter=adapter
        linearProgressIndicator.visibility= View.INVISIBLE
        modelsRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy>0){
                    Log.d(TAG,"recycle"+ dy.toString())
                   Log.d(TAG, adapter.itemCount.toString())
                    Log.d(TAG,size.toString())
                    if ( modelsRecycler.layoutManager?.childCount==adapter.itemCount){
                        Log.d(TAG,dy.toString())
                    }
                }
            }
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                //Log.d(TAG,newState.toString())
            }
        })
    }
     fun onScrolled(@NonNull recyclerView: RecyclerView,dx: Int,dy: Int): Unit {    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()    }

   suspend fun retrivemodels(warehouseID :String){
       val requestref = FirebaseFirestore.getInstance()
       requestref.collection("warehouses").document(warehouseID as String).collection("models")
           .orderBy("modelID", Query.Direction.DESCENDING).limit(50).get().addOnSuccessListener {
                size=it.size()
               for (document in it) {
                   val intent = Intent(this,modelDetails::class.java)
                   intent.putExtra("modelID",document.data.getValue("modelID") as String)
                   val modelDetails =modelID(
                       document.data.getValue("category") as String,
                       document.data.getValue("company model") as String,
                       document.data.getValue("company name") as String,
                       document.data.getValue("model name") as String,
                       document.data.getValue("modelID") as String,
                       warehouseID as String,
                       document.data.getValue("unitsAvailable") as String)
                   adapter.add(modelDetails)
               }
           }
    }
}
