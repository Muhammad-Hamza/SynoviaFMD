package com.scanapp.network.remote

data class SupplyModel(
    val operationCode: String,
    val information: String,
    val state: String,
    val productName: String,
    val canReactivate: Boolean

)
