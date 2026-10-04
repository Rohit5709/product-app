package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.WalletTransactionEntity
import com.example.data.model.WalletTransactionType
import com.example.ui.MarketplaceViewModel
import com.example.ui.Screen
import com.example.ui.theme.CoralSecondary
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TealPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    viewModel: MarketplaceViewModel,
    onNavigate: (Screen) -> Unit
) {
    val user by viewModel.currentUser.collectAsStateWithLifecycle()
    val transactions by viewModel.walletTransactions.collectAsStateWithLifecycle()

    var showTopupDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = { Text("In-App Wallet", fontWeight = FontWeight.Bold) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Balance Hero Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("wallet_balance_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(TealPrimary, Color(0xFF0F766E))
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBalanceWallet,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Total Balance",
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontSize = 14.sp
                                    )
                                }
                                Surface(
                                    color = Color.White.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "Instant 1-Click Pay",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "₹${(user?.walletBalance ?: 0.0).toInt()}",
                                color = Color.White,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Button(
                                    onClick = { showTopupDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.testTag("add_money_btn")
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Add Money", color = TealPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }

                                Button(
                                    onClick = { onNavigate(Screen.PromoteAndEarn) },
                                    colors = ButtonDefaults.buttonColors(containerColor = CoralSecondary),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.testTag("earn_cashback_btn")
                                ) {
                                    Icon(Icons.Default.Campaign, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Earn Cashback", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Transaction History Header
            item {
                Text(
                    text = "Transaction Ledger",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (transactions.isEmpty()) {
                item {
                    Text(
                        text = "No transactions recorded yet.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                items(transactions) { tx ->
                    WalletTransactionRow(tx = tx)
                }
            }
        }
    }

    if (showTopupDialog) {
        var topupAmountText by remember { mutableStateOf("200") }

        AlertDialog(
            onDismissRequest = { showTopupDialog = false },
            title = { Text("Add Money to Wallet", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Select quick amount or type amount in INR:")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("100", "200", "500").forEach { preset ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (topupAmountText == preset) TealPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .clickable { topupAmountText = preset }
                                    .padding(2.dp)
                            ) {
                                Text(
                                    text = "+₹$preset",
                                    color = if (topupAmountText == preset) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = topupAmountText,
                        onValueChange = { topupAmountText = it },
                        label = { Text("Amount (₹)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = topupAmountText.toDoubleOrNull() ?: 100.0
                        viewModel.addMoneyToWallet(amt)
                        showTopupDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text("Top-up Now")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showTopupDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun WalletTransactionRow(tx: WalletTransactionEntity) {
    val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(tx.timestamp))
    val isCredit = tx.type == WalletTransactionType.CREDIT || tx.type == WalletTransactionType.CASHBACK || tx.type == WalletTransactionType.REFUND
    val amountColor = if (isCredit) SuccessGreen else ErrorRed
    val sign = if (isCredit) "+" else "-"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(amountColor.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isCredit) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                    contentDescription = null,
                    tint = amountColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = tx.description, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Text(text = "$dateStr • ID: ${tx.id}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(
                text = "$sign₹${tx.amount.toInt()}",
                color = amountColor,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
    }
}
