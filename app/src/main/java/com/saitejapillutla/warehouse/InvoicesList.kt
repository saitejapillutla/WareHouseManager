package com.saitejapillutla.warehouse

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pacific.adapter.RecyclerAdapter
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_invoices_list.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class InvoicesList : AppCompatActivity() {
    val TAG ="InvoicesListActivity"
    val coroutineContext: CoroutineContext
    get() = Dispatchers.IO +job
    private lateinit var job: Job
    val adapter =RecyclerAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoices_list)
        invoiceList_recycler
        val warehouseID =intent.getStringExtra("uniqueID")
        job = Job()
        CoroutineScope(newSingleThreadContext("retrivemodels")).launch(Dispatchers.Main){
            if (warehouseID != null) {
                retriveOrdersToBeInvoiced(warehouseID)
            }
        }

        invoiceList_recycler.adapter=adapter


    }

    suspend fun retriveOrdersToBeInvoiced(uniqueID :String)
    {
        val requestref = FirebaseFirestore.getInstance()
        requestref.collection("warehouses").document(uniqueID)
            .collection("Orders").orderBy("uid", Query.Direction.DESCENDING).addSnapshotListener { value, error ->
                if (value != null) {
                    for(doc in value){
                        Log.d(TAG,doc.data.getValue("warehouse").toString())
                        if (doc.data.getValue("warehouse").toString()==uniqueID){

                            val invoiceItem =invoiceListAdapter(
                                doc.data.getValue("modelNo") as String,
                                doc.data.getValue("warehouse") as String,
                                doc.data.getValue("uid") as String,
                                doc.data.getValue("itemName") as String,
                                doc.data.getValue("category") as String,
                            )
                            adapter.add(invoiceItem)
                        }


                    }

                }else{Log.d(TAG, error.toString())}
            }
    }




    override fun onDestroy() {
        job.cancel()
        super.onDestroy()    }
}