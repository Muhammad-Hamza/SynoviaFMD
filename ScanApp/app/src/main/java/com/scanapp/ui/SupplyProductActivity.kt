package com.scanapp.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.scanapp.R
import com.scanapp.ui.dashboard.ScannerActivity


class SupplyProductActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supply_product)

        val btnScanpak = findViewById<AppCompatButton>(R.id.btnScanpak)
        btnScanpak.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1);
                return@setOnClickListener
            }
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
                    Log.d("Supply",resultData.toString())
                    Toast.makeText(this, resultData, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Data not found", Toast.LENGTH_SHORT).show()
                }
            }
        })

}