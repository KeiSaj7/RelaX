package com.example.relax.models.endpoints

import com.google.gson.annotations.SerializedName

data class HotelDestinationResponse(
    @SerializedName("status") val status: Boolean?,
    @SerializedName("message") val message: String?,
    @SerializedName("timestamp") val timestamp: Long?,
    @SerializedName("data") val data: List<HotelDestinationId>?
)

data class HotelDestinationId(
    @SerializedName("dest_id") val destId: String?,
    @SerializedName("search_type") val searchType: String?
)