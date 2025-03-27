package com.example.relax.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.relax.models.PokemonResponse
import com.example.relax.models.network.APIRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HolidayViewModel @Inject constructor(
    private val repository: APIRepository
) : ViewModel(){
    private val _pokemon = MutableStateFlow<PokemonResponse?>(null)
    val pokemon: StateFlow<PokemonResponse?> = _pokemon

    fun getPokemon(){
        viewModelScope.launch {
            try{
                val result = repository.getPokemon()
                Log.d("API_RESPONSE", "Success: $result")
                _pokemon.value = result
            } catch (e: Exception){
                Log.e("API_ERROR", "Error: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}