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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MarketplaceViewModel
import com.example.ui.theme.CoralSecondary
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TealPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromoteAndEarnScreen(
    viewModel: MarketplaceViewModel,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    var viewCountText by remember { mutableStateOf("100") }
    var selectedPlatform by remember { mutableStateOf("Instagram Story") }
    var proofNote by remember { mutableStateOf("@rohit_story_proof_1") }
    var isSubmitted by remember { mutableStateOf(false) }
    var earnedAmount by remember { mutableStateOf(0.0) }

    val views = viewCountText.toIntOrNull() ?: 0
    // 50 verified views = ₹25 cashback
    val calculatedCashback = (views / 50) * 25.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = { Text("Promote & Earn Cashback", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack, modifier = Modifier.testTag("promote_back_btn")) {
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
            // Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(CoralSecondary, Color(0xFFC2410C))
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Campaign, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Official Story Promotion Campaign",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Get ₹25 Cashback for Every 50 Verified Views",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Post our approved promo creative to your Instagram Story, WhatsApp Status, or Facebook, and earn real cashback credited straight into your in-app wallet.",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Calculation tier guide
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "Cashback Rate Chart", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    listOf(
                        "50 verified views" to "₹25 Cashback",
                        "100 verified views" to "₹50 Cashback",
                        "200 verified views" to "₹100 Cashback",
                        "500 verified views" to "₹250 Cashback"
                    ).forEach { (viewTier, reward) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = viewTier, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = reward, fontWeight = FontWeight.Bold, color = SuccessGreen, fontSize = 13.sp)
                        }
                    }
                }
            }

            // Proof Submission Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = "Submit Story Proof & Claim", fontWeight = FontWeight.Bold, fontSize = 15.sp)

                    // Platform selector
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Instagram Story", "WhatsApp Status", "Facebook").forEach { plat ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (selectedPlatform == plat) TealPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .clickable { selectedPlatform = plat }
                                    .padding(2.dp)
                            ) {
                                Text(
                                    text = plat,
                                    color = if (selectedPlatform == plat) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = viewCountText,
                        onValueChange = { viewCountText = it },
                        label = { Text("Eligible Views Count") },
                        leadingIcon = { Icon(Icons.Default.Visibility, contentDescription = null, tint = TealPrimary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("views_count_input")
                    )

                    OutlinedTextField(
                        value = proofNote,
                        onValueChange = { proofNote = it },
                        label = { Text("Proof Handle / Screenshot Note") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("proof_note_input")
                    )

                    // Estimated Cashback Preview
                    Surface(
                        color = TealPrimary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Calculated Cashback:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text(text = "₹${calculatedCashback.toInt()}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TealPrimary)
                        }
                    }

                    Button(
                        onClick = {
                            if (calculatedCashback > 0) {
                                earnedAmount = calculatedCashback
                                viewModel.addMoneyToWallet(calculatedCashback)
                                isSubmitted = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CoralSecondary),
                        shape = RoundedCornerShape(10.dp),
                        enabled = calculatedCashback > 0,
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("claim_cashback_btn")
                    ) {
                        Text("Verify & Claim ₹${calculatedCashback.toInt()} to Wallet", fontWeight = FontWeight.Bold)
                    }

                    if (isSubmitted) {
                        Surface(
                            color = SuccessGreen.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Verified! ₹${earnedAmount.toInt()} has been successfully credited to your in-app wallet balance.",
                                    color = SuccessGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
