package com.saitejapillutla.warehouse

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_order.*
import kotlinx.android.synthetic.main.activity_all_models.*
import java.util.*

class addOrder : AppCompatActivity() {

    val TAG="addOrder"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_order)


        val category =resources.getStringArray(R.array.categoryArray)
        val arrayAdapter= ArrayAdapter(this,R.layout.category_drop_down_item,category)
        ordercategoryselected.setAdapter(arrayAdapter)

        var cat ="A"

        ordercategoryselected.setOnItemClickListener { parent, view, position, id ->
            Log.d(TAG,"Current Position : ${position} ")
            cat =category[position]
        }
        val modelNo=modelNo.text
        val quantity=quantity.text
        val warehouse=warehouse.text
        val deliveryhouse=deliveryhouse.text
        val email="info@dummy.com"
        val phone="1-800-123-4567"
        val itemName="machine"
        val address="Dummy Address\n" +
                "Lorem Ipsum Sit Amet\n" +
                "Dummy Pin\n" +
                "Dummy place\n" +
                "Telephone : 1-800-123-4567\n" +
                "Email : info@dummy.com"
        addOrderBtn.setOnClickListener {
            val uid =Calendar.getInstance().timeInMillis.toString()
            if (cat!="A"){
            val data = hashMapOf(
                "category" to cat,
                "modelNo" to modelNo.toString(),
                "quantity" to quantity.toString(),
                "warehouse" to warehouse.toString(),
                "deliveryhouse" to deliveryhouse.toString(),
                "email" to email,
                "phone" to phone,
                "itemName" to itemName,
                "address" to address,
                "pincode" to "543670",
                "status" to "placed",
                "uid" to uid,
                "currentPoint" to warehouse.toString()
            )
            val uniqUID=intent.getCharSequenceExtra("uniqueID")
            Log.d(TAG, uniqUID as String)
            FirebaseFirestore.getInstance().collection("warehouses")
                .document(uniqUID.toString())
                .collection("Orders")
                .document(uid)
                .set(data as Map<String, Any> )
                .addOnSuccessListener {
                    onBackPressed()
//                    val intent =Intent(this,HomeActivity::class.java)
//                    intent.putExtra("orderupdated",1)
//                    startActivity(intent)
//                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)

                }
        }else{
            MaterialAlertDialogBuilder(this).setIcon(R.drawable.ic_baseline_error_outline_24)
                .setTitle("Error").setMessage("Please Specify Order Category")
                .show()}
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
    }
}