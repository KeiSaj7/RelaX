package com.example.relax.models.network

import javax.inject.Inject

class APIRepository @Inject constructor(private val apiService: APIService) {
    suspend fun getPokemon() = apiService.getPokemon()
}