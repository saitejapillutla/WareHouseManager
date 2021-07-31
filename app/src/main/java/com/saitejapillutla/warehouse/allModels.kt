package com.saitejapillutla.warehouse

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.pacific.adapter.RecyclerAdapter
import com.squareup.okhttp.Dispatcher
import kotlinx.android.synthetic.main.activity_all_models.*
import kotlinx.android.synthetic.main.activity_create_model.*
import kotlinx.android.synthetic.main.activity_members.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class allModels : AppCompatActivity() {
    val TAG ="allModelsActivity"

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
        requestRecycler.adapter=adapter
        linearProgressIndicator.visibility= View.INVISIBLE
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }
   suspend fun retrivemodels(warehouseID :String){
       val requestref = FirebaseFirestore.getInstance()
       requestref.collection("warehouses").document(warehouseID as String).collection("models")
           .get().addOnSuccessListener {
               for (document in it) {
                        val modelDetails =modelID(
                            document.data.getValue("category") as String,
                            document.data.getValue("company model") as String,
                            document.data.getValue("company name") as String,
                            document.data.getValue("model name") as String,
                            document.data.getValue("modelID") as String,)
                   adapter.add(modelDetails)
               }

           }
    }
}