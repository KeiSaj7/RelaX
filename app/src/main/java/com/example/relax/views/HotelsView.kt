package com.example.relax.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.relax.models.endpoints.GrossPrice
import com.example.relax.models.endpoints.Hotel
import com.example.relax.viewmodels.HotelsViewModel
import java.util.Locale

@Composable
fun HotelsView (navController: NavController, hotelViewModel: HotelsViewModel)
{
    val hotelsResponse by hotelViewModel.hotels.collectAsState()

    Scaffold { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            val hotels = hotelsResponse?.data?.hotels
            when {
                hotels == null -> {
                    LoadingIndicator()
                    //ErrorView(message = "Received response but hotel data is missing.")
                }
                hotels.isEmpty() -> {
                    EmptyResultsView(message = "No hotels found matching your criteria.") // Reuse
                }
                else -> {
                    HotelsList(
                        navController = navController,
                        hotels = hotels,
                        onHotelClick = { /* Handle hotel click if needed later */ }
                    )
                }
            }
            }
        }
}

@Composable
fun HotelsList(
    navController: NavController,
    hotels: List<Hotel>,
    onHotelClick: (Hotel) -> Unit = {} // Callback for item clicks if needed
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Button(onClick = {navController.popBackStack()} ){
            Text("Flights")
        }}
        item {
            Text(
                "Available Hotels",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        items(
            items = hotels,
            key = { hotel -> hotel.hotelId ?: hotel.hashCode() }
        ) { hotel ->
            HotelCard(
                hotel = hotel,
                onClick = { onHotelClick(hotel) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelCard(
    hotel: Hotel,
    onClick: () -> Unit = {} // Add callback if cards should be clickable later
) {
    val property = hotel.property

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // --- Hotel Name ---
            Text(
                text = property.hotelName ?: "Hotel Name Unavailable",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            // --- Reviews ---
            if (property.reviewScore != null || property.reviewCount != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Review Score",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    val scoreText = property.reviewScore?.let { String.format(Locale.US, "%.1f/10", it) } ?: ""
                    val countText = property.reviewCount?.let { "($it reviews)" } ?: ""
                    Text(
                        text = "$scoreText $countText".trim(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }


            // --- Check-in / Check-out Row ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Check-in Column
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.AutoMirrored.Filled.Login,
                            contentDescription = "Check-in",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Check-in", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = property.checkinDate,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(start = 20.dp)
                    )
                    val checkinTimeText = formatCheckInOutTime(property.checkin?.fromTime, property.checkin?.untilTime)
                    if (checkinTimeText.isNotEmpty()) {
                        Text(
                            text = checkinTimeText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 20.dp)
                        )
                    }
                }

                // Check-out Column
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Check-out", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Check-out",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = property.checkoutDate,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(end = 20.dp)
                    )
                    val checkoutTimeText = formatCheckInOutTime(property.checkout?.fromTime, property.checkout?.untilTime)
                    if (checkoutTimeText.isNotEmpty()) {
                        Text(
                            text = checkoutTimeText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(end = 20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- Price ---
            val grossPrice = property.priceBreakDown?.grossPrice
            if (grossPrice != null) {
                Text(
                    text = formatHotelPrice(grossPrice),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // --- Accessibility Label / Description ---
            if (!hotel.accessibilityLabel.isNullOrBlank()) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                )
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "Description",
                        modifier = Modifier.size(16.dp).padding(end = 6.dp, top = 2.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = hotel.accessibilityLabel,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}


// --- Helper function to format Check-in/out times ---
private fun formatCheckInOutTime(fromTime: String?, untilTime: String?): String {
    val parts = mutableListOf<String>()
    if (!fromTime.isNullOrBlank()) parts.add("From $fromTime")
    if (!untilTime.isNullOrBlank()) parts.add("Until $untilTime")
    return parts.joinToString(" ")
}

// --- Helper function to format Hotel Price ---
fun formatHotelPrice(grossPrice: GrossPrice?): String {
    if (grossPrice?.value == null || grossPrice.currency.isNullOrBlank()) {
        return "N/A"
    }

    val totalValue = grossPrice.value.toDouble()

    return try {
        val format = java.text.NumberFormat.getCurrencyInstance(Locale.getDefault())
        format.currency = java.util.Currency.getInstance(grossPrice.currency)
        format.format(totalValue)
    } catch (e: Exception) {
        "${grossPrice.currency} ${String.format(Locale.US, "%.2f", totalValue)}"
    }
}