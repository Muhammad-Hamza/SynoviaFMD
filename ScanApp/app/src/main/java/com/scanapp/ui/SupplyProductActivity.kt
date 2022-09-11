package com.scanapp.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.scanapp.Listeners
import com.scanapp.R
import com.scanapp.network.remote.SupplyModel
import com.scanapp.network.remote.TokenResponse
import com.scanapp.ui.dashboard.ScannerActivity
import com.scanapp.ui.product_supply.ProductSupplyViewModel


class SupplyProductActivity : AppCompatActivity() {

    lateinit var etProductCode: EditText
    lateinit var etSerialNumber: EditText
    lateinit var etBatchNo: EditText
    lateinit var etExpiry: EditText

    private lateinit var mViewModel: ProductSupplyViewModel

    private fun initView() {
        etProductCode = findViewById(R.id.etProductCode)
        etSerialNumber = findViewById(R.id.etSerialNumber)
        etBatchNo = findViewById(R.id.etBatchNo)
        etExpiry = findViewById(R.id.etExpiry)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supply_product)
        mViewModel = ViewModelProvider(this).get(ProductSupplyViewModel::class.java)

        mViewModel.attachErrorListener(object : Listeners.DialogInteractionListener {
            override fun dismissDialog() {

            }

            override fun addDialog() {
            }

            override fun addErrorDialog() {
            }

            override fun addErrorDialog(msg: String?) {
            }

        })
        val btnScanpak = findViewById<AppCompatButton>(R.id.btnScanpak)
        val btnComplete = findViewById<AppCompatButton>(R.id.btnComplete)
        initView()
        val text = "01022564353848351725020910000012100000000031"
        val productCode = text.substring(3, 17)
        val expiry = text.substring(19, 25)
        val batch = text.substring(27, 32)
        val serialNumber = text.substring(text.length - 11, text.length)


        btnComplete.setOnClickListener {
            hitAPIRequest()
        }
        btnScanpak.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
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
                    Log.d("Supply", resultData!!.toString())
                    val productCode = resultData?.substring(3, 17)
                    val expiry = resultData?.substring(19, 25)
                    val batch = resultData?.substring(27, 32)
                    val serialNumber = resultData?.substring(resultData!!.length - 11, resultData!!.length)

                    etProductCode.setText(productCode)
                    etSerialNumber.setText(serialNumber)
                    etBatchNo.setText(batch)
                    etExpiry.setText(expiry)

                    Toast.makeText(this, resultData, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Data not found", Toast.LENGTH_SHORT).show()
                    makeEmptyFields()
                }
            } else {
                makeEmptyFields()
            }
        })

    private fun makeEmptyFields() {
        etProductCode.setText("")
        etSerialNumber.setText("")
        etBatchNo.setText("")
        etExpiry.setText("")
    }

    private fun showMessage(msg: String) {
        Toast.makeText(
            this, msg, Toast.LENGTH_SHORT
        ).show()

    }

    private fun hitAPIRequest(text: String) {


        mViewModel.postSupplyInfo(
            this,
            etProductCode.text.toString(),
            etSerialNumber.text.toString(),
            etBatchNo.text.toString(),
            etExpiry.text.toString(),
            object : ProductSupplyViewModel.onCompleteListener {
                override fun onDataFetch(model: SupplyModel) {
                    showMessage(model.information)

                    makeEmptyFields()
                }

            }
        )

    }

    private fun hitAPIRequest() {
        val productCode = etProductCode.text.toString()
        val expiry = etExpiry.text.toString()
        val batch = etBatchNo.text.toString()
        val serialNumber = etSerialNumber.text.toString()

        mViewModel.postSupplyInfo(
            this,
            productCode,
            serialNumber,
            batch,
            expiry,
            object : ProductSupplyViewModel.onCompleteListener {
                override fun onDataFetch(model: SupplyModel) {
                    showMessage(model.information)
                    makeEmptyFields()

                    val txtSupplied = findViewById<TextView>(R.id.txtSupplied)
                    val txtInfo = findViewById<TextView>(R.id.txtInfoValue)
                    val bg = findViewById<LinearLayoutCompat>(R.id.llStatus)

                    if(model?.state != null)
                    {
                        if (model.state.equals("Destroyed") || model.state.equals("Recalled"))
                        {
                            bg.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.colorRed))
                        }
                    }

                    txtSupplied.setText(model.state)
                    txtInfo.setText(model.information)


                }

            }
        )

    }

}