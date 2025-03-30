package com.example.relax

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.relax.ui.theme.RelaXTheme
import com.example.relax.viewmodels.HomeViewModel
import com.example.relax.views.StartScreen
import com.example.relax.views.ResultView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RelaXTheme {
                val navController = rememberNavController()
                val homeViewModel: HomeViewModel = hiltViewModel()
                NavHost(navController = navController, startDestination = "home_view") {

                    composable("home_view") {
                        StartScreen(
                            navController = navController,
                            homeViewModel = homeViewModel
                        )
                    }
                    composable("results_screen") {
                        ResultView(
                            homeViewModel = homeViewModel
                        )
                    }
                }

            }
        }
    }
}