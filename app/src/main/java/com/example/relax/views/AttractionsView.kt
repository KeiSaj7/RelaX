package com.example.relax.views

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Attractions // Example icon for attraction
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info // Reusing for description
import androidx.compose.material.icons.filled.Star // Reusing for reviews
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.relax.models.endpoints.Attraction // Your Attraction data class
import com.example.relax.models.endpoints.RepresentativePrice // Your Price data class
import com.example.relax.viewmodels.AttractionsViewModel
import java.util.Locale

// Assuming your ViewModel has a way to represent loading/error/success for the list
// For simplicity, we'll infer from the attractions list being null or empty.
// A dedicated StateFlow<AttractionListResultState> in VM would be more robust.

@Composable
fun AttractionsView(
    navController: NavController,
    attractionsViewModel: AttractionsViewModel
) {
    // Collect the list of attractions from the ViewModel
    // This directly observes repository.attractions via the ViewModel
    val attractionsList by attractionsViewModel.attractions.collectAsState()

    // Optional: Add a loading state if your ViewModel exposes one for the initial fetch
    // val isLoading by attractionsViewModel.isLoading.collectAsState()
    // Optional: Add an error state
    // val error by attractionsViewModel.errorMessage.collectAsState()

    // Trigger initial fetch if not already done (ViewModel's init should handle this)
    // LaunchedEffect(Unit) {
    // if (attractionsList == null) { // Or check a specific loading state
    // attractionsViewModel.getAttractions() // Assuming VM has this public fun
    // }
    // }

    Scaffold { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            // Handle different states
            // This logic assumes that if attractionsList is null, it's loading or an error occurred before data arrived.
            // A more robust ViewModel would expose a sealed interface for ResultState (Idle, Loading, Success, Error).
            when {
                attractionsList == null -> { // Assuming VM has a flag for fetch attempt
                    LoadingIndicator()
                }
                attractionsList?.isEmpty() == true -> {
                    EmptyResultsView(message = "No attractions found for this location.")
                }
                else -> {
                    AttractionsList(
                        navController = navController, // For potential navigation buttons
                        attractionsViewModel = attractionsViewModel, // For potential actions
                        attractions = attractionsList!!, // Safe due to !isNullOrEmpty check
                        onAttractionClick = { attraction ->
                            Log.d("AttractionsView", "Clicked attraction: ${attraction.name}")
                            // TODO: Handle attraction click (e.g., navigate to detail screen, show dialog for booking link)
                            // For now, let's assume you'll add booking link dialog similar to HotelsView later
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AttractionsList(
    navController: NavController,
    attractionsViewModel: AttractionsViewModel,
    attractions: List<Attraction>,
    onAttractionClick: (Attraction) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Optional: Navigation buttons to Home/Flights/Hotels if needed
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(onClick = { attractionsViewModel.navigateToHome(navController) }) { Text("Home") }
                Button(onClick = { attractionsViewModel.navigateToFlights(navController) }) { Text("Flights") }
                Button(onClick = { attractionsViewModel.navigateToHotels(navController) }) { Text("Hotels") }
            }
        }
        item {
            Text(
                "Popular Attractions",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        items(
            items = attractions,
            // Use a stable key if Attraction has a unique ID, otherwise fallback to hashCode
            key = { attraction -> attraction.name ?: attraction.hashCode() }
        ) { attraction ->
            AttractionCard(
                attraction = attraction,
                onClick = { onAttractionClick(attraction) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttractionCard(
    attraction: Attraction,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // --- Attraction Name ---
            Text(
                text = attraction.name ?: "Attraction Name Unavailable",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // --- Reviews ---
            val reviews = attraction.reviewsStats?.combinedNumericStats
            if (reviews?.average != null || reviews?.total != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Average Review Score",
                        tint = Color(0xFFFFC107), // Amber/Gold
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    val scoreText = reviews.average?.let { String.format(Locale.US, "%.1f/10", it) } ?: ""
                    val countText = reviews.total?.let { "($it reviews)" } ?: ""
                    Text(
                        text = "$scoreText $countText".trim(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // --- Short Description ---
            if (!attraction.shortDescription.isNullOrBlank()) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "Description",
                        modifier = Modifier.size(16.dp).padding(end = 6.dp, top = 2.dp), // Adjust padding
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = attraction.shortDescription,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // --- Price ---
            if (attraction.representativePrice != null) {
                Text(
                    text = formatAttractionPrice(attraction.representativePrice),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// --- Helper function to format Attraction Price ---
fun formatAttractionPrice(price: RepresentativePrice?): String {
    if (price?.chargeAmount == null || price.currency.isNullOrBlank()) {
        return "Price N/A" // Or "Free" or "" depending on how you want to show no price
    }

    val totalValue = price.chargeAmount.toDouble()

    return try {
        val format = java.text.NumberFormat.getCurrencyInstance(Locale.getDefault())
        format.currency = java.util.Currency.getInstance(price.currency)
        format.format(totalValue)
    } catch (e: Exception) {
        // Fallback for invalid currency code or locale issues
        "${price.currency} ${String.format(Locale.US, "%.2f", totalValue)}"
    }
}