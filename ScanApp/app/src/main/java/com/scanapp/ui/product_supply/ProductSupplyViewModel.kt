package com.scanapp.ui.product_supply

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.scanapp.Listeners
import com.scanapp.network.ApiClient
import com.scanapp.network.ApiInterface
import com.scanapp.network.NoConnectivityException
import com.scanapp.network.remote.SupplyModel
import com.scanapp.network.remote.TokenResponse
import com.scanapp.ui.TokenViewModel
import com.scanapp.util.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class ProductSupplyViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var mErrorListener: Listeners.DialogInteractionListener

    fun attachErrorListener(mErrorListener: Listeners.DialogInteractionListener) {
        this.mErrorListener = mErrorListener
    }

    companion object {
        private val TAG = TokenViewModel::class.java.simpleName
    }

    fun postSupplyInfo(
        context: Context,
        productCode: String,
        serialNumber: String,
        batch: String,
        expiry: String,
        mListener: onCompleteListener
    ) {
        mErrorListener.addDialog()
        val apiService = ApiClient.client(context).create(ApiInterface::class.java)
        var call = apiService.getSupplyRequest(
            productCode,
            serialNumber,
            batch,
            expiry,
            Utils.getSupplyHeader(context)
        )

        call?.enqueue(object : Callback<SupplyModel> {
            override fun onFailure(call: Call<SupplyModel>, t: Throwable) {
                mErrorListener.dismissDialog()
                if (t is NoConnectivityException) {
                } else {
                    mErrorListener.addErrorDialog()
                }
            }

            override fun onResponse(call: Call<SupplyModel>, response: Response<SupplyModel>) {
                Log.d(TAG, response.raw().toString())
                mListener.onDataFetch(response.body()!!)
                mErrorListener.dismissDialog()
            }
        })
    }

    interface onCompleteListener {
        fun onDataFetch(model: SupplyModel)
    }
}
