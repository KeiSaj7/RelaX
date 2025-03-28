package com.example.relax.models

data class Flights (
    val status: Boolean,
    val message: String,
    val timestamp: Long,
    val data: List<Flight>
)