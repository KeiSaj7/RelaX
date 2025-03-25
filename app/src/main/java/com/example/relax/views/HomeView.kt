package com.example.relax.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.relax.R
import com.example.relax.viewmodels.HolidayViewModel
import com.example.relax.viewmodels.HomeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel

class HomeView {
    @Preview(showBackground = true)
    @Composable
    fun StartScreen(viewModel: HomeViewModel = viewModel(), viewModel2: HolidayViewModel = hiltViewModel()){
        val startingLocation by viewModel.startingLocation.collectAsState()
        val destination by viewModel.destination.collectAsState()
        val appName by remember { mutableStateOf("Relax") }
        viewModel2.getPokemon()
        //val pokemons by viewModel2.pokemons.collectAsState( )
        Column(
            modifier = Modifier
                .fillMaxSize()
        ){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 100.dp)
            ){
                RenderAppName(appName)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ){
                Text(
                    text = "From",
                    fontSize = 24.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(x = 50.dp)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ){
                OutlinedTextField(
                    value = startingLocation,
                    onValueChange = { text ->
                        viewModel.updateStartingLocation(text)
                    },
                    modifier = Modifier
                        .padding(16.dp)
                        .size(width = 160.dp, height = 50.dp)
                    )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ){
                Image(
                    painter = painterResource(id = R.drawable.upanddownarrows),
                    contentDescription = "Up and down arrows image",
                    modifier = Modifier
                        .size(160.dp)
                        .padding(16.dp),
                    contentScale = ContentScale.Fit
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ){
                Text(
                    text = "To",
                    fontSize = 24.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(x = 250.dp)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ){
                OutlinedTextField(
                    value = destination,
                    onValueChange = { text ->
                        viewModel.updateDestination(text)
                    },
                    modifier = Modifier
                        .padding(16.dp)
                        .size(width = 160.dp, height = 50.dp)
                        .offset(x = 180.dp)
                )
            }
            /*
            Row(
                modifier = Modifier.padding(16.dp)
            ){
                Button(onClick = {viewModel2.getPokemon()}) {
                    Text("Fetch Data")
                }
                pokemons.forEach { item -> Text(text = item) }
            }*/
        }
    }

    @Composable
    fun RenderAppName(text: String){
        Text(
            text = text,
            fontSize = 30.sp,
            fontFamily = FontFamily.SansSerif,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }

}