package com.example.relax.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.relax.models.endpoints.searchFlightLocation.Flight
import com.example.relax.viewmodels.HomeViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class) // Needed for DropdownMenu, Scaffold, etc.
@Composable
fun StartScreen(
    navController: NavController,
    homeViewModel: HomeViewModel
) {
    val appName by remember { mutableStateOf("Relax") }

    // State for the text currently typed in the fields
    var startQuery by remember { mutableStateOf("") }
    var destQuery by remember { mutableStateOf("") }

    // State for the confirmed selections (holds the full Flight object)
    var selectedStartFlight by remember { mutableStateOf<Flight?>(null) }
    var selectedDestinationFlight by remember { mutableStateOf<Flight?>(null) }

    // State for controlling suggestion dropdown visibility
    var isStartDropdownVisible by remember { mutableStateOf(false) }
    var isDestDropdownVisible by remember { mutableStateOf(false) }

    // Collect suggestions from ViewModel
    val startSuggestions by homeViewModel.startingLocation.collectAsState()
    val destinationSuggestions by homeViewModel.destination.collectAsState()

    // Collect loading state for the final flight search (optional pattern)
    val isSearchingFlights by homeViewModel.isSearchingFlights.collectAsState()

    // Helpers
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    var searchJob by remember { mutableStateOf<Job?>(null) } // Debounce job for API calls
    val snackbarHostState = remember { SnackbarHostState() } // For showing messages

    // --- Debounced Search Logic ---
    fun triggerSearch(query: String, point: String) {
        searchJob?.cancel() // Cancel previous job
        // Don't trigger API for very short queries, but still allow clearing selection
        if (query.length < 2) {
            if (point == "start") isStartDropdownVisible = false else isDestDropdownVisible = false
            // Clear suggestions in ViewModel if query becomes too short
            // homeViewModel.clearLocationSuggestions(point)
            return
        }
        searchJob = coroutineScope.launch {
            delay(700) // Adjust debounce delay (milliseconds)
            homeViewModel.getDestination(query, point)
            // Show dropdown once search is triggered (will populate when state updates)
            if (point == "start") isStartDropdownVisible = true else isDestDropdownVisible = true
        }
    }

    // Date Picker
    var selectedDepartureDateMillis by remember { mutableStateOf<Long?>(null) }
    val formattedSelectedDepartureDate = remember(selectedDepartureDateMillis){
        selectedDepartureDateMillis?.let { millis ->
            val calendar = Calendar.getInstance().apply { timeInMillis = millis}
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateFormat.format(calendar.time)
        } ?: ""
    }
    var selectedReturnDateMillis by remember { mutableStateOf<Long?>(null) }
    val formattedSelectedReturnDate = remember(selectedReturnDateMillis){
        selectedReturnDateMillis?.let { millis ->
            val calendar = Calendar.getInstance().apply { timeInMillis = millis}
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateFormat.format(calendar.time)
        } ?: ""
    }
    var showDepartureCalendar by remember { mutableStateOf(false) }
    var showReturnCalendar by remember { mutableStateOf(false) }

    // Adults
    var adults by remember {mutableIntStateOf(1)}


    // --- UI ---
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) } // Host for validation/error messages
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()) // Make content scrollable
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                // App Name
                RenderAppName(appName)
                Spacer(modifier = Modifier.height(32.dp))

                // --- Departure Input ---
                Text("From", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 4.dp))
                LocationInputWithDropdown(
                    query = startQuery,
                    onQueryChange = { newQuery ->
                        startQuery = newQuery
                        // Clear selection if user modifies the query after selecting
                        if (selectedStartFlight?.name != newQuery && selectedStartFlight?.id != newQuery) {
                            selectedStartFlight = null
                        }
                        triggerSearch(newQuery, "start")
                    },
                    selectedFlight = selectedStartFlight,
                    onFlightSelected = { flight ->
                        selectedStartFlight = flight
                        startQuery = flight.name ?: flight.code ?: "" // Display selected name
                        isStartDropdownVisible = false
                        focusManager.clearFocus()
                    },
                    suggestions = startSuggestions,
                    isDropdownVisible = isStartDropdownVisible,
                    onDismissDropdown = { isStartDropdownVisible = false },
                    placeholder = "Departure city or airport",
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isSearchingFlights
                )
                Spacer(modifier = Modifier.height(24.dp))

                // --- Swap Icon ---
                Icon(
                    imageVector = Icons.Default.SwapVert,
                    contentDescription = "Swap locations",
                    modifier = Modifier
                        .size(36.dp)
                        .align(Alignment.CenterHorizontally)
                        .clickable(enabled = !isSearchingFlights) {
                            val tempQuery = startQuery
                            val tempSelected = selectedStartFlight
                            startQuery = destQuery
                            selectedStartFlight = selectedDestinationFlight
                            destQuery = tempQuery
                            selectedDestinationFlight = tempSelected
                            homeViewModel.clearLocationSuggestions("start")
                            homeViewModel.clearLocationSuggestions("dest")
                            isStartDropdownVisible = false
                            isDestDropdownVisible = false
                        },
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(24.dp))

                // --- Arrival Input ---
                Text("To", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 4.dp))
                LocationInputWithDropdown(
                    query = destQuery,
                    onQueryChange = { newQuery ->
                        destQuery = newQuery
                        if (selectedDestinationFlight?.name != newQuery && selectedDestinationFlight?.id != newQuery) {
                            selectedDestinationFlight = null
                        }
                        triggerSearch(newQuery, "dest")
                    },
                    selectedFlight = selectedDestinationFlight,
                    onFlightSelected = { flight ->
                        selectedDestinationFlight = flight
                        destQuery = flight.name ?: flight.code ?: "" // Display selected name
                        isDestDropdownVisible = false
                        focusManager.clearFocus()
                    },
                    suggestions = destinationSuggestions,
                    isDropdownVisible = isDestDropdownVisible,
                    onDismissDropdown = { isDestDropdownVisible = false },
                    placeholder = "Arrival city or airport",
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isSearchingFlights
                )
                Spacer(modifier = Modifier.height(40.dp))

                Text("Dates", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 4.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp) // Spacing between date fields
                ){
                    // You'll need state for the selected dates
                    Box(modifier = Modifier
                        .weight(1f)
                        .clickable{
                            showDepartureCalendar = true
                        }
                    ){
                        OutlinedTextField(
                            value = formattedSelectedDepartureDate.ifBlank { "Select Date" },
                            onValueChange = {}, // Not editable directly
                            readOnly = true, // User clicks to open picker
                            label = { Text("Departure") },
                            leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Departure Date")},
                            enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = LocalContentColor.current.copy(),
                                disabledContainerColor = Color.Transparent, // Or MaterialTheme.colorScheme.surface
                                disabledBorderColor = MaterialTheme.colorScheme.outline, // Standard border
                                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        )
                    }
                    Box(modifier = Modifier
                        .weight(1f)
                        .clickable{
                            showReturnCalendar = true
                        }
                    ){
                        OutlinedTextField(
                            value = formattedSelectedReturnDate.ifBlank { "One way" },
                            onValueChange = {}, // Not editable directly
                            readOnly = true, // User clicks to open picker
                            label = { Text("Return") },
                            leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Return Date")},
                            enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = LocalContentColor.current.copy(),
                                disabledContainerColor = Color.Transparent, // Or MaterialTheme.colorScheme.surface
                                disabledBorderColor = MaterialTheme.colorScheme.outline, // Standard border
                                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))

                Row(
                    Modifier.fillMaxWidth(),
                ){
                    NumberPicker(count = adults, onCountChange = {newCount -> adults = newCount} )
                }
                Spacer(modifier = Modifier.height(40.dp))

                // --- Search Button ---
                Button(
                    onClick = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        val startId = selectedStartFlight?.id // Use ID from selected Flight
                        val destId = selectedDestinationFlight?.id // Use ID from selected Flight
                        val departDate = formattedSelectedDepartureDate
                        val returnDate = formattedSelectedReturnDate

                        if (startId != null && destId != null && departDate.isNotBlank()) {
                            // Call ViewModel to fetch flights
                            homeViewModel.getFlights(
                                fromId = startId,
                                toId = destId,
                                departDate = departDate,
                                returnDate = returnDate,
                                adults = adults
                                // Pass other params like returnDate, adults if needed
                            )
                            // Navigate to results screen (assuming non-suspend getFlights)
                            navController.navigate("results_screen")
                        } else {
                            // Show validation error message
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Please select departure, arrival, and date.",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    },
                    // Enable only when both locations are selected and not currently searching
                    enabled = selectedStartFlight != null && selectedDestinationFlight != null && !isSearchingFlights,
                    modifier = Modifier
                        .fillMaxWidth(0.9f) // Adjust width as desired
                        .height(50.dp) // Standard button height
                        .align(Alignment.CenterHorizontally)
                ) {
                    // Show progress indicator inside button if searching (optional alternate loading style)
                    // if (isSearchingFlights) { CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp) } else { Text("Search Flights", fontSize = 16.sp) }
                    Text("Search Flights", fontSize = 16.sp)
                }

                Spacer(Modifier.height(24.dp)) // Space at the bottom

            } // End Column

            // --- Loading Overlay (Optional pattern - shown over everything) ---
            if (isSearchingFlights) {
                Surface(
                    color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.4f), // Use theme scrim color
                    modifier = Modifier.fillMaxSize()
                ) {} // Semi-transparent background
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center) // Centered spinner
                )
            }
            if(showDepartureCalendar){
                DatePickerModal(
                    onDateSelected = { millis ->
                        selectedDepartureDateMillis = millis // Update the state with the selected date
                        showDepartureCalendar = false // Close dialog after selection
                    },
                    onDismiss = {
                        showDepartureCalendar = false // Close dialog if dismissed
                    }
                )
            }
            if(showReturnCalendar){
                DatePickerModal(
                    onDateSelected = { millis ->
                        selectedReturnDateMillis = millis // Update the state with the selected date
                        showReturnCalendar = false // Close dialog after selection
                    },
                    onDismiss = {
                        showReturnCalendar = false // Close dialog if dismissed
                    }
                )
            }
        } // End Box
    } // End Scaffold
}

