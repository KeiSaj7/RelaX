package com.example.relax.views

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.relax.viewmodels.HomeViewModel

@Composable
fun FlightResultsScreen(navController: NavController, homeViewModel: HomeViewModel = hiltViewModel()){
    Text(
        text = "RESULTS HERE",
        fontSize = 30.sp,
        fontFamily = FontFamily.SansSerif,
        color = Color.Black,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}
