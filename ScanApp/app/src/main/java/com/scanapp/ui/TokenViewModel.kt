package com.scanapp.ui

import android.app.Application
import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import com.scanapp.Listeners
import com.scanapp.R
import com.scanapp.network.ApiClient
import com.scanapp.network.ApiInterface
import com.scanapp.network.NoConnectivityException
import com.scanapp.network.remote.TokenResponse
import retrofit2.Call
import retrofit2.Response
import java.io.IOException
import retrofit2.Callback

class TokenViewModel(application: Application) : AndroidViewModel(application)
{

    private lateinit var mErrorListener: Listeners.DialogInteractionListener

    fun attachErrorListener(mErrorListener: Listeners.DialogInteractionListener)
    {
        this.mErrorListener = mErrorListener
    }

    companion object
    {
        private val TAG = TokenViewModel::class.java.simpleName
    }

    fun getToken(context: Context, email: String, password: String, mListener: onTokenCompleteListener)
    {
        mErrorListener.addDialog()
        val apiService = ApiClient.client(context).create(ApiInterface::class.java)
        Log.d(TAG, "===============LOGGING===============")
        var call = apiService.getToken("client_credentials", "MzkX7fY5QjMLRFgo33XLkpPf", "rZFse0cCPCuQkkNy1Lpm09Gr")

        call?.enqueue(object : Callback<TokenResponse>
        {
            override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>)
            {
                Log.d(TAG, response.raw().toString())
                mErrorListener.dismissDialog()
                try
                {
                    val userResponse = response.body()
                    if (userResponse != null) mListener.onTokenFetched(userResponse)
                } catch (e: IOException)
                {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<TokenResponse>, t: Throwable)
            {
                mErrorListener.dismissDialog()
                if (t is NoConnectivityException)
                {
                }
                else
                {
                    mErrorListener.addErrorDialog()
                }
            }
        })
    }

    interface onTokenCompleteListener
    {
        fun onTokenFetched(tokenResponse: TokenResponse)
    }

}

