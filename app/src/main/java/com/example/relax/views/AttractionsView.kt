package com.example.relax.views

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Attractions
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
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
import com.example.relax.models.endpoints.Attraction
import com.example.relax.models.endpoints.RepresentativePrice
import com.example.relax.viewmodels.AttractionsViewModel
import com.example.relax.viewmodels.HotelsViewModel
import java.util.Locale

@Composable
fun AttractionsView(
    navController: NavController,
    attractionsViewModel: AttractionsViewModel
) {
    val attractionsList by attractionsViewModel.attractions.collectAsState()

    Scaffold { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            val errorFlag = attractionsViewModel.errorFlag.collectAsState().value
            val status = attractionsViewModel.status.collectAsState().value
            when {
                errorFlag == true -> {
                    AttractionsErrorView(attractionsViewModel, navController)
                }
                attractionsList == null -> {
                    LoadingIndicator()
                }
                status == false -> {
                    AttractionsErrorView(attractionsViewModel, navController)
                }
                attractionsList?.isEmpty() == true -> {
                    EmptyResultsView(message = "No attractions found for this location.")
                }
                else -> {
                    AttractionsList(
                        navController = navController,
                        attractionsViewModel = attractionsViewModel,
                        attractions = attractionsList!!,
                        onAttractionClick = { attraction ->
                            Log.d("AttractionsView", "Clicked attraction: ${attraction.name}")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AttractionsErrorView(
    attractionsViewModel: AttractionsViewModel,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.ErrorOutline,
            contentDescription = "Error",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Search Error",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Error occurred. Please try again. ",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                attractionsViewModel.changeFlag(false)
                attractionsViewModel.navigateToFlights(navController)
            }
        ) { Text("Retry") }
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
                        modifier = Modifier.size(16.dp).padding(end = 6.dp, top = 2.dp),
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

fun formatAttractionPrice(price: RepresentativePrice?): String {
    if (price?.chargeAmount == null || price.currency.isNullOrBlank()) {
        return "Price N/A"
    }

    val totalValue = price.chargeAmount.toDouble()

    return try {
        val format = java.text.NumberFormat.getCurrencyInstance(Locale.getDefault())
        format.currency = java.util.Currency.getInstance(price.currency)
        format.format(totalValue)
    } catch (e: Exception) {
        "${price.currency} ${String.format(Locale.US, "%.2f", totalValue)}"
    }
}