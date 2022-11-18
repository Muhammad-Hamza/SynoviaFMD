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

//    val CLIENT_CRED_TE ="rZFse0cCPCuQkkNy1Lpm09Gr"
//    val CLIENT_ID_TE ="MzkX7fY5QjMLRFgo33XLkpPf"
//    val CLIENT_CRED_QA ="aa24dd48-bb7d-4fc9-b5a0-248b1bda0207"
//    val CLIENT_ID_QA ="b6e49ab1-b04e-4f55-bbaf-270ad2b8ed8c"
// val CLIENT_CRED_QA ="890dcd23-5e0c-45ba-8e71-d3de3ed47dc1"
//    val CLIENT_ID_QA ="b803d3fc-4a86-4186-a43b-e82b6cfc9543"
    val CLIENT_SECRET_KEY_PROD ="845e7db2-5222-4b11-ad74-ac7930e99c8e"
    val CLIENT_ID_PROD ="cf0444d5-3308-4efe-8f3a-cd9e983d6d1c"

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
        var call = apiService.getToken("client_credentials", CLIENT_ID_PROD, CLIENT_SECRET_KEY_PROD)

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

