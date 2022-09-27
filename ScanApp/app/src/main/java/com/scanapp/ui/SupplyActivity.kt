package com.scanapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.scanapp.R
import com.scanapp.util.launchActivity

class SupplyActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supply)
        val btnNext = findViewById<CardView>(R.id.llNext)
        val dNum = findViewById<EditText>(R.id.etDeliveryNumber)


        btnNext.setOnClickListener{
//            if(intent.hasExtra("verificationType")){
//                if(intent.extras?.getString("verificationType") == "verify" )
//                launchActivity<VerifyActivity> {  }
//                else{
//                    if(intent.extras?.getString("verificationType") == "fixStatus" )
//                        launchActivity<FixActivity> {  }
//                }
//
//            } else
            if (!dNum.text.isNullOrEmpty())
                launchActivity<SupplyProductActivity> { }
            else
                Toast.makeText(applicationContext,"Please Enter Delivery Number First",Toast.LENGTH_SHORT).show()
        }
    }
}