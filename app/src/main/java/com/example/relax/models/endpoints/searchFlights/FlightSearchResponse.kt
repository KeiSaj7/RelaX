package com.example.relax.models.endpoints.searchFlights

import com.google.gson.annotations.SerializedName

data class FlightSearchResponse(
    @SerializedName("status") val status: Boolean?,
    @SerializedName("message") val message: String?,
    @SerializedName("timestamp") val timestamp: Long?,
    @SerializedName("data") val data: DataContainer?
)

data class DataContainer(
    @SerializedName("flightOffers") val flightOffers: List<FlightOffer>?
)

data class FlightOffer(
    @SerializedName("segments") val segments: List<Segment>?,
    @SerializedName("priceBreakdown") val priceBreakdown: MinimalPriceBreakdown?,
    @SerializedName("pointOfSale") val pointOfSale: String?,
    @SerializedName("tripType") val tripType: String?
)

data class Segment(
    @SerializedName("departureAirport") val departureAirport: MinimalAirportInfo?,
    @SerializedName("arrivalAirport") val arrivalAirport: MinimalAirportInfo?,
    @SerializedName("departureTime") val departureTime: String?,
    @SerializedName("arrivalTime") val arrivalTime: String?,
    @SerializedName("legs") val legs: List<Leg>?
)

data class Leg(
    @SerializedName("arrivalAirport") val legArrivalAirport: LegArrivalAirport?
)

data class LegArrivalAirport(
    @SerializedName("code") val code: String?
)

data class MinimalAirportInfo(
    @SerializedName("code") val code: String?
)

data class MinimalPriceBreakdown(
    @SerializedName("total") val total: PriceInfo?
)

data class PriceInfo(
    @SerializedName("currencyCode") val currencyCode: String?,
    @SerializedName("units") val units: Long?,
    @SerializedName("nanos") val nanos: Int?
)