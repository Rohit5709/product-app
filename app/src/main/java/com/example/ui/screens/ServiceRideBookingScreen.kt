package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MarketplaceViewModel
import com.example.ui.theme.CoralSecondary
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TealPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceRideBookingScreen(
    viewModel: MarketplaceViewModel,
    title: String,
    mode: String, // "RIDE", "TEMPO", "SERVICE"
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    var pickup by remember { mutableStateOf("Tonk Road, Jaipur") }
    var destination by remember { mutableStateOf("Gaurav Tower, Malviya Nagar") }
    var problemDesc by remember { mutableStateOf("AC not cooling properly, needs gas refilling") }
    var vehicleType by remember { mutableStateOf(if (mode == "TEMPO") "Tata Ace (1 Ton)" else "Bike Taxi") }
    var isBooked by remember { mutableStateOf(false) }

    val estFare = when (mode) {
        "RIDE" -> 65.0
        "TEMPO" -> 350.0
        else -> 199.0 // Service inspection fee
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = { Text(title, fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack, modifier = Modifier.testTag("booking_back_btn")) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (mode == "SERVICE") "Service Request Details" else "Trip & Fare Estimation",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    if (mode == "SERVICE") {
                        OutlinedTextField(
                            value = pickup,
                            onValueChange = { pickup = it },
                            label = { Text("Service Location / Home Address") },
                            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = TealPrimary) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = problemDesc,
                            onValueChange = { problemDesc = it },
                            label = { Text("Describe the issue / requirement") },
                            leadingIcon = { Icon(Icons.Default.Build, contentDescription = null, tint = CoralSecondary) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        OutlinedTextField(
                            value = pickup,
                            onValueChange = { pickup = it },
                            label = { Text("Pickup Location") },
                            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = TealPrimary) },
                            modifier = Modifier.fillMaxWidth().testTag("pickup_input")
                        )
                        OutlinedTextField(
                            value = destination,
                            onValueChange = { destination = it },
                            label = { Text("Drop Destination") },
                            leadingIcon = { Icon(Icons.Default.Navigation, contentDescription = null, tint = CoralSecondary) },
                            modifier = Modifier.fillMaxWidth().testTag("destination_input")
                        )

                        if (mode == "TEMPO") {
                            Text(text = "Choose Vehicle:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("Tata Ace (1 Ton)", "Pickup (1.5 Ton)", "3-Wheeler Loading").forEach { v ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (vehicleType == v) TealPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.clickable { vehicleType = v }
                                    ) {
                                        Text(
                                            text = v,
                                            color = if (vehicleType == v) Color.White else MaterialTheme.colorScheme.onSurface,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (mode == "SERVICE") "Inspection / Visiting Fee" else "Estimated Fare (~4.2 km)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "₹${estFare.toInt()}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TealPrimary
                        )
                    }

                    Button(
                        onClick = { isBooked = true },
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("confirm_booking_btn")
                    ) {
                        Text(
                            text = if (mode == "SERVICE") "Book Service Provider" else "Confirm Booking",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (isBooked) {
                        Surface(
                            color = SuccessGreen.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (mode == "SERVICE") "Service Provider Assigned!" else "Driver Assigned & Arriving in 4 mins!",
                                        color = SuccessGreen,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (mode == "SERVICE")
                                        "Technician Suresh Verma (Rating 4.9 ★) will visit your address today."
                                    else
                                        "Driver Vikram Singh (RJ 14 BK 4920) is on the way to your pickup location.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
