package com.saitejapillutla.warehouse

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_create_user.*
import kotlinx.android.synthetic.main.load_dialog.*
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class createUser : AppCompatActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private  val TAG = "createUser"
    private  val RC_SIGN_IN = 9001
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)

        auth = FirebaseAuth.getInstance();
        phoneNumber.setImeActionLabel("hi", KeyEvent.KEYCODE_ENTER);
        sendrequest.setOnClickListener {
            PhoneNumber.isErrorEnabled = false
            fullname.isErrorEnabled = false
            var state = true
            if (phoneNumber.text.toString().matches("[0-9]+".toRegex())) {
                Log.d(TAG, "Phone number is ${phoneNumber.text}")
                if (phoneNumber.text.toString().length != 10) {
                    PhoneNumber.error = "Phone number length should be 10 digits!!"

                    state = false
                    Log.d(TAG, "Phone number is ${phoneNumber.text}")
                } else {
                    PhoneNumber.isErrorEnabled = false
                    Log.d(TAG, "Phone number passed is ${phoneNumber.text}")
                }
            } else {

                PhoneNumber.error = "This is not a phone number!!"
                Log.d(TAG, "Phone number is ${phoneNumber.text}")
                state = false

            }
            if ((fullName.text.toString()
                    .matches("^[a-zA-Z]+$".toRegex())) && (fullName.text != null)
            ) {
                fullname.isErrorEnabled = false
                Log.d(TAG, " fullname passed is ${fullName.text}")
            } else {
                fullname.error = "This format is invalid"
                state = false
            }
            if (state) {
                textView7.visibility = View.GONE
                sendrequest.visibility = View.GONE
                sendingrequest.visibility = View.VISIBLE
                addduser(auth.currentUser)
            }


        }
    }
    fun showDialog(){
      
        val dialog= MaterialAlertDialogBuilder(this).setView(R.layout.load_dialog)
        dialog.setCancelable(false)
        dialog.setPositiveButton("Okay") { _: DialogInterface, _: Int ->
            val intent = Intent(this, pending::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
        dialog.show()
    }
    fun addduser(user: FirebaseUser?){
        if((user!=null)){
            val data = hashMapOf(
                "fullname" to fullName.text.toString(),
                "phoneNumber" to phoneNumber.text.toString(),
                "uniqueID" to uniqueID.text.toString().lowercase(),
                "name" to user.displayName,
                "email" to user.email,
                "uid" to user.uid,
                "photoURL" to user.photoUrl.toString(),
                "firstLogin" to DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS").withZone(
                    ZoneOffset.UTC).format(Instant.now()),
                "status" to "AccessPending"
            )
            val requestData = hashMapOf(
                "fullname" to fullName.text.toString(),
                "uniqueID" to uniqueID.text.toString(),
                "uid" to user.uid,
                "photoURL" to user.photoUrl.toString(),
                "email" to user.email,

            )
            var firstWrite = FirebaseFirestore.getInstance()
            firstWrite.collection("users").document( user.uid.toString())
                .set(data as Map<String, Any>).addOnSuccessListener {
                    FirebaseFirestore.getInstance().collection("warehouses").document(uniqueID.text.toString()).collection("requests").document(user.uid.toString() ).set(requestData as Map<String, Any> )
                        .addOnSuccessListener {
                            Log.d(TAG, "user updated ${user.displayName}")
                            showDialog()
                        }

//                    val intent= Intent(this,pending::class.java)
//                    startActivity(intent)
//                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
//                    finish()
                }
        }
    }
}