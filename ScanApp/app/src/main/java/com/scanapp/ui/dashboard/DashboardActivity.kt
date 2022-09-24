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
        val verify = findViewById<CardView>(R.id.verify)
        val fixStatus = findViewById<CardView>(R.id.fixStatus)

        llSupply.setOnClickListener{
            launchActivity<SupplyActivity> {  }
        }
        verify.setOnClickListener{
            launchActivity<SupplyActivity> {
                putExtra("verificationType","verify")
            }
        }
        fixStatus.setOnClickListener{
            launchActivity<SupplyActivity> {
                putExtra("verificationType","fixStatus")
            }
        }

    }
}