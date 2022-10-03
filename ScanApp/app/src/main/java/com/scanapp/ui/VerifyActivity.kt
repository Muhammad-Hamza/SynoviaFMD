package com.scanapp.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.scanapp.Listeners
import com.scanapp.R
import com.scanapp.gs1.FieldAI
import com.scanapp.gs1.FieldsAI
import com.scanapp.network.remote.SupplyModel
import com.scanapp.ui.dashboard.ScannerActivity
import com.scanapp.ui.product_supply.ProductSupplyViewModel


class VerifyActivity : AppCompatActivity()
{

    private val state: String = "Supplied"
    lateinit var etProductCode: EditText
    lateinit var etSerialNumber: EditText
    lateinit var etBatchNo: EditText
    lateinit var etExpiry: EditText

    private lateinit var mViewModel: ProductSupplyViewModel

    private fun initView()
    {
        etProductCode = findViewById(R.id.etProductCode)
        etSerialNumber = findViewById(R.id.etSerialNumber)
        etBatchNo = findViewById(R.id.etBatchNo)
        etExpiry = findViewById(R.id.etExpiry)
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify)
        mViewModel = ViewModelProvider(this).get(ProductSupplyViewModel::class.java)

        mViewModel.attachErrorListener(object : Listeners.DialogInteractionListener
        {
            override fun dismissDialog()
            {

            }

            override fun addDialog()
            {
            }

            override fun addErrorDialog()
            {
            }

            override fun addErrorDialog(msg: String?)
            {
            }

        })
        val btnScanpak = findViewById<AppCompatButton>(R.id.btnScanpak)
        val btnComplete = findViewById<AppCompatButton>(R.id.btnComplete)
        initView()


        btnComplete.setOnClickListener {
//            finish()
        }
        btnScanpak.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1);
                return@setOnClickListener
            }
            findViewById<LinearLayoutCompat>(R.id.llStatus).visibility = View.GONE
            val intent = Intent(this, ScannerActivity::class.java)
            someActivityResultLauncher.launch(intent)
        }
    }

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    var someActivityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ActivityResultCallback<ActivityResult> { result: ActivityResult ->
        if (result.resultCode === RESULT_OK) { // There are no request codes
            val data: Intent = result.getData()!!
            var resultData = data.getStringExtra("result")

            if (resultData != null) {
                parseGS1(resultData)
            }

        }

    })

    private fun parseGS1(resultData: String) {

        if (!TextUtils.isEmpty(resultData)) {
            try {
                Log.d("Supply", resultData!!.toString())

                val splittedResult = resultData.split(resultData.toCharArray().get(0))

                var fieldList = mutableListOf<FieldsAI>()
                for (s in splittedResult) {
                    if (!s.isEmpty()) {
                        fieldList.add(FieldsAI.from(s))
                    }
                }
                Log.d("Supply", Gson().toJson(resultData))

                var productCode: FieldAI? =null
                var batch : FieldAI? =null
                var serialNumber : FieldAI? =null
                var expiry : FieldAI? =null
                for (ai in fieldList) {

                    if (productCode == null)
                        productCode = ai.list.firstOrNull { it.ai.ai == "01" }
                    if (batch == null)
                        batch = ai.list.firstOrNull { it.ai.ai == "10" }
                    if (serialNumber == null)
                        serialNumber = ai.list.firstOrNull { it.ai.ai == "21" }
                    if (expiry == null)
                        expiry = ai.list.firstOrNull { it.ai.ai == "17" }
                }

                etProductCode.setText(productCode?.textBody)
                etSerialNumber.setText(serialNumber?.textBody)
                etBatchNo.setText(batch?.textBody)
                etExpiry.setText(expiry?.textBody)


                Log.d("SUPPLY", productCode?.textBody.toString())
                Log.d("SUPPLY", serialNumber?.textBody.toString())
                Log.d("SUPPLY", batch?.textBody.toString())
                Log.d("SUPPLY", expiry?.textBody.toString())
                hitAPIRequest()
            } catch (ex: Exception) {
                Toast.makeText(this, "Invalid Data Try to scan again", Toast.LENGTH_SHORT)
                    .show()
            }

        } else {
            Toast.makeText(this, "Data not found", Toast.LENGTH_SHORT)
                .show()
            makeEmptyFields()
        }
    }

    private fun makeEmptyFields()
    {
        etProductCode.setText("")
        etSerialNumber.setText("")
        etBatchNo.setText("")
        etExpiry.setText("")
    }

    private fun showMessage(msg: String)
    {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT)
                .show()

    }


    private fun hitAPIRequest()
    {
        val productCode = etProductCode.text.toString()
        val expiry = etExpiry.text.toString()
        val batch = etBatchNo.text.toString()
        val serialNumber = etSerialNumber.text.toString()

        mViewModel.verifyPack(this, productCode, serialNumber, batch, expiry, object : ProductSupplyViewModel.onCompleteListener
        {
            override fun onDataFetch(model: SupplyModel, isError: Boolean)
            { //                showMessage(model.information)
//                makeEmptyFields()

                val txtSupplied = findViewById<TextView>(R.id.txtSupplied)
                val txtInfo = findViewById<TextView>(R.id.txtInfoValue)
                val bg = findViewById<LinearLayoutCompat>(R.id.llStatus)
                val prodName = findViewById<TextView>(R.id.txtProductName)
                val txtOperationCode = findViewById<TextView>(R.id.txtOperationCode)
                val txtAlertId = findViewById<TextView>(R.id.txtAlertId)

                bg.visibility = View.VISIBLE
                if (isError)
                {
                    bg.background = ContextCompat.getDrawable(applicationContext, R.drawable.status_bg_red)

                }
                else
                {
                    bg.background = ContextCompat.getDrawable(applicationContext, R.drawable.status_bg)
                }
                txtSupplied.setText(model.state)
                txtInfo.setText(model.warning)

                if(model.operationCode != null && model.operationCode.length > 0)
                    txtOperationCode.setText(model.operationCode)
                if(model.productName != null && model.productName.length > 0)
                    prodName.setText(model.productName)

                if(model.alertId != null && model.alertId.length > 0)
                    txtAlertId.setText(model.alertId)


                if (model.information != null && model.information.length > 0) {
                    txtInfo.setText(model.information)
                }

            }

        })

    }
}