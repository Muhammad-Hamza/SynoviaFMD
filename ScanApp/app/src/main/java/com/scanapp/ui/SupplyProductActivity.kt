package com.scanapp.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
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
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.scanapp.Listeners
import com.scanapp.R
import com.scanapp.databinding.ActivityFixBinding
import com.scanapp.databinding.ActivitySupplyProductBinding
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
    private val map = hashMapOf<String,String>()
    private val greenColor: String = "#00B050"
    private val redColor: String = "#FF0000"
    private val amberColor: String = "#FFC000"
    private lateinit var binding: ActivitySupplyProductBinding

    private lateinit var mViewModel: ProductSupplyViewModel

    private fun initView() {
        etProductCode = findViewById(R.id.etProductCode)
        etSerialNumber = findViewById(R.id.etSerialNumber)
        etBatchNo = findViewById(R.id.etBatchNo)
        etExpiry = findViewById(R.id.etExpiry)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prepareForColors()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_supply_product)

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
                    val batchRecallReason = findViewById<TextView>(R.id.txtBatchRecallReason)
                    val canReactive = findViewById<TextView>(R.id.txtCanReactivate)
                    val isInterMarket = findViewById<TextView>(R.id.txtIsInterMarket)
                    val txtCanReactivateUntill = findViewById<TextView>(R.id.txtCanReactivateUntil)
                    val txtproductWithDrawalReason = findViewById<TextView>(R.id.txtProductWithdrawal)


                    bg.visibility = View.VISIBLE
                    val color = map[model.operationCode]
                    if(color != null && color.length > 1){
                        bg.setBackgroundColor(Color.parseColor(color))

                    } else {
                        if (isError) {
                            bg.background =
                                ContextCompat.getDrawable(applicationContext, R.drawable.status_bg_red)

                        } else {
                            bg.background =
                                ContextCompat.getDrawable(applicationContext, R.drawable.status_bg)
                        }
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
                        binding.txtLink.visibility = View.VISIBLE
                        binding.txtAmsLink.visibility = View.VISIBLE
                        txtAmsLink.setMovementMethod(LinkMovementMethod.getInstance())
                        txtAmsLink.setText(model.amsLink)
                    } else {
                        binding.txtLink.visibility = View.GONE
                        binding.txtAmsLink.visibility = View.GONE
                    }

                    if (model.information != null && model.information.length > 0) {
                        txtInfo.setText(model.information)
                    }

                    if (model.productWithdrawalReason != null && model.productWithdrawalReason.length > 0) {
                        txtproductWithDrawalReason.visibility = View.VISIBLE
                        binding.productWithdrawalHeading.visibility = View.VISIBLE

                        txtproductWithDrawalReason.setText(model.productWithdrawalReason)
                    } else {
                        binding.productWithdrawalHeading.visibility = View.GONE
                        txtproductWithDrawalReason.visibility = View.GONE
                    }
                    if (model.canReactivate != null && model.canReactivate) {
                        binding.canReactivateHeading.visibility = View.VISIBLE
                        canReactive.visibility = View.VISIBLE
                        canReactive.setText(model.canReactivate.toString())
                    } else {
                        canReactive.visibility = View.GONE
                        binding.canReactivateHeading.visibility = View.GONE
                    }
                    if (model.batchRecallReason != null && model.batchRecallReason.isNotEmpty()) {
                        binding.batchRecall.visibility = View.VISIBLE
                        batchRecallReason.visibility = View.VISIBLE
                        batchRecallReason.setText(model.batchRecallReason)
                    } else {
                        binding.batchRecall.visibility = View.GONE
                        batchRecallReason.visibility = View.GONE
                    }
                    if (model.isIntermarket != null && model.isIntermarket.isNotEmpty()) {
                        binding.isInterMarkerHeading.visibility = View.VISIBLE
                        isInterMarket.visibility = View.VISIBLE
                        isInterMarket.setText(model.isIntermarket)
                    } else {
                        binding.isInterMarkerHeading.visibility = View.GONE
                        isInterMarket.visibility = View.GONE
                    }
                    if (model.canReactivateUntil != null && model.canReactivateUntil.isNotEmpty()) {
                        binding.canReactivateUntilHeading.visibility = View.VISIBLE
                        txtCanReactivateUntill.visibility = View.VISIBLE
                        txtCanReactivateUntill.setText(model.canReactivateUntil)
                    } else {
                        binding.canReactivateUntilHeading.visibility = View.GONE
                        txtCanReactivateUntill.visibility = View.GONE
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
    fun prepareForColors(){
        map.put("11110201",amberColor)
        map.put("11110201",amberColor)
        map.put("11411100",amberColor)
        map.put("10010000",greenColor)
        map.put("11110100",greenColor)
        map.put("11210200",greenColor)
        map.put("11310300",greenColor)
        map.put("11310400",greenColor)
        map.put("11310400",greenColor)
        map.put("11310500",greenColor)
        map.put("11310600",greenColor)
        map.put("11310700",greenColor)
        map.put("11310700",greenColor)
        map.put("11310800",greenColor)
        map.put("11310800",greenColor)
        map.put("11410100",greenColor)
        map.put("11420100",greenColor)
        map.put("12200000",greenColor)
        map.put("14000000",greenColor)
        map.put("14100000",greenColor)
        map.put("22100000",greenColor)
        map.put("51420100",greenColor)
        map.put("51420101",greenColor)
        map.put("11110200",redColor)
        map.put("30020000",redColor)
        map.put("51420200",redColor)
        map.put("51420201",redColor)
        map.put("51420300",redColor)
        map.put("51420301",redColor)
        map.put("51420400",redColor)
        map.put("51420401",redColor)
        map.put("51420500",redColor)
        map.put("51420501",redColor)
        map.put("51420600",redColor)
        map.put("51420601",redColor)
        map.put("51420700",redColor)
        map.put("51420800",redColor)
        map.put("51420801",redColor)
        map.put("51420801",redColor)
        map.put("51420900",redColor)
        map.put("51421000",redColor)
        map.put("51421000",redColor)
        map.put("30020000",redColor)
        map.put("11411000",redColor)
        map.put("11411200",redColor)
        map.put("11110300",redColor)
        map.put("11110301",redColor)
        map.put("11110400",redColor)
        map.put("11110500",redColor)
        map.put("11110600",redColor)
        map.put("11110700",redColor)
        map.put("11110701",redColor)
        map.put("11110800",redColor)
        map.put("11110900",redColor)
        map.put("11111000",redColor)
        map.put("11111200",redColor)
        map.put("11111200",redColor)
        map.put("41020001",redColor)
        map.put("41020002",redColor)
        map.put("41020003",redColor)
        map.put("41020005",redColor)
        map.put("51020200",redColor)
        map.put("51220000",redColor)
        map.put("51220200",redColor)
        map.put("51220201",redColor)
        map.put("51220300",redColor)
        map.put("51220301",redColor)
        map.put("51220400",redColor)
        map.put("51220401",redColor)
        map.put("51220500",redColor)
        map.put("51220501",redColor)
        map.put("51220600",redColor)
        map.put("51220601",redColor)
        map.put("51220700",redColor)
        map.put("51220701",redColor)
        map.put("51220800",redColor)
        map.put("51220801",redColor)
        map.put("51220801",redColor)
        map.put("51220900",redColor)
        map.put("51221000",redColor)
        map.put("51221200",redColor)
        map.put("51320000",redColor)
        map.put("51320200",redColor)
        map.put("51320201",redColor)
        map.put("51320300",redColor)
        map.put("51320301",redColor)
        map.put("51320400",redColor)
        map.put("51320401",redColor)
        map.put("51320500",redColor)
        map.put("51320501",redColor)
        map.put("51320600",redColor)
        map.put("51320601",redColor)
        map.put("51320700",redColor)
        map.put("51320701",redColor)
        map.put("51320800",redColor)
        map.put("51320801",redColor)
        map.put("51320900",redColor)
        map.put("51320900",redColor)
        map.put("51321000",redColor)
        map.put("51321200",redColor)
        map.put("51420001",redColor)
        map.put("51420000",redColor)
        map.put("51420002",redColor)
        map.put("51421200",redColor)
        map.put("52120000",redColor)
        map.put("52120000",redColor)
        map.put("62120003",redColor)
        map.put("70020000",redColor)
        map.put("B0020000",redColor)
        map.put("B1020000",amberColor)
        map.put("C0020001",amberColor)
        map.put("C0020002",amberColor)
        map.put("41020000",amberColor)
        map.put("C0020003",redColor)
        map.put("D0020000",redColor)
        map.put("51421100",amberColor)
        map.put("62120005",amberColor)
        map.put("62120004",amberColor)
        map.put("62120006",amberColor)
        map.put("62120007",amberColor)
        map.put("64120000",amberColor)
        map.put("64120001",amberColor)
        map.put("62120007",amberColor)
        map.put("52210000",amberColor)
        map.put("54120000",amberColor)
        map.put("61020000",amberColor)
        map.put("61020001",amberColor)
        map.put("61020002",amberColor)
        map.put("61020003",amberColor)
        map.put("61020004",amberColor)
        map.put("61020005",amberColor)
        map.put("61020008",amberColor)
        map.put("61020009",amberColor)
        map.put("61020010",amberColor)
        map.put("61020012",amberColor)
        map.put("61020013",amberColor)
        map.put("61020014",amberColor)
        map.put("61020015",amberColor)
        map.put("62120001",amberColor)
        map.put("62120002",amberColor)
        map.put("51321100",amberColor)
        map.put("51421100",amberColor)
        map.put("11111100",amberColor)
        map.put("51221100",amberColor)
        map.put("42220000",amberColor)
        map.put("44020001",amberColor)
        map.put("41020002",redColor)
        map.put("41020003",redColor)
        map.put("41020004",amberColor)
        map.put("11220200",amberColor)
        map.put("11220201",amberColor)
        map.put("11220201",amberColor)
        map.put("11110601",amberColor)
        map.put("11110601",amberColor)
        map.put("11110801",amberColor)
        map.put("11110501",amberColor)
        map.put("11110401",amberColor)
        map.put("11320300",amberColor)
        map.put("11320301",amberColor)
        map.put("11320400",amberColor)
        map.put("11320401",amberColor)
        map.put("11320500",amberColor)
        map.put("11320501",amberColor)
        map.put("11320600",amberColor)
        map.put("11320601",amberColor)
        map.put("11320700",amberColor)
        map.put("11320701",amberColor)
        map.put("11320801",amberColor)
        map.put("11320800",amberColor)
        map.put("11320800",amberColor)
    }
}