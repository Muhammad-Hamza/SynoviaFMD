package com.scanapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.scanapp.R
import com.scanapp.ui.dashboard.DashboardActivity
import com.scanapp.util.launchActivity

class MainActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn = findViewById<Button>(R.id.btnSubmit)

        btn.setOnClickListener{
            launchActivity<DashboardActivity> {  }
        }
    }
}