// --- Reusable Composable for Location Input + Dropdown ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationInputWithDropdown(
    query: String,
    onQueryChange: (String) -> Unit,
    selectedFlight: Flight?,
    onFlightSelected: (Flight) -> Unit,
    suggestions: List<Flight>?,
    isDropdownVisible: Boolean,
    onDismissDropdown: () -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Box(modifier = modifier) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    // Hide dropdown if the field loses focus WITHOUT making a selection
                    // (Selection handler already clears focus)
                    // if (!focusState.isFocused && selectedFlight == null) { onDismissDropdown() }

                    // Alternative: Show dropdown on focus if query is valid? Needs careful handling.
                    // if (focusState.isFocused && query.length >= 2) { /* potentially trigger search or show existing */ }
                },
            placeholder = { Text(placeholder) },
            singleLine = true,
            enabled = enabled,
            trailingIcon = {
                when {
                    // Show checkmark if selected
                    selectedFlight != null -> Icon(
                        Icons.Filled.CheckCircle,
                        "Selected",
                        tint = MaterialTheme.colorScheme.primary // Use primary color for selected
                    )
                    // Show clear button only if NOT selected and query is not empty
                    query.isNotEmpty() -> IconButton(onClick = {
                        onQueryChange("") // Clear the query text
                        // If you also want to clear suggestions immediately:
                        // onDismissDropdown()
                        // homeViewModel.clearLocationSuggestions(...) // Needs ViewModel access or callback
                    }) {
                        Icon(Icons.Filled.Clear, "Clear text")
                    }
                    else -> null // No icon otherwise
                }
            },
            colors = OutlinedTextFieldDefaults.colors( // Subtle visual cue when selected
                focusedBorderColor = if (selectedFlight != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary, // Or a different color like Green
                unfocusedBorderColor = if (selectedFlight != null) MaterialTheme.colorScheme.primary.copy(alpha=0.7f) else MaterialTheme.colorScheme.outline
            )
        )

        // Suggestions Dropdown
        DropdownMenu(
            expanded = isDropdownVisible && !suggestions.isNullOrEmpty() && selectedFlight == null, // Show if flag is true, have suggestions, and nothing is selected yet
            onDismissRequest = onDismissDropdown, // Called when clicked outside or back button
            modifier = Modifier
                .fillMaxWidth() // Match text field width
                // Limit height to prevent overly long lists
                .heightIn(max = 300.dp)
        ) {
            suggestions?.forEach { flight ->
                DropdownMenuItem(
                    text = {
                        // Combine fields for a richer display (as discussed before)
                        val primaryText = flight.name ?: flight.code ?: "Unknown location"
                        val secondaryTextParts = mutableListOf<String>()
                        if (!flight.city.isNullOrBlank() && flight.city != flight.name) {
                            secondaryTextParts.add(flight.city)
                        }
                        if (!flight.countryName.isNullOrBlank()) {
                            secondaryTextParts.add(flight.countryName)
                        }
                        if (!flight.code.isNullOrBlank() && flight.code != flight.name) {
                            secondaryTextParts.add("(${flight.code})")
                        }
                        val secondaryText = secondaryTextParts.joinToString(" - ")

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val icon = when (flight.type?.uppercase()) {
                                "AIRPORT" -> Icons.Default.LocalAirport
                                "CITY" -> Icons.Default.LocationCity
                                "COUNTRY" -> Icons.Default.Public
                                else -> Icons.Default.LocationOn
                            }
                            Icon(
                                imageVector = icon,
                                contentDescription = flight.type,
                                modifier = Modifier
                                    .size(20.dp)
                                    .padding(end = 8.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Column {
                                Text(primaryText, fontWeight = FontWeight.Medium)
                                if (secondaryText.isNotEmpty()) {
                                    Text(
                                        text = secondaryText,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    },
                    onClick = {
                        onFlightSelected(flight) // This updates state AND clears focus
                        // onDismissDropdown() // Not needed here, focus clear hides it
                    }
                )
            }
            // Handle empty suggestions state after search
            if(isDropdownVisible && suggestions?.isEmpty() == true && query.length >= 2) {
                DropdownMenuItem(
                    text = { Text("No locations found", style = LocalTextStyle.current.copy(color = Color.Gray)) },
                    onClick = { onDismissDropdown() },
                    enabled = false // Make it non-clickable
                )
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Calendar.getInstance().timeInMillis
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateSelected(datePickerState.selectedDateMillis)
                },
                enabled = datePickerState.selectedDateMillis != null
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Composable
fun NumberPicker(
    label: String = "Adults",
    count: Int,
    onCountChange: (Int) -> Unit,
    minValue: Int = 1,
    maxValue: Int = 9
) {
    Column(modifier = Modifier) {

        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        // Row contained within a border for the frame effect
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min) // Ensures Row height fits content snugly
                .border(
                    BorderStroke(1.dp, MaterialTheme.colorScheme.outline), // Standard outline color
                    shape = MaterialTheme.shapes.extraSmall // Match OutlinedTextField corner radius
                )
                .padding(horizontal = 4.dp), // Padding inside the border
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Decrease Button
            IconButton(
                onClick = { if (count > minValue) onCountChange(count - 1) },
                enabled = count > minValue // Disable if at minimum value
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Decrease $label", // Accessibility
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Divider for visual separation
            HorizontalDivider(
                modifier = Modifier
                    .height(24.dp) // Adjust height as needed
                    .width(1.dp)
                    .padding(vertical = 8.dp), // Padding around divider
                color = MaterialTheme.colorScheme.outline
            )

            // Count Display - Takes up the central space
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.bodyLarge, // Make number prominent
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f) // Fill available horizontal space
                    .padding(vertical = 12.dp) // Vertical padding to ensure height matches IconButton touch area
            )

            // Divider for visual separation
            HorizontalDivider(
                modifier = Modifier
                    .height(24.dp) // Adjust height as needed
                    .width(1.dp)
                    .padding(vertical = 8.dp), // Padding around divider
                color = MaterialTheme.colorScheme.outline
            )

            // Increase Button
            IconButton(
                onClick = { if (count < maxValue) onCountChange(count + 1) },
                enabled = count < maxValue // Disable if at maximum value
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Increase $label", // Accessibility
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun RenderAppName(text: String){
    Text(
        text = text,
        fontSize = 34.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
    )
}
