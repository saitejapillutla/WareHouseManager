package com.saitejapillutla.warehouse

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_order.*
import kotlinx.android.synthetic.main.activity_create_model.*
import kotlinx.android.synthetic.main.activity_create_model.categoryselected
import kotlinx.android.synthetic.main.activity_create_user.*
import java.util.*

class addOrder : AppCompatActivity() {

    val TAG="addOrder"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_order)


        val category =resources.getStringArray(R.array.categoryArray)
        val arrayAdapter= ArrayAdapter(this,R.layout.category_drop_down_item,category)
        ordercategoryselected.setAdapter(arrayAdapter)

        var cat =""
        ordercategoryselected.onItemSelectedListener =object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (parent != null) {
                    cat =parent.getItemAtPosition(position).toString()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                cat ="Nothing Selected"
            }
        }
        val modelNo=modelNo.text
        val quantity=quantity.text
        val warehouse=warehouse.text
        val deliveryhouse=deliveryhouse.text
        val email="info@dummy.com"
        val phone="1-800-123-4567"
        val orders="234567"
        val address="Dummy Address\n" +
                "Lorem Ipsum Sit Amet\n" +
                "Dummy Pin\n" +
                "Dummy place\n" +
                "Telephone : 1-800-123-4567\n" +
                "Email : info@dummy.com"

        addOrderBtn.setOnClickListener {

            if (cat!="Nothing Selected"){
            val data = hashMapOf(
                "category" to cat.toString(),
                "modelNo" to modelNo.toString(),
                "quantity" to quantity.toString(),
                "warehouse" to warehouse.toString(),
                "deliveryhouse" to deliveryhouse.toString(),
                "email" to email.toString(),
                "phone" to phone.toString(),
                "orders" to orders.toString(),
                "address" to address.toString()
            )
//            val ref =FirebaseFirestore.getInstance()
            val uniqUID=intent.getCharSequenceExtra("uniqueID")
            Log.d(TAG, uniqUID as String)
//            ref.collection("warehouses").document(uniqUID as String)
//                .collection("Orders").document(""+Calendar.getInstance().timeInMillis)
//                .set(data  as Map<String, Any>).addOnSuccessListener {
//                    Log.d(TAG,"Order Created asssssssssssss")
//                }
            FirebaseFirestore.getInstance().collection("warehouses")
                .document(uniqUID.toString())
                .collection("Orders")
                .document(Calendar.getInstance().timeInMillis.toString())
                .set(data as Map<String, Any> )
                .addOnSuccessListener {
                    Log.d(TAG,"Order Created asssssssssssss")
                }
        }else{  MaterialAlertDialogBuilder(this).setIcon(R.drawable.ic_baseline_error_outline_24).setTitle("Error").setMessage("Please Specify Order Category")  }

        }

    }
}