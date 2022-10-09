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
    @SerializedName("canReactivateUntil")
    val canReactivateUntil: String,
    @SerializedName("isIntermarket")
    val isIntermarket: String,
    @SerializedName("productWithdrawalReason")
    val productWithdrawalReason: String,
    @SerializedName("batchRecallReason")
    val batchRecallReason: String,
    @SerializedName("amsLink")
    val amsLink: String,
    val productName: String,
    val canReactivate: Boolean,
    @SerializedName("warning")
    val warning: String

)
