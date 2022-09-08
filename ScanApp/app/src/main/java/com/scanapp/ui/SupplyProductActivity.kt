package com.scanapp.ui

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.scanapp.R
import com.scanapp.ui.dashboard.ScannerActivity


class SupplyProductActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supply_product)

        val btnScanpak = findViewById<AppCompatButton>(R.id.btnScanpak)
        btnScanpak.setOnClickListener {
            val intent = Intent(this, ScannerActivity::class.java)
            someActivityResultLauncher.launch(intent)
        }
    }

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    var someActivityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { result: ActivityResult ->
            if (result.resultCode === RESULT_OK) {
                // There are no request codes
                val data: Intent = result.getData()!!
                val resultData = data.getStringExtra("result")
                if (!TextUtils.isEmpty(resultData)) {
                    Toast.makeText(this, resultData, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Data not found", Toast.LENGTH_SHORT).show()
                }
            }
        })

}