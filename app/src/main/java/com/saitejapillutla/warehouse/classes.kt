package com.saitejapillutla.warehouse

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.SimpleRecyclerItem
import kotlinx.android.synthetic.main.member_details.view.*
import kotlinx.android.synthetic.main.member_requests.view.*
import kotlinx.android.synthetic.main.model_adapter.view.*
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class classes {
}
class member(val name: String,
             val email: String,
             val photoURL: String,
             val userID:String,
             val context:Context,
             val uniqID:String,
             val role:String ): SimpleRecyclerItem(){
    val TAG = "MemberBuilderItem"
    override fun bind(holder: AdapterViewHolder) {
        //val binding = holder.binding(ItemMovieBinding::bind)
         }
    override fun getLayout(): Int {
        return R.layout.member_details
    }
    override fun unbind(holder: AdapterViewHolder) {
        super.unbind(holder)
    }
    override fun onViewAttachedToWindow(holder: AdapterViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.itemView.textView27.text=name
        holder.itemView.textView28.text= email
        Glide.with(context)
            .load(photoURL)
            .circleCrop()
            .into(holder.itemView.imageView6);
        if (role =="Administrator"){
            holder.itemView.editrole.visibility=View.VISIBLE
            holder.itemView.editrole.setOnClickListener {

                val singleItems = arrayOf("Administrator", "Employee", "Delivery")
                var checkedItem = 0
                MaterialAlertDialogBuilder(context)
                    .setTitle("Choose a the role for ${name}")
                    .setNeutralButton("Cancel") { dialog, which ->
                    }
                    .setPositiveButton("Assign" ,DialogInterface.OnClickListener {
                            dialog, id ->
                       // holder.itemView.progressBar3.visibility=View.VISIBLE
                        var requestref = FirebaseFirestore.getInstance()
                        val userUpdate = hashMapOf(
                            "role" to singleItems[checkedItem],
                        )
                        val notify = hashMapOf(
                            "type" to "rolesRhange",
                            "msg" to "${name}'s role has been updated as ${singleItems[checkedItem]}"
                        )
                        Log.d(TAG," user ID id ${userID}")
                        requestref.collection("users").document(userID)
                            .update(userUpdate as Map<String, Any>).addOnSuccessListener {


                            holder.itemView.progressBar3.setProgress(40,false)
                            requestref.collection("warehouses").document(uniqID).collection("notifications").document(
                                "Time.from(Instant.now()).toString()")
                                .set(notify).addOnSuccessListener {
                                holder.itemView.progressBar3.setProgress(80,false)


                                    val dialog=MaterialAlertDialogBuilder(context).setTitle("Action Result")
                                .setIcon(R.drawable.track_changes).setView(R.layout.msg_success_dialog)
                                .setNeutralButton("Okay") { dialog, which ->}
                                    dialog.show()

                                holder.itemView.progressBar3.visibility=View.GONE}


                        }.addOnFailureListener { Log.d(TAG, it.toString()) }
                    })
                    .setSingleChoiceItems(singleItems, 0) { dialog, which ->
                        Log.d(TAG,"Selected ${which}")
                        checkedItem = which
                    }.show()
            }

        }
    }
}
class request (val name: String,val email: String,val photoURL: String,val userID:String,
               val context:Context,
               val uniqID:String
               ): SimpleRecyclerItem(){
    val TAG = "RequestBuilderItem"
    override fun bind(holder: AdapterViewHolder) {
        //val binding = holder.binding(ItemMovieBinding::bind)
    }
    override fun getLayout(): Int {

        return R.layout.member_requests
    }
    override fun unbind(holder: AdapterViewHolder) {
        super.unbind(holder)
    }

    override fun onViewAttachedToWindow(holder: AdapterViewHolder) {
        super.onViewAttachedToWindow(holder)
            holder.itemView.textView30.text=name
            holder.itemView.requestemail.text= email
        Glide.with(context)
            .load(photoURL)
            .circleCrop()
            .into(holder.itemView.imageView10);

            holder.itemView.declineRequest.setOnClickListener {
                holder.itemView.progressBar.visibility=View.VISIBLE
                holder.itemView.acceptRequest.isEnabled=false
                holder.itemView.declineRequest.isEnabled=false
                MaterialAlertDialogBuilder(context).setIcon(R.drawable.ic__delete)
                    .setTitle("Decline this request ?").setMessage("This request will be rejected and all its data will be removed")
                    .setPositiveButton("Decline" ,DialogInterface.OnClickListener {
                            dialog, id -> removeRequest(holder)
                        var requestref = FirebaseFirestore.getInstance()

                        requestref.collection("warehouses").document(uniqID).collection("requests").document(userID)
                            .delete().addOnSuccessListener {
                                requestref.collection("users").document(userID).delete().addOnSuccessListener {
                                    holder.itemView.memberRequest.visibility=View.GONE }

                            }
                    })
                    .setNeutralButton("Cancel") { _: DialogInterface, _: Int ->

                        holder.itemView.progressBar.visibility=View.GONE
                        holder.itemView.acceptRequest.isEnabled=true
                        holder.itemView.declineRequest.isEnabled=true
                    }
                    .show()


            }
            holder.itemView.acceptRequest.setOnClickListener {

                val singleItems = arrayOf("Administrator", "Employee", "Delivery")
                var checkedItem = 1
                MaterialAlertDialogBuilder(context)
                    .setTitle("Choose a the role for ${name}")
                    .setNeutralButton("Cancel") { dialog, which ->

                    }
                    .setPositiveButton("Assign" ,DialogInterface.OnClickListener {
                            dialog, id ->
                        holder.itemView.progressBar.visibility=View.VISIBLE
                        var requestref = FirebaseFirestore.getInstance()
                        val data = hashMapOf(
                            "email" to email,
                            "fullname" to name,
                            "uid" to uniqID,
                            "photoURL" to photoURL,
                            "uniqID" to uniqID,
                            "joinedOn" to DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS").withZone(
                                ZoneOffset.UTC).format(Instant.now()),
                        )
                        val userUpdate = hashMapOf(
                            "status" to "Active",
                            "role" to singleItems[checkedItem],
                            "uniqueID" to uniqID
                        )
                        Log.d(TAG,"Selected as ${checkedItem}")
                        requestref.collection("warehouses").document(uniqID).collection("members").document(userID).set(
                            data as Map<String, Any>).addOnSuccessListener {
                            requestref.collection("warehouses").document(uniqID).collection("requests").document(userID).delete().addOnSuccessListener {
                                requestref.collection("users").document(userID).update(userUpdate as Map<String, Any>).addOnSuccessListener {
                                    holder.itemView.memberRequest.visibility=View.GONE
                                }
                            }
                            Log.d(TAG,"Updated ${checkedItem}")}
                    })
                    .setSingleChoiceItems(singleItems, 0) { dialog, which ->
                        Log.d(TAG,"Selected ${which}")
                        checkedItem = which
                    }
                    .setCancelable(false)
                    .show()
            }
}

    fun removeRequest(holder: AdapterViewHolder){
            Log.d(TAG,"request Declined")




    }




}

