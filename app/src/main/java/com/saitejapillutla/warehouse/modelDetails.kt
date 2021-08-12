package com.saitejapillutla.warehouse

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_model_details.*

class modelDetails : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_model_details)

        val category   =intent.getCharSequenceExtra("category")
        val companyModel     =intent.getCharSequenceExtra("companyModel")
        val companyName =intent.getCharSequenceExtra("companyName")
        val modelName    =intent.getCharSequenceExtra("modelName")
        val modelID  =intent.getCharSequenceExtra("modelID")
        val wareHouseID    =intent.getCharSequenceExtra("wareHouseID")
        val unitsAvailable  =intent.getCharSequenceExtra("unitsAvailable")

        model_details_category.setText(category)
        model_details_name.setText(modelName)
        model_details_uniqID.setText(modelID)
        model_details_manufacturor.setText(companyName)
        model_details_modelID.setText(companyModel)
        model_details_warehouseID.setText(wareHouseID)
        model_details_unitsAvailable.setText(unitsAvailable)


    }
}