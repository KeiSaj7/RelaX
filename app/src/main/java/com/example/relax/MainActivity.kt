package com.example.relax

import com.example.relax.models.navigationRoutes.FlightsRoute
import com.example.relax.models.navigationRoutes.HomeRoute
import com.example.relax.models.navigationRoutes.HotelsRoute
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.relax.models.navigationRoutes.AttractionsRoute
import com.example.relax.ui.theme.RelaXTheme
import com.example.relax.viewmodels.AttractionsViewModel
import com.example.relax.viewmodels.FlightsViewModel
import com.example.relax.viewmodels.HomeViewModel
import com.example.relax.viewmodels.HotelsViewModel
import com.example.relax.views.AttractionsView
import com.example.relax.views.HotelsView
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
                NavHost(navController = navController, startDestination = HomeRoute) {
                    composable<HomeRoute> {
                        val homeViewModel: HomeViewModel = hiltViewModel()
                        StartScreen(
                            navController = navController,
                            homeViewModel = homeViewModel
                        )
                    }

                    composable<FlightsRoute> {
                        val flightViewModel: FlightsViewModel = hiltViewModel()
                        ResultView(
                            navController = navController,
                            flightsViewModel = flightViewModel
                        )
                    }

                    composable<HotelsRoute> {
                        val hotelsViewModel: HotelsViewModel = hiltViewModel()
                        HotelsView(
                            navController = navController,
                            hotelViewModel = hotelsViewModel
                        )
                    }

                    composable<AttractionsRoute> {
                        val attractionsViewmodel: AttractionsViewModel = hiltViewModel()
                        AttractionsView(
                            navController = navController,
                            attractionsViewModel = attractionsViewmodel
                        )
                    }
                }

            }
        }
    }
}
