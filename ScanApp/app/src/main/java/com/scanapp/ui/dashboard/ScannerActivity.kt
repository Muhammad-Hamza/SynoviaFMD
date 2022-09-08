package com.scanapp.ui.dashboard

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.scanapp.R


class ScannerActivity : AppCompatActivity() {
    private lateinit var codeScanner: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)
        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)
        codeScanner = CodeScanner(this, scannerView)
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
//                Log.e("asd",it.text)
//                Toast.makeText(this, it.text, Toast.LENGTH_LONG).show()
                val returnIntent = Intent()
                returnIntent.putExtra("result", it.text)
                setResult(RESULT_OK, returnIntent)
                finish()
            }
        }
        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }
    //2022-09-08 00:04:25.162 10561-10561/com.scanapp E/asd: 01022564353848351725123110a0001210000000001
    //2022-09-08 00:07:06.826 10561-10561/com.scanapp E/asd: 0102256435384835172502091000001210000000003
    //2022-09-08 00:07:49.099 10561-10561/com.scanapp E/asd: 01022564353848351725020910000012100000000031
    //2022-09-08 00:08:30.567 10561-10561/com.scanapp E/asd: 0102256435384835172502091000099210000000011

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }
}