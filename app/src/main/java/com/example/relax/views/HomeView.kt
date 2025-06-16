package com.example.relax.views

import com.example.relax.models.navigationRoutes.FlightsRoute
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
import com.example.relax.models.endpoints.Flight
import com.example.relax.viewmodels.HomeViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
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

    val startSuggestions by homeViewModel.startingLocation.collectAsState()
    val destinationSuggestions by homeViewModel.destination.collectAsState()

    val isSearchingFlights by homeViewModel.isSearchingFlights.collectAsState()

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    var searchJob by remember { mutableStateOf<Job?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    fun triggerSearch(query: String, point: String) {
        searchJob?.cancel()
        if (query.length < 2) {
            if (point == "start") isStartDropdownVisible = false else isDestDropdownVisible = false
            return
        }
        searchJob = coroutineScope.launch {
            delay(1200)
            homeViewModel.getDestination(query, point)
            if (point == "start") isStartDropdownVisible = true else isDestDropdownVisible = true
        }
    }

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

    var adults by remember {mutableIntStateOf(1)}


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                RenderAppName(appName)
                Spacer(modifier = Modifier.height(32.dp))

                Text("From", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 4.dp))
                LocationInputWithDropdown(
                    query = startQuery,
                    onQueryChange = { newQuery ->
                        startQuery = newQuery
                        if (selectedStartFlight?.name != newQuery && selectedStartFlight?.id != newQuery) {
                            selectedStartFlight = null
                        }
                        triggerSearch(newQuery, "start")
                    },
                    selectedFlight = selectedStartFlight,
                    onFlightSelected = { flight ->
                        selectedStartFlight = flight
                        startQuery = flight.name ?: flight.code ?: ""
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
                        destQuery = flight.name ?: flight.code ?: ""
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
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ){
                    Box(modifier = Modifier
                        .weight(1f)
                        .clickable{
                            showDepartureCalendar = true
                        }
                    ){
                        OutlinedTextField(
                            value = formattedSelectedDepartureDate.ifBlank { "Select Date" },
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Departure") },
                            leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Departure Date")},
                            enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = LocalContentColor.current.copy(),
                                disabledContainerColor = Color.Transparent,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
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
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Return") },
                            leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Return Date")},
                            enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = LocalContentColor.current.copy(),
                                disabledContainerColor = Color.Transparent,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
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

                Button(
                    onClick = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        val startId = selectedStartFlight?.id
                        val destId = selectedDestinationFlight?.id
                        val departDate = formattedSelectedDepartureDate
                        val returnDate = formattedSelectedReturnDate

                        if (startId != null && destId != null && departDate.isNotBlank()) {
                            homeViewModel.getFlights(
                                fromId = startId,
                                toId = destId,
                                departDate = departDate,
                                returnDate = returnDate,
                                adults = adults
                            )

                            navController.navigate(
                                FlightsRoute(
                                    destinationName = selectedDestinationFlight?.code,
                                    departDate = departDate,
                                    returnDate = returnDate,
                                    adults = adults,
                                    null
                                )
                            )
                        } else {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Please select departure, arrival, and date.",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    },
                    enabled = selectedStartFlight != null && selectedDestinationFlight != null && !isSearchingFlights,
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(50.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text("Search Flights", fontSize = 16.sp)
                }

                Spacer(Modifier.height(24.dp))

            }

            if (isSearchingFlights) {
                Surface(
                    color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxSize()
                ) {}
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            if(showDepartureCalendar){
                DatePickerModal(
                    onDateSelected = { millis ->
                        selectedDepartureDateMillis = millis
                        showDepartureCalendar = false
                    },
                    onDismiss = {
                        showDepartureCalendar = false
                    }
                )
            }
            if(showReturnCalendar){
                DatePickerModal(
                    onDateSelected = { millis ->
                        selectedReturnDateMillis = millis
                        showReturnCalendar = false
                    },
                    onDismiss = {
                        showReturnCalendar = false
                    }
                )
            }
        }
    }
}

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
                },
            placeholder = { Text(placeholder) },
            singleLine = true,
            enabled = enabled,
            trailingIcon = {
                when {
                    selectedFlight != null -> Icon(
                        Icons.Filled.CheckCircle,
                        "Selected",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    query.isNotEmpty() -> IconButton(onClick = {
                        onQueryChange("")
                    }) {
                        Icon(Icons.Filled.Clear, "Clear text")
                    }
                    else -> null
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (selectedFlight != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = if (selectedFlight != null) MaterialTheme.colorScheme.primary.copy(alpha=0.7f) else MaterialTheme.colorScheme.outline
            )
        )

        DropdownMenu(
            expanded = isDropdownVisible && !suggestions.isNullOrEmpty() && selectedFlight == null,
            onDismissRequest = onDismissDropdown,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 300.dp)
        ) {
            suggestions?.forEach { flight ->
                DropdownMenuItem(
                    text = {
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
                        onFlightSelected(flight)
                    }
                )
            }
            if(isDropdownVisible && suggestions?.isEmpty() == true && query.length >= 2) {
                DropdownMenuItem(
                    text = { Text("No locations found", style = LocalTextStyle.current.copy(color = Color.Gray)) },
                    onClick = { onDismissDropdown() },
                    enabled = false
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .border(
                    BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    shape = MaterialTheme.shapes.extraSmall
                )
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { if (count > minValue) onCountChange(count - 1) },
                enabled = count > minValue
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Decrease $label",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            HorizontalDivider(
                modifier = Modifier
                    .height(24.dp)
                    .width(1.dp)
                    .padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outline
            )

            Text(
                text = count.toString(),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 12.dp)
            )

            HorizontalDivider(
                modifier = Modifier
                    .height(24.dp)
                    .width(1.dp)
                    .padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outline
            )

            IconButton(
                onClick = { if (count < maxValue) onCountChange(count + 1) },
                enabled = count < maxValue
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Increase $label",
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