package com.example.relax.models.endpoints

import com.google.gson.annotations.SerializedName

data class Attractions(
    @SerializedName("status") val status: Boolean?,
    @SerializedName("message") val message: String?,
    @SerializedName("timestamp") val timestamp: Long?,
    @SerializedName("data") val data: Products?
)

data class Products(
    @SerializedName("products") val products: List<Attraction>?
)

data class Attraction(
    @SerializedName("name") val name: String?,
    @SerializedName("shortDescription") val shortDescription: String?,
    @SerializedName("representativePrice") val representativePrice: RepresentativePrice?,
    @SerializedName("reviewsStats") val reviewsStats: ReviewsStats?
)

data class RepresentativePrice(
    @SerializedName("chargeAmount") val chargeAmount: Float?,
    @SerializedName("currency") val currency: String?
)

data class ReviewsStats(
    @SerializedName("combinedNumericStats") val combinedNumericStats: CombinedNumericStats
)

data class CombinedNumericStats(
    @SerializedName("average") val average: Float?,
    @SerializedName("total") val total: Int?
)