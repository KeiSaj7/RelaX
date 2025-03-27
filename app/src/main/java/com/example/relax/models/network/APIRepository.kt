package com.example.relax.models.network

import com.example.relax.models.PokemonResponse
import javax.inject.Inject

class APIRepository @Inject constructor(private val apiService: APIService) {
    suspend fun getPokemon(): PokemonResponse = apiService.getPokemon()
}