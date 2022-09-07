package com.scanapp.ui.dashboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView
import com.scanapp.R
import com.scanapp.ui.SupplyActivity
import com.scanapp.util.launchActivity

class DashboardActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val llSupply = findViewById<CardView>(R.id.llSupply)

        llSupply.setOnClickListener{
            launchActivity<SupplyActivity> {  }
        }

    }
}