package com.saitejapillutla.warehouse

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_create_user.*
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private  val TAG = "ProfileActivity"
    var warehouseId =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        signout.setOnClickListener{
            val dialog= MaterialAlertDialogBuilder(this)
                .setIcon(R.drawable.logout_circle)
                .setTitle("Are You Sure to Logout ?")
                .setMessage("Save Changes You have made before logging out.. ").setCancelable(true)
                .setPositiveButton("Logout") { _: DialogInterface, _: Int ->
                    auth.signOut()
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.firebase_client_id))
                        .requestEmail()
                        .build()

                    googleSignInClient = GoogleSignIn.getClient(this, gso)
                    googleSignInClient.signOut()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    finish()
                }

            dialog.setCancelable(true)
            dialog.show()
        }

        uchanges.setOnClickListener {
            var message =""
            if (!(zipcode.text.toString().matches("[0-9]+".toRegex()))) {
                message =message +" Your Zipcode is in invalid format \n"
            }
            if (!(cphone.text.toString().matches("[0-9]+".toRegex()))) {
                message =message +" Your Phone is in invalid format \n"
            }

            if (!(android.util.Patterns.EMAIL_ADDRESS.matcher(cemail.text).matches())) {
                message =message +" Your Email is in invalid format \n "

            }
            if(message!=""){
                MaterialAlertDialogBuilder(this)
                    .setIcon(R.drawable.ic_baseline_error_outline_24)
                    .setTitle("Error in Context")
                    .setMessage(message)
                    .setPositiveButton("Okay") { _: DialogInterface, i: Int ->}
                    .setCancelable(true)
                    .show()
            }else{
                Log.d(TAG, "Sending Request to Update Changes")
                updateuserprofileData(auth.currentUser)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        auth= FirebaseAuth.getInstance()
        getUserprofileData(auth.currentUser)
    }
    fun getUserprofileData( user: FirebaseUser?){
        val ref = FirebaseFirestore.getInstance()
        if (user!=null){
            ref.collection("users").document( user.uid).get().addOnSuccessListener {
               if( it.data?.getValue("status")=="Active"){
                    LProfile.visibility= View.GONE
                   DProfile.visibility=View.VISIBLE
                   textView15.text=it.data?.getValue("fullname").toString()
                   textView17.text=it.data?.getValue("role").toString()
                   warehouseCode.setText(it.data?.getValue("uniqueID").toString())
                   phone.setText(it.data?.getValue("phoneNumber").toString())
                   email.setText(it.data?.getValue("email").toString())
                   email.isEnabled=false
                   warehouseCode.isEnabled=false
                   if( it.data?.getValue("role").toString()!="Administrator"){
                      warehouseCode.isEnabled=false
                      address.isEnabled=false
                      zipcode.isEnabled=false
                      cemail.isEnabled=false
                      cphone.isEnabled=false
                  }
                   warehouseId=it.data?.getValue("uniqueID").toString()
                   ref.collection("warehouses").document( it.data?.getValue("uniqueID").toString()).get().addOnSuccessListener {
                       address.setText(it.data?.getValue("address").toString())
                        zipcode.setText(it.data?.getValue("zipcode").toString())
                       cphone.setText(it.data?.getValue("cphone").toString())
                        cemail.setText(it.data?.getValue("cemail").toString())


                   }

               }
            }.addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
        }
    }
    fun updateuserprofileData(user: FirebaseUser?){
        val data = hashMapOf(
            "fullname" to textView15.text,
            "role" to textView17.text,
            "phoneNumber" to phone.text.toString()
        )
        val wrehousedata = hashMapOf(
            "address" to    address.text.toString(),
            "zipcode" to zipcode.text.toString(),
            "cphone" to cphone.text.toString(),
            "cemail" to cemail.text.toString(),
                )
        if (user!=null){
        val ref = FirebaseFirestore.getInstance()
        ref.collection("users").document( user.uid).update(data as Map<String, Any>)
            .addOnSuccessListener {
                FirebaseFirestore.getInstance().collection("warehouses").document(warehouseId).update(wrehousedata as Map<String, Any>)
                    .addOnSuccessListener {

                            MaterialAlertDialogBuilder(this).setTitle("Update Status")
                                .setIcon(R.drawable.track_changes)
                                .setView(R.layout.msg_success_dialog)
                                .setPositiveButton("Okay") { _: DialogInterface, i: Int ->}
                                .show()

                    }
            }

    }}


}