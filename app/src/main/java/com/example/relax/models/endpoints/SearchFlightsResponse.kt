package com.example.relax.models.endpoints

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class FlightSearchResponse(
    @SerializedName("status") val status: Boolean?,
    @SerializedName("message") val message: String?,
    @SerializedName("timestamp") val timestamp: Long?,
    @SerializedName("data") val data: DataContainer?
)

@Serializable
data class DataContainer(
    @SerializedName("flightOffers") val flightOffers: List<FlightOffer>?
)

@Serializable
data class FlightOffer(
    @SerializedName("segments") val segments: List<Segment>?,
    @SerializedName("priceBreakdown") val priceBreakdown: MinimalPriceBreakdown?,
    @SerializedName("pointOfSale") val pointOfSale: String?,
    @SerializedName("tripType") val tripType: String?
)

@Serializable
data class Segment(
    @SerializedName("departureAirport") val departureAirport: MinimalAirportInfo?,
    @SerializedName("arrivalAirport") val arrivalAirport: MinimalAirportInfo?,
    @SerializedName("departureTime") val departureTime: String?,
    @SerializedName("arrivalTime") val arrivalTime: String?,
    @SerializedName("legs") val legs: List<Leg>?
)

@Serializable
data class Leg(
    @SerializedName("arrivalAirport") val legArrivalAirport: LegArrivalAirport?
)

@Serializable
data class LegArrivalAirport(
    @SerializedName("code") val code: String?
)

@Serializable
data class MinimalAirportInfo(
    @SerializedName("code") val code: String?
)

@Serializable
data class MinimalPriceBreakdown(
    @SerializedName("total") val total: PriceInfo?
)

@Serializable
data class PriceInfo(
    @SerializedName("currencyCode") val currencyCode: String?,
    @SerializedName("units") val units: Long?,
    @SerializedName("nanos") val nanos: Int?
)