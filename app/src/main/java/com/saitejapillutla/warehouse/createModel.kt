package com.saitejapillutla.warehouse

import android.annotation.SuppressLint
import android.graphics.drawable.InsetDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
//import android.widget.PopupMenu
import androidx.annotation.MenuRes
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.PopupMenu
import kotlinx.android.synthetic.main.activity_create_model.*

class createModel : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_model)


        val category =resources.getStringArray(R.array.category)
        val arrayAdapter=ArrayAdapter(this,R.layout.category_drop_down_item,category)
        autoCompleteTextView.setAdapter(arrayAdapter)
    }


}