class modelID(
    val category: String,
    val companyModel: String,
    val companyName: String,
    val modelName: String,
    val modelID: String,
    val wareHouseID:String,
    val unitsAvailable: String,
): SimpleRecyclerItem(){
    override fun bind(holder: AdapterViewHolder) {
        //this works
    }

    override fun getLayout(): Int {
        return R.layout.model_adapter
    }
    override fun unbind(holder: AdapterViewHolder) {
        super.unbind(holder)
    }
    override fun onViewAttachedToWindow(holder: AdapterViewHolder) {
        super.onViewAttachedToWindow(holder)
        val v=holder.itemView
        v.modelname.text=modelName
        v.modelid.text="ID : "+modelID
        v.category.text=category
       // v.unitsAvailable.setText(unitsAvailable)
            v.model_adapter_layout.setOnClickListener {
                val intent=Intent(v.context,modelDetails::class.java)
                intent.putExtra("category",category)
                intent.putExtra("companyModel",companyModel)
                intent.putExtra("companyName",companyName)
                intent.putExtra("modelName",modelName)
                intent.putExtra("modelID",modelID)
                intent.putExtra("wareHouseID",unitsAvailable)
                intent.putExtra("unitsAvailable",unitsAvailable)
                v.context.startActivity(intent)
            }
    }
}