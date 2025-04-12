package com.example.relax.models.endpoints

import com.google.gson.annotations.SerializedName

data class SearchHotelsResponse(
    @SerializedName("status") val status: Boolean?,
    @SerializedName("message") val message: String?,
    @SerializedName("timestamp") val timestamp: Long?,
    @SerializedName("data") val data: SearchHotelsData?
)

data class SearchHotelsData(
    @SerializedName("hotels") val hotels: List<Hotel>?
)

data class Hotel(
    @SerializedName("hotel_id") val hotelId: Long?,
    @SerializedName("accessibilityLabel") val accessibilityLabel: String?,
    @SerializedName("property") val property: Property
)

data class Property(
    @SerializedName("name") val hotelName: String?,
    @SerializedName("reviewCount") val reviewCount: Int?,
    @SerializedName("reviewScore") val reviewScore: Float?,
    @SerializedName("qualityClass") val qualityClass: String?,
    @SerializedName("checkin") val checkin: Checkin?,
    @SerializedName("checkout") val checkout: Checkout?,
    @SerializedName("priceBreakdown") val priceBreakDown: PriceBreakDown?,
)

data class PriceBreakDown(
    @SerializedName("grossPrice") val grossPrice: Checkout?,
)

data class GrossPrice(
    @SerializedName("value") val value: Float?,
    @SerializedName("currency") val currency: String?,
)

data class Checkin(
    @SerializedName("untilTime") val untilTime: String?,
    @SerializedName("fromTime") val fromTime: String?
)

data class Checkout(
    @SerializedName("untilTime") val untilTime: String?,
    @SerializedName("fromTime") val fromTime: String?
)