package com.example.relax.models.network

import retrofit2.http.GET

public interface APIService {
    @GET("pokemon/ditto")
    suspend fun getPokemon(): List<String>
}