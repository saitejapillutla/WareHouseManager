package com.saitejapillutla.warehouse

//import android.widget.PopupMenu
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.SimpleRecyclerItem
import kotlinx.android.synthetic.main.activity_create_model.*
import kotlinx.android.synthetic.main.model_adapter.view.*
import java.sql.Timestamp
import java.time.Instant
import java.time.Instant.EPOCH
import java.time.Instant.now
import java.time.LocalDateTime.now
import java.time.format.DateTimeFormatter
import java.util.*

class createModel : AppCompatActivity() {
    val TAG ="createModel"
companion object{
    var updated :Boolean=false;
}
    override fun onResume() {
        super.onResume()
        val category =resources.getStringArray(R.array.categoryArray)
        val arrayAdapter=ArrayAdapter(this,R.layout.category_drop_down_item,category)
        categoryselected.setAdapter(arrayAdapter)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_model)
        val category =resources.getStringArray(R.array.categoryArray)
        val arrayAdapter=ArrayAdapter(this,R.layout.category_drop_down_item,category)
        categoryselected.setAdapter(arrayAdapter)

        categoryselected.setOnItemClickListener { parent, view, position, id ->


        }
        addItem.setOnClickListener {
            var itemsAvail=0
            try {
                itemsAvail =Integer.parseInt(itemsAvailable.text.toString())
                itemsAvail++;
            }catch (e : NumberFormatException){
                itemsAvail=0
            }
            Log.d(TAG," items available --->"+itemsAvail)
            if(itemsAvail<0){
                itemsAvail=0;
            }
            itemsAvailable.setText(itemsAvail.toString())
        }
        removeItem.setOnClickListener {
            var itemsAvail=0
            try {
                itemsAvail =Integer.parseInt(itemsAvailable.text.toString())
                itemsAvail--;
            }catch (e : NumberFormatException){                itemsAvail=0            }
            Log.d(TAG," items available --->"+itemsAvail)
            if(itemsAvail<0){                itemsAvail=0;            }
            itemsAvailable.setText(itemsAvail.toString())        }
        generate_modelId.setOnClickListener {
            generate_modelId.visibility= View.INVISIBLE
           // val modelID= "MID" + ((Math.random() * 9000000).toInt() + 1000000)
            val modelID = "MID"+ Calendar.getInstance().timeInMillis
            textView42.setText(modelID)
        }

        addModelDB.setOnClickListener {
            var state =true
            if(ModelName.text.toString()==""){
                state=false;
                ModelName.error="Model should not be Null !!"
            }
            if(CompanyName.text.toString()==""){
                state=false;
                CompanyName.error="Company Name Should not be Null!!"
            }
            if(CompanyModel.text.toString()==""){
                state=false;
                CompanyModel.error="Company Model Should not be Null!!"
            }
           if ( generate_modelId.text=="ModelID"){

               generate_modelId.visibility= View.INVISIBLE
               //val modelID= "MID" + ((Math.random() * 9000000).toInt() + 1000000)
               val modelID = "MID"+ Calendar.getInstance().timeInMillis
               textView42.setText(modelID)
           }
            if(state){
                val data = hashMapOf(
                    "category" to categoryselected.text.toString(),
                "model name" to ModelName.text.toString(),
                    "company name" to CompanyName.text.toString(),
                "company model" to CompanyModel.text.toString(),
                    "modelID" to textView42.text.toString(),
                )


                val ref =FirebaseFirestore.getInstance()
                var warehouseID =intent.getCharSequenceExtra("uniqID").toString();
                val modelID=textView42.text as String
                ref.collection("warehouses").document(warehouseID as String)
                    .collection("models")
                    .document(modelID)
                    .set(data  as Map<String, Any> ).addOnSuccessListener {
                        val intent = Intent(this, allModels::class.java)
                        intent.putExtra("updated",1)
                        intent.putExtra("uniqID",warehouseID)
                        startActivity(intent)
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        //finish()



                    }
            }
        }

    }
}
