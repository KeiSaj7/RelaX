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
    flightsViewModel: FlightsViewModel, // Accept the shared ViewModel instance
    // Callback for when a user selects a flight offer (to be handled by the caller, e.g., navigate)
    onFlightOfferClick: (FlightOffer) -> Unit = {} // Default empty lambda is fine for now
) {
    // Collect the state from the passed-in ViewModel
    val responseState by flightsViewModel.flights.collectAsState()

    // Use a local variable for smart casting within the 'when' block
    val currentResponse = responseState

    // --- State for the confirmation dialog ---
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var selectedOfferForBooking by remember { mutableStateOf<FlightOffer?>(null) }
    val context = LocalContext.current // Get context for launching intent

    // --- Dialog Logic ---
    if (showConfirmationDialog && selectedOfferForBooking != null) {
        val offer = selectedOfferForBooking!! // Safe because of the check
        val bookingUrl = "https://flights.booking.com/checkout/ticket-type/${offer.token}" // Your static link for now
        // In a real app, bookingUrl might be:
        // val bookingUrl = constructBookingUrl(offer.token, flightViewModel.routeArgs.pointOfSale) // Example
        // or from offer.supplierInfo.bookingLink if available.

        ConfirmationDialog(
            offer = offer, // Pass the offer to display info if needed
            onConfirm = {
                showConfirmationDialog = false
                selectedOfferForBooking = null
                // Create an Intent to open the URL
                val intent = Intent(Intent.ACTION_VIEW, bookingUrl.toUri())
                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    // Handle cases where no browser is available or URL is invalid
                    // You might show a Snackbar or Toast here
                    println("Could not open URL: $bookingUrl. Error: ${e.message}")
                }
            },
            onDismiss = {
                showConfirmationDialog = false
                selectedOfferForBooking = null
            }
        )
    }
    // Scaffold provides basic Material layout structure (optional but good practice)
    Scaffold { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues), // Apply padding from Scaffold
            color = MaterialTheme.colorScheme.background
        ) {
            // Use 'when' to handle the different states of the response
            when {
                // 1. Loading state
                currentResponse == null -> {
                    LoadingIndicator()
                }

                currentResponse.data!!.flightOffers!!.isEmpty() -> {
                    EmptyResultsView(message = "No flight offers found matching your criteria.")
                }

                else -> {
                    // Pass the non-null list and the click handler to the list composable
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

// --- Composables for different states ---

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
fun ErrorView(message: String) {
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
            text = "Search Error", // Changed title slightly
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        // Optional: Add a "Retry" button here if applicable
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
            color = MaterialTheme.colorScheme.onSurfaceVariant // Softer color
        )
    }
}

// --- Composable for the list of flights ---

@Composable
fun FlightOffersList(
    navController: NavController,
    flightViewModel: FlightsViewModel,
    flightOffers: List<FlightOffer>,
    onFlightOfferClick: (FlightOffer) -> Unit // Callback for item clicks
) {

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp) // Spacing between cards
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly // Space out buttons
            ) {
                Button(onClick = { flightViewModel.navigateToHome(navController) }) { Text("Home") }
                Button(onClick = { flightViewModel.navigateToHotels(navController) }) { Text("Hotels") }
            }
        }
        item { Text("Available Flights", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp)) }

        items(items = flightOffers, key = { offer -> offer.token ?: offer.hashCode() }) { offer -> // Use a stable key if possible
            FlightOfferCard(
                offer = offer,
                // When this card is clicked, invoke the callback passed from ResultView
                onClick = { onFlightOfferClick(offer) }
            )
        }
    }
}

@Composable
fun ConfirmationDialog(
    offer: FlightOffer, // You can use this to display offer details in dialog if desired
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    // You can customize this dialog further.
    // For a simple one:
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = "Redirect") },
        title = { Text("Confirm Booking") },
        text = {
            Text("You will be redirected to an external site to complete your booking for this flight. Continue?")
            // Optionally, display some offer details here:
            // Text("\nPrice: ${formatPrice(offer.priceBreakdown?.total)}")
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

// --- Composable for a single flight offer card ---

@OptIn(ExperimentalMaterial3Api::class) // Required for Card onClick
@Composable
fun FlightOfferCard(
    offer: FlightOffer,
    onClick: () -> Unit // Callback for when this specific card is clicked
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        onClick = onClick // Make the whole card clickable
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Row for Price and Stops/Type Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top, // Align tops for different text sizes
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Price - formatted and prominent
                Text(
                    text = formatPrice(offer.priceBreakdown?.total),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

            }

            Spacer(modifier = Modifier.height(12.dp))

            // Display each segment (leg) of the flight
            offer.segments?.forEachIndexed { index, segment ->
                SegmentView(segment = segment)
                // Add a visual separator between segments if it's a multi-leg flight
                if (index < (offer.segments.size - 1)) {
                    // Simple dashed line separator (using text)
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

// --- Composable for displaying a single flight segment ---

@Composable
fun SegmentView(segment: Segment) {
    Row( // Use Row for better alignment control of segment details
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Departure Info
        Column(modifier = Modifier.weight(1f)) { // Takes up available space
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
                text = segment.departureTime!!,//formatDisplayDateTime(segment.departureTime),
                style = MaterialTheme.typography.bodySmall, // Smaller font for time
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 20.dp) // Indent time under icon
            )
        }

        // --- Middle Section: Stops and Arrow ---
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 4.dp) // Add some horizontal padding
        ) {
            // Calculate stops for THIS segment based on its legs
            val stopsCount = (segment.legs?.size ?: 1) - 1 // 0 stops = 1 leg, 1 stop = 2 legs, etc.
            val stopsText = when {
                stopsCount <= 0 -> "Direct"
                stopsCount == 1 -> "1 Stop"
                else -> "$stopsCount Stops"
            }

            Text(
                text = stopsText,
                style = MaterialTheme.typography.labelMedium, // Use a smaller label style
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 2.dp) // Add space below stops text
            )

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "to",
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.outline
            )
        }

        // Arrival Info
        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) { // Align text to the end
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text( // Airport code first for alignment
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
                modifier = Modifier.padding(end = 20.dp), // Indent time under icon (from right)
                textAlign = TextAlign.End
            )
        }
    }
}


// --- Helper Functions ---

fun formatPrice(priceInfo: PriceInfo?): String {
    if (priceInfo == null || priceInfo.currencyCode == null || priceInfo.units == null) {
        return "N/A" // Not Available
    }
    // Combine units and nanos (assuming nanos are billionths of a unit)
    val totalValue = priceInfo.units + (priceInfo.nanos?.toDouble() ?: 0.0) / 1_000_000_000.0

    // Use Java's NumberFormat for proper currency formatting based on locale
    return try {
        val format = java.text.NumberFormat.getCurrencyInstance(Locale.getDefault())
        // Attempt to map currency code to Java Currency object
        format.currency = java.util.Currency.getInstance(priceInfo.currencyCode)
        format.format(totalValue)
    } catch (e: Exception) {
        // Fallback if currency code is invalid or locale data missing
        "${priceInfo.currencyCode} ${String.format(Locale.US, "%.2f", totalValue)}"
    }
}


// Parser for the ISO_OFFSET_DATE_TIME format from your API ("2025-05-01T13:25:00")
// IMPORTANT: Your example log shows "2025-05-01T13:25:00" which is MISSING the offset (+00:00 or Z)
// If the API *always* returns times without offset, we need to handle that.
// Assuming it *should* have an offset or is implicitly UTC. If not, parsing needs adjustment.
// Let's try ISO_OFFSET_DATE_TIME first, but add a fallback for LOCAL_DATE_TIME.