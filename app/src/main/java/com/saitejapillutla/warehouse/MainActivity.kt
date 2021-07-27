package com.saitejapillutla.warehouse

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private  val TAG = "MainActivityTAG"
    private  val RC_SIGN_IN = 9001
    private var signup =false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

// Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.firebase_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = FirebaseAuth.getInstance();
        gSignin.setOnClickListener {
            signIn()
        }
        gSignup.setOnClickListener {
            signup=true
            signIn()
        }
        }
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
        verifying.visibility=View.VISIBLE
        gSignin.visibility=View.GONE
        gSignup.visibility=View.GONE
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    verifyUser(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    //updateUI(null)
                }
            }
    }

    fun verifyUser(user: FirebaseUser?){

        var out =false
        if(signup&&(user!=null)){
            presentemail.text=user.email
            var firstWrite =FirebaseFirestore.getInstance()
             firstWrite.collection("users").document( user.uid).get().addOnSuccessListener { document ->
                 if (document.data != null) {
                     var dialog= MaterialAlertDialogBuilder(this).setTitle("User Already Available!").setMessage("Try Signing")
                         .setPositiveButton("Okay",DialogInterface.OnClickListener { dialog, id ->
                             auth = FirebaseAuth.getInstance()
                             auth.signOut()
                             val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                 .requestIdToken(getString(R.string.firebase_client_id))
                                 .requestEmail()
                                 .build()
                             googleSignInClient = GoogleSignIn.getClient(this, gso)
                             googleSignInClient.signOut().addOnSuccessListener {Log.d(TAG, "Signed out Current User ${user}")  }
                         })


                            dialog.show()


                     verifying.visibility=View.GONE
                     gSignin.visibility=View.VISIBLE
                     gSignup.visibility=View.VISIBLE
                 } else {
                     //Log.d(TAG, "DocumentSnapshot data: ${result.data!!.getValue("status")}")
                     val intent=Intent(this,createUser::class.java)
                     startActivity(intent)
                     overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
                     finish()
                 }
             }
                 .addOnFailureListener { exception ->
                     Log.d(TAG, "get failed with ", exception)
                 }

        }else verifySignIn(user)
    }
    fun verifySignIn(user: FirebaseUser?){
        if(user!=null)
        {   presentemail.text=user.email
            verifying.visibility=View.VISIBLE
            gSignin.visibility=View.GONE
            gSignup.visibility=View.GONE
            var isUserPresent =FirebaseFirestore.getInstance()
            isUserPresent.collection("users").document( user.uid).get().addOnSuccessListener { result ->
                when(result.data?.getValue("status").toString()){
                    "New"->{

                        Log.d(TAG, "DocumentSnapshot data: ${result.data!!.getValue("status")}")
                        val intent=Intent(this,createUser::class.java)
                        intent.putExtra(result.data?.getValue("fullname").toString(),"fullname")
                        startActivity(intent)
                        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
                        finish()
                    }
                    "AccessPending"->{
                        Log.d(TAG, "AccessPending")
                        val intent=Intent(this,pending::class.java)
                        intent.putExtra("fullname",result.data?.getValue("fullname").toString())
                        startActivity(intent)
                        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
                        finish()
                    }
                    "Active"->{
                        val userdata = hashMapOf(
                                    "fullname" to result.data?.getValue("fullname").toString(),
                                    "phoneNumber" to result.data?.getValue("phoneNumber").toString(),
                                    "uniqueID" to result.data?.getValue("uniqueID").toString(),
                                    "name" to result.data?.getValue("name").toString(),
                                    "email" to result.data?.getValue("email").toString(),
                                    "uid" to result.data?.getValue("uid").toString(),
                                    "photoURL" to result.data?.getValue("photoURL").toString(),
                                    "status" to result.data?.getValue("status").toString(),
                                    "role" to result.data?.getValue("role").toString(),
                        )
                        Log.d(TAG, "Active")
                        val intent=Intent(this,HomeActivity::class.java)
                        intent.putExtra("userdata",userdata)
                        startActivity(intent)
                        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
                        finish()
                    }
                    else ->{
                        auth.signOut()
                        googleSignInClient.signOut()
                        verifying.visibility=View.GONE
                        gSignin.visibility=View.VISIBLE
                        gSignup.visibility=View.VISIBLE
                    }
                }
            }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }
            Log.d(TAG, "user found ${user.displayName}")
        }else{
            verifying.visibility=View.GONE
            gSignin.visibility=View.VISIBLE
            gSignup.visibility=View.VISIBLE
        }
    }
    public override fun onStart() {
        super.onStart()
            var user=auth.currentUser
            verifySignIn(user)
        }
}