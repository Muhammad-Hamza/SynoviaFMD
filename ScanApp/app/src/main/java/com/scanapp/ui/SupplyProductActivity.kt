package com.scanapp.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
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
import com.google.gson.Gson
import com.scanapp.Listeners
import com.scanapp.R
import com.scanapp.gs1.FieldAI
import com.scanapp.gs1.FieldsAI
import com.scanapp.network.remote.SupplyModel
import com.scanapp.network.remote.TokenResponse
import com.scanapp.ui.dashboard.ScannerActivity
import com.scanapp.ui.product_supply.ProductSupplyViewModel
import java.lang.reflect.Field


class SupplyProductActivity : AppCompatActivity() {

    private var state: String? = null
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

        if (intent.hasExtra("FROM_FIX")) {
            state = intent.extras?.getString("State")
        } else {
            state = "Supplied"
        }
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
            finish()
//            parseGS1("\u001D1721062310000000\u001D2112YZYCcRHkCqnbCC12u1\u001D0110631721690828")
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
            findViewById<LinearLayoutCompat>(R.id.llStatus).visibility = View.GONE
            val intent = Intent(this, ScannerActivity::class.java)
            someActivityResultLauncher.launch(intent)
            makeEmptyFields()
        }
    }

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    var someActivityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { result: ActivityResult ->
            if (result.resultCode === RESULT_OK) { // There are no request codes
                val data: Intent = result.getData()!!
                val resultData = data.getStringExtra("result")
                if (resultData != null) {
                    try{
                        parseGS1(resultData)

                    } catch (ex:Exception){

                    }
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
        Toast.makeText(this, msg, Toast.LENGTH_SHORT)
            .show()

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
            state!!,
            object : ProductSupplyViewModel.onCompleteListener {
                override fun onDataFetch(model: SupplyModel, isError: Boolean) {
//                showMessage(model.information)
//                makeEmptyFields()

                    val txtSupplied = findViewById<TextView>(R.id.txtSupplied)
                    val txtInfo = findViewById<TextView>(R.id.txtInfoValue)
                    val prodName = findViewById<TextView>(R.id.txtProductName)
                    val txtOperationCode = findViewById<TextView>(R.id.txtOperationCode)
                    val txtAlertId = findViewById<TextView>(R.id.txtAlertId)
                    val txtAlertCode = findViewById<TextView>(R.id.txtAlertCode)
                    val txtAmsLink = findViewById<TextView>(R.id.txtAmsLink)
                    val bg = findViewById<LinearLayoutCompat>(R.id.llStatus)
                    bg.visibility = View.VISIBLE
                    if (isError) {
                        bg.background =
                            ContextCompat.getDrawable(applicationContext, R.drawable.status_bg_red)

                    } else {
                        bg.background =
                            ContextCompat.getDrawable(applicationContext, R.drawable.status_bg)
                    }
                    txtInfo.setText(model.warning)
                    txtOperationCode.setText(model.operationCode)
                    if (model.productName != null && model.productName.length > 0) {
                        prodName.setText(model.productName)
                    }
                    if (model.alertId != null && model.alertId.length > 0) {

                        txtAlertId.setText(model.alertId)
                    }

                    if (model.alertCode != null && model.alertCode.length > 0) {

                        txtAlertCode.setText(model.alertCode)
                    }
                    if (model.amsLink != null && model.amsLink.length > 0) {
                        txtAmsLink.setMovementMethod(LinkMovementMethod.getInstance())
                        txtAmsLink.setText(model.amsLink)
                    }

                    if (model.information != null && model.information.length > 0) {
                        txtInfo.setText(model.information)
                    }
                    txtSupplied.setText(model.state)

                }

            })

    }


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

}