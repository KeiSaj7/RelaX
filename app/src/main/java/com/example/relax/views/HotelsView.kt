package com.example.relax.views

import android.content.Intent
import android.util.Log
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
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.example.relax.models.endpoints.GrossPrice
import com.example.relax.models.endpoints.Hotel
import com.example.relax.models.endpoints.HotelDetailsResponse
import com.example.relax.viewmodels.HotelsViewModel
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun HotelsView (navController: NavController, hotelViewModel: HotelsViewModel)
{
    val hotelsResponse by hotelViewModel.hotels.collectAsState()

    var showHotelBookingDialog by remember { mutableStateOf(false) }
    var selectedHotelForBooking by remember { mutableStateOf<Hotel?>(null) }

    // This state is for the INDIVIDUAL hotel's booking URL details from the ViewModel/Repository
    val hotelUrlDetailsFromVM by hotelViewModel.urlResponse.collectAsState()

    // Local state to manage what the dialog shows while fetching URL for *this specific interaction*
    var isDialogFetchingUrl by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Effect to clear the repository's URL when the dialog is dismissed,
    // so the next click doesn't show stale data while loading.
    LaunchedEffect(showHotelBookingDialog) {
        if (!showHotelBookingDialog) {
            hotelViewModel.clearUrl() // Call VM method to clear repo's URL
            isDialogFetchingUrl = false // Reset local loading flag
        }
    }

    if (showHotelBookingDialog && selectedHotelForBooking != null) {
        val hotel = selectedHotelForBooking!!

        HotelBookingConfirmationDialog(
            hotel = hotel,
            isDialogCurrentlyFetchingUrl = isDialogFetchingUrl, // Pass local loading state
            actualUrlDetailsResponse = hotelUrlDetailsFromVM,   // Pass actual data from repo
            onConfirmOpenUrl = { bookingUrl ->
                showHotelBookingDialog = false // This will trigger LaunchedEffect to clear URL
                // selectedHotelForBooking = null // Not strictly needed, dialog closes

                val intent = Intent(Intent.ACTION_VIEW, bookingUrl.toUri())
                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Log.e("RelaxLOG", "Could not open URL: $bookingUrl. Error: ${e.message}")
                }
            },
            onDismiss = {
                showHotelBookingDialog = false // This will trigger LaunchedEffect to clear URL
                // selectedHotelForBooking = null // Not strictly needed
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
                        hotelViewModel = hotelViewModel,
                        hotels = hotels,
                        onHotelClick = { clickedHotel ->
                            Log.d("RelaxLOG", "Hotel card clicked: ID ${clickedHotel.hotelId}")
                            if (clickedHotel.hotelId == null) {
                                Log.e("RelaxLOG", "Clicked hotel has a null ID. Cannot fetch details.")
                                // Optionally show a toast/snackbar to the user here
                                return@HotelsList
                            }

                            // Clear previous URL first to avoid showing stale data in dialog while loading
                            hotelViewModel.clearUrl()

                            selectedHotelForBooking = clickedHotel
                            showHotelBookingDialog = true
                            isDialogFetchingUrl = true // Start local loading indicator for dialog

                            coroutineScope.launch {
                                try {
                                    Log.d("RelaxLOG", "Calling VM.getHotelDetails for ID: ${clickedHotel.hotelId}")
                                    // Call the ViewModel's suspend function.
                                    // This will update repository.url, which hotelUrlDetailsFromVM observes.
                                    hotelViewModel.getHotelDetails(hotelId = clickedHotel.hotelId.toString())
                                } catch (e: Exception) {
                                    Log.e("RelaxLOG", "Error launching VM.getHotelDetails: ${e.message}")
                                    // The ViewModel's getHotelDetails should ideally ensure repository.url
                                    // is set to an error state (e.g., HotelDetailsResponse(status=false,...))
                                    // or null, which the dialog will then pick up.
                                } finally {
                                    isDialogFetchingUrl = false // Stop local loading state for the dialog
                                }
                            }
                        }
                    )
                }
            }
            }
        }
}

@Composable
fun HotelBookingConfirmationDialog(
    hotel: Hotel,
    isDialogCurrentlyFetchingUrl: Boolean,     // Local loading state from HotelsView
    actualUrlDetailsResponse: HotelDetailsResponse?, // Actual data from viewModel.urlResponse.value
    onConfirmOpenUrl: (url: String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = "Redirect") },
        title = { Text(text = "Book: ${hotel.property.hotelName ?: "Selected Hotel"}") },
        text = {
            if (isDialogCurrentlyFetchingUrl) {
                // If HotelsView initiated a fetch for this dialog instance
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Fetching booking link...")
                }
            } else if (actualUrlDetailsResponse == null) {
                // Fetch completed (isDialogCurrentlyFetchingUrl is false), but repo still has null
                // This means the VM's getHotelDetails finished, and repo._url is still null (e.g. error in repo, or cleared)
                Text("Booking details are not available. Please try again or check connection.")
            } else if (actualUrlDetailsResponse.status == true && !actualUrlDetailsResponse.data?.url.isNullOrBlank()) {
                // Success and URL is present in the repo's state
                Text("You will be redirected to an external site to complete your booking. Continue?")
            } else {
                // API call for URL was made by VM, repo updated _url, but it reported an error or no URL
                Text("Could not get booking link: ${actualUrlDetailsResponse.message ?: "Details unavailable."}\nPlease try again later.")
            }
        },
        confirmButton = {
            val bookingUrl = actualUrlDetailsResponse?.data?.url
            // Enable confirm button only if NOT fetching LOCALLY AND we have a valid URL from a successful API call in repo
            if (!isDialogCurrentlyFetchingUrl && actualUrlDetailsResponse?.status == true && !bookingUrl.isNullOrBlank()) {
                TextButton(onClick = { onConfirmOpenUrl(bookingUrl) }) {
                    Text("Yes, Continue")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun HotelsList(
    navController: NavController,
    hotelViewModel: HotelsViewModel,
    hotels: List<Hotel>,
    onHotelClick: (Hotel) -> Unit = {} // Callback for item clicks if needed
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(onClick = {
                    hotelViewModel.clearUrl() // Clear URL before navigating away
                    hotelViewModel.navigateToHome(navController)
                }) { Text("Home") }
                Button(onClick = {
                    hotelViewModel.clearUrl() // Clear URL before navigating away
                    hotelViewModel.navigateToFlights(navController)
                }) { Text("Flights") }
            }
        }
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