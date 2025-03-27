package com.example.relax.models.network

import com.example.relax.models.PokemonResponse
import retrofit2.http.GET

public interface APIService {
    @GET("pokemon/ditto")
    suspend fun getPokemon(): PokemonResponse
}