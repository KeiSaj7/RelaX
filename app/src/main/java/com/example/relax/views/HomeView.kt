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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.navigation.NavController
import com.example.relax.R
import com.example.relax.viewmodels.HomeViewModel
import kotlinx.coroutines.delay

@Preview(showBackground = true)
@Composable
fun StartScreen(navController: NavController , homeViewModel : HomeViewModel = hiltViewModel()){
    var startingLocation by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    val appName by remember { mutableStateOf("Relax") }
    val startingLocationResponse by homeViewModel.startingLocation.collectAsState()
    val destinationResponse by homeViewModel.destination.collectAsState()

    // Effects to prevent often API calls
    LaunchedEffect(startingLocation) {
        delay(2000)
        if(startingLocation.isNotEmpty()){
            homeViewModel.getDestination(startingLocation, "start")
        }
    }
    LaunchedEffect(destination) {
        delay(2000)
        if(destination.isNotEmpty()){
            homeViewModel.getDestination(destination, "")
        }
    }

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
                    startingLocation = text
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
                    destination = text
                },
                modifier = Modifier
                    .padding(16.dp)
                    .size(width = 160.dp, height = 50.dp)
                    .offset(x = 180.dp)
            )
        }

        Row(
            modifier = Modifier.padding(16.dp)
        ){
            val selectedStartId: String? = startingLocationResponse?.firstOrNull()?.id
            val selectedDestId: String? = destinationResponse?.firstOrNull()?.id

            Button(
                onClick = {
                    // Check if selected IDs are available before calling
                    if (selectedStartId != null && selectedDestId != null) {
                        homeViewModel.getFlights(
                            fromId = selectedStartId,
                            toId = selectedDestId,
                            departDate = "2025-05-01" // TODO: Get this from a Date Picker state
                        )
                        // Navigate after triggering the fetch
                        navController.navigate("results_screen")
                    } else {
                        // TODO: Show error message to user (e.g., via Snackbar)
                        // that locations must be selected
                    }
                },
                // Disable button if selected locations aren't ready
                enabled = (selectedStartId != null && selectedDestId != null) // Add date validation too
            ) {
                Text("Search")
            }
        }
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
