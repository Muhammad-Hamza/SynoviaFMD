package com.scanapp.network.remote

import com.google.gson.annotations.SerializedName

data class SupplyModel(
    val operationCode: String,
    val information: String,
    val state: String,
    @SerializedName("alertId")
    val alertId: String,
    @SerializedName("alertCode")
    val alertCode: String,
    @SerializedName("amsLink")
    val amsLink: String,
    val productName: String,
    val canReactivate: Boolean,
    @SerializedName("warning")
    val warning: String

)
