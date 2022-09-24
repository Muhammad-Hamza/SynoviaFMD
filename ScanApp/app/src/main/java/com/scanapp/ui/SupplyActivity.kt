package com.scanapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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


        btnNext.setOnClickListener{
            if(intent.hasExtra("verificationType")){
                launchActivity<VerifyActivity> {  }
            } else
            launchActivity<SupplyProductActivity> {  }
        }
    }
}