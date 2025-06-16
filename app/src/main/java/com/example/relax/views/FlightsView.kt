package com.example.relax.views

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FlightLand
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.relax.models.endpoints.FlightOffer
import com.example.relax.models.endpoints.PriceInfo
import com.example.relax.models.endpoints.Segment
import com.example.relax.viewmodels.FlightsViewModel
import java.util.Locale
import androidx.core.net.toUri

@Composable
fun ResultView(
    navController: NavController,
    flightsViewModel: FlightsViewModel,
    onFlightOfferClick: (FlightOffer) -> Unit = {}
) {
    val responseState by flightsViewModel.flights.collectAsState()

    val currentResponse = responseState

    var showConfirmationDialog by remember { mutableStateOf(false) }
    var selectedOfferForBooking by remember { mutableStateOf<FlightOffer?>(null) }
    val context = LocalContext.current

    if (showConfirmationDialog && selectedOfferForBooking != null) {
        val offer = selectedOfferForBooking!! // Safe because of the check
        val bookingUrl = "https://flights.booking.com/checkout/ticket-type/${offer.token}"

        ConfirmationDialog(
            offer = offer,
            onConfirm = {
                showConfirmationDialog = false
                selectedOfferForBooking = null
                val intent = Intent(Intent.ACTION_VIEW, bookingUrl.toUri())
                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    println("Could not open URL: $bookingUrl. Error: ${e.message}")
                }
            },
            onDismiss = {
                showConfirmationDialog = false
                selectedOfferForBooking = null
            }
        )
    }
    Scaffold { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            when {
                currentResponse == null -> {
                    LoadingIndicator()
                }
                currentResponse.status == false -> {
                    FlightsErrorView(flightsViewModel, navController)
                }

                currentResponse.data!!.flightOffers!!.isEmpty() -> {
                    EmptyResultsView(message = "No flight offers found matching your criteria.")
                }

                else -> {
                    FlightOffersList(
                        navController = navController,
                        flightViewModel = flightsViewModel,
                        flightOffers = currentResponse.data.flightOffers,
                        onFlightOfferClick = { offer ->
                            selectedOfferForBooking = offer
                            showConfirmationDialog = true
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun LoadingIndicator() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun EmptyResultsView(message: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun FlightsErrorView(
    flightsViewModel: FlightsViewModel,
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
                flightsViewModel.navigateToHome(navController)
            }
        ) { Text("Retry") }
    }
}

@Composable
fun FlightOffersList(
    navController: NavController,
    flightViewModel: FlightsViewModel,
    flightOffers: List<FlightOffer>,
    onFlightOfferClick: (FlightOffer) -> Unit
) {

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { flightViewModel.navigateToHome(navController) }) { Text("Home") }
                Button(onClick = { flightViewModel.navigateToHotels(navController) }) { Text("Hotels") }
                Button(onClick = { flightViewModel.navigateToAttractions(navController) }) { Text("Attractions") }

            }
        }
        item { Text("Available Flights", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp)) }

        items(items = flightOffers, key = { offer -> offer.token ?: offer.hashCode() }) { offer ->
            FlightOfferCard(
                offer = offer,
                onClick = { onFlightOfferClick(offer) }
            )
        }
    }
}

@Composable
fun ConfirmationDialog(
    offer: FlightOffer,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = "Redirect") },
        title = { Text("Confirm Booking") },
        text = {
            Text("You will be redirected to an external site to complete your booking for this flight. Continue?")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Yes, Continue")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("No, Cancel")
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class) // Required for Card onClick
@Composable
fun FlightOfferCard(
    offer: FlightOffer,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatPrice(offer.priceBreakdown?.total),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

            }

            Spacer(modifier = Modifier.height(12.dp))

            offer.segments?.forEachIndexed { index, segment ->
                SegmentView(segment = segment)
                if (index < (offer.segments.size - 1)) {
                    Text(
                        text = "--------- Connection ---------", // Or use a Divider
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        textAlign = TextAlign.Center,
                        letterSpacing = 2.sp // Creates dashed effect
                    )
                }
            }
        }
    }
}

@Composable
fun SegmentView(segment: Segment) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {

                Icon(
                    imageVector = Icons.Filled.FlightTakeoff,
                    contentDescription = "Departure",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = segment.departureAirport?.code ?: "???",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = segment.departureTime!!,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 20.dp)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            val stopsCount = (segment.legs?.size ?: 1) - 1
            val stopsText = when {
                stopsCount <= 0 -> "Direct"
                stopsCount == 1 -> "1 Stop"
                else -> "$stopsCount Stops"
            }

            Text(
                text = stopsText,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 2.dp)
            )

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "to",
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.outline
            )
        }

        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = segment.arrivalAirport?.code ?: "???",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End
                )
                Spacer(modifier = Modifier.width(4.dp))

                Icon(
                    imageVector = Icons.Filled.FlightLand,
                    contentDescription = "Arrival",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = segment.arrivalTime!!,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(end = 20.dp),
                textAlign = TextAlign.End
            )
        }
    }
}



fun formatPrice(priceInfo: PriceInfo?): String {
    if (priceInfo == null || priceInfo.currencyCode == null || priceInfo.units == null) {
        return "N/A"
    }
    val totalValue = priceInfo.units + (priceInfo.nanos?.toDouble() ?: 0.0) / 1_000_000_000.0

    return try {
        val format = java.text.NumberFormat.getCurrencyInstance(Locale.getDefault())
        format.currency = java.util.Currency.getInstance(priceInfo.currencyCode)
        format.format(totalValue)
    } catch (e: Exception) {
        "${priceInfo.currencyCode} ${String.format(Locale.US, "%.2f", totalValue)}"
    }
}