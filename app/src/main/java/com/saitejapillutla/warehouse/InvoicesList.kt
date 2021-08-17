package com.saitejapillutla.warehouse

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
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
        val warehouseID =intent.getStringExtra("uniqID")
        job = Job()
        CoroutineScope(newSingleThreadContext("retrivemodels")).launch(Dispatchers.Main){
            if (warehouseID != null) {
                retriveOrdersToBeInvoiced(warehouseID)
            }
        }

        var invoiceItem =invoiceListAdapter(
           "modelNo",
            "wareHouseID",
            "uid",
           "modelName",
        )
        adapter.add(invoiceItem)
        adapter.add(invoiceItem)
        adapter.add(invoiceItem)
        adapter.add(invoiceItem)
        adapter.add(invoiceItem)
        invoiceList_recycler.adapter=adapter


    }

    suspend fun retriveOrdersToBeInvoiced(uniqueID :String)
    {
        val requestref = FirebaseFirestore.getInstance()
        requestref.collection("warehouses").document(uniqueID)
            .collection("Orders").addSnapshotListener { value, error ->
                if (value != null) {

                    for(doc in value){
                        Log.d(TAG,doc.data.getValue("warehouse").toString())
                        if (doc.data.getValue("warehouse").toString()==uniqueID){

                            var invoiceItem =invoiceListAdapter(
                                doc.data.getValue("modelNo") as String,
                                doc.data.getValue("wareHouseID") as String,
                                doc.data.getValue("uid") as String,
                                doc.data.getValue("modelName") as String,
                            )
                            adapter.add(invoiceItem)

                        }

                    }

                }
            }
    }




    override fun onDestroy() {
        job.cancel()
        super.onDestroy()    }
}