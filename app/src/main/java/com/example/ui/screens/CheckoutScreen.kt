package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.PaymentMethod
import com.example.data.model.UserAddressEntity
import com.example.ui.components.PriceRow
import com.example.ui.MarketplaceViewModel
import com.example.ui.Screen
import com.example.ui.theme.CoralSecondary
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TealPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: MarketplaceViewModel,
    shopId: String,
    onNavigate: (Screen) -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val user by viewModel.currentUser.collectAsStateWithLifecycle()
    val addresses by viewModel.userAddresses.collectAsStateWithLifecycle()
    val cartGroups by viewModel.cartGroups.collectAsStateWithLifecycle()

    val shopCart = cartGroups.firstOrNull { it.shop.id == shopId }

    var selectedAddressId by remember(addresses) {
        mutableStateOf(addresses.firstOrNull { it.isDefault }?.id ?: addresses.firstOrNull()?.id ?: "")
    }
    var selectedPaymentMethod by remember { mutableStateOf(PaymentMethod.WALLET) }
    var specialInstructions by remember { mutableStateOf("") }
    var isPlacingOrder by remember { mutableStateOf(false) }
    var showAddAddressDialog by remember { mutableStateOf(false) }

    val selectedAddress = addresses.firstOrNull { it.id == selectedAddressId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = { Text("Checkout", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack, modifier = Modifier.testTag("checkout_back_btn")) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
        )

        if (shopCart == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No items found for this shop in cart", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Shop details header
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Ordering from: ${shopCart.shop.name}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${shopCart.items.size} item(s) • Est. Delivery: ${shopCart.shop.estimatedDeliveryMins} mins",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Delivery Address Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Delivery Address",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "+ Add New",
                                color = TealPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier
                                    .clickable { showAddAddressDialog = true }
                                    .testTag("add_new_address_btn")
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (addresses.isEmpty()) {
                            Text(
                                text = "No address saved. Please add one.",
                                color = ErrorRed,
                                style = MaterialTheme.typography.bodySmall
                            )
                        } else {
                            addresses.forEach { addr ->
                                val isSelected = addr.id == selectedAddressId
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable { selectedAddressId = addr.id }
                                        .testTag("address_item_${addr.id}"),
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) TealPrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, TealPrimary) else null
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = if (addr.label == "Home") Icons.Default.Home else Icons.Default.Work,
                                            contentDescription = null,
                                            tint = if (isSelected) TealPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(text = addr.label, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                if (addr.isDefault) {
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Surface(
                                                        color = TealPrimary.copy(alpha = 0.15f),
                                                        shape = RoundedCornerShape(4.dp)
                                                    ) {
                                                        Text("DEFAULT", color = TealPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                                    }
                                                }
                                            }
                                            Text(
                                                text = "${addr.fullAddress}, ${addr.landmark}, ${addr.city} - ${addr.pinCode}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        if (isSelected) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = "Selected", tint = TealPrimary, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Delivery instructions
                OutlinedTextField(
                    value = specialInstructions,
                    onValueChange = { specialInstructions = it },
                    label = { Text("Special instructions for delivery / shop") },
                    placeholder = { Text("E.g. Ring the doorbell, leave at door") },
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("delivery_instructions_input")
                )

                // Payment Method Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Payment Method",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // In-App Wallet Option
                        val walletBal = user?.walletBalance ?: 0.0
                        val hasEnoughWallet = walletBal >= shopCart.finalTotal

                        PaymentOptionTile(
                            title = "In-App Wallet",
                            subtitle = "Available Balance: ₹${walletBal.toInt()}${if (!hasEnoughWallet) " (Insufficient)" else " • 1-Click Pay"}",
                            icon = Icons.Default.AccountBalanceWallet,
                            isSelected = selectedPaymentMethod == PaymentMethod.WALLET,
                            enabled = hasEnoughWallet,
                            onClick = { selectedPaymentMethod = PaymentMethod.WALLET },
                            testTag = "payment_method_wallet"
                        )

                        // UPI Option
                        PaymentOptionTile(
                            title = "UPI (Google Pay / PhonePe / Paytm)",
                            subtitle = "Instant, secure bank transfer",
                            icon = Icons.Default.QrCode,
                            isSelected = selectedPaymentMethod == PaymentMethod.UPI,
                            onClick = { selectedPaymentMethod = PaymentMethod.UPI },
                            testTag = "payment_method_upi"
                        )

                        // Cash on Delivery
                        PaymentOptionTile(
                            title = "Cash on Delivery (COD)",
                            subtitle = "Pay cash or scan QR when delivered",
                            icon = Icons.Default.CurrencyRupee,
                            isSelected = selectedPaymentMethod == PaymentMethod.CASH_ON_DELIVERY,
                            onClick = { selectedPaymentMethod = PaymentMethod.CASH_ON_DELIVERY },
                            testTag = "payment_method_cod"
                        )

                        // Card
                        PaymentOptionTile(
                            title = "Credit / Debit Card",
                            subtitle = "Visa, Mastercard, RuPay",
                            icon = Icons.Default.CreditCard,
                            isSelected = selectedPaymentMethod == PaymentMethod.ONLINE_CARD,
                            onClick = { selectedPaymentMethod = PaymentMethod.ONLINE_CARD },
                            testTag = "payment_method_card"
                        )
                    }
                }

                // Final Order Summary Breakdown
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Price Details",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        PriceRow(title = "Item Subtotal", amount = "₹${shopCart.subtotal.toInt()}")
                        PriceRow(title = "Delivery Fee", amount = "₹${shopCart.deliveryFee.toInt()}")
                        PriceRow(title = "Platform Fee", amount = "₹${shopCart.platformFee.toInt()}")
                        if (shopCart.discount > 0) {
                            PriceRow(title = "Discount", amount = "-₹${shopCart.discount.toInt()}", isGreen = true)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Total Payable Amount", fontWeight = FontWeight.Bold)
                            Text(
                                text = "₹${shopCart.finalTotal.toInt()}",
                                fontWeight = FontWeight.Bold,
                                color = TealPrimary,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }

            // Bottom Place Order Bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Total to Pay",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "₹${shopCart.finalTotal.toInt()}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TealPrimary
                        )
                    }

                    Button(
                        onClick = {
                            val addr = selectedAddress ?: return@Button
                            isPlacingOrder = true
                            viewModel.placeOrder(
                                shopId = shopId,
                                deliveryAddress = "${addr.label}: ${addr.fullAddress}, ${addr.landmark}, ${addr.pinCode}",
                                deliveryCity = addr.city,
                                paymentMethod = selectedPaymentMethod,
                                specialInstructions = specialInstructions
                            ) { createdOrderId ->
                                isPlacingOrder = false
                                onNavigate(Screen.OrderDetail(createdOrderId))
                            }
                        },
                        enabled = selectedAddress != null && !isPlacingOrder,
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1.5f)
                            .height(50.dp)
                            .testTag("pay_and_place_order_btn")
                    ) {
                        if (isPlacingOrder) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Text(
                                text = "Pay & Place Order",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }

    // Add New Address Dialog
    if (showAddAddressDialog) {
        var label by remember { mutableStateOf("Home") }
        var fullAddress by remember { mutableStateOf("") }
        var landmark by remember { mutableStateOf("") }
        var pinCode by remember { mutableStateOf("302015") }

        AlertDialog(
            onDismissRequest = { showAddAddressDialog = false },
            title = { Text("Add Delivery Address", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Home", "Work", "Other").forEach { l ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (label == l) TealPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .clickable { label = l }
                                    .padding(2.dp)
                            ) {
                                Text(
                                    text = l,
                                    color = if (label == l) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = fullAddress,
                        onValueChange = { fullAddress = it },
                        label = { Text("House / Flat / Street") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = landmark,
                        onValueChange = { landmark = it },
                        label = { Text("Landmark (Optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = pinCode,
                        onValueChange = { pinCode = it },
                        label = { Text("Pincode") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (fullAddress.isNotBlank()) {
                            viewModel.addNewAddress(label, fullAddress, landmark, pinCode)
                            showAddAddressDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text("Save Address")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddAddressDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun PaymentOptionTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(enabled = enabled) { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) TealPrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, TealPrimary) else null
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (enabled) (if (isSelected) TealPrimary else MaterialTheme.colorScheme.onSurface) else MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outlineVariant
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.outlineVariant
                )
            }
            RadioButton(
                selected = isSelected,
                onClick = if (enabled) onClick else null,
                enabled = enabled,
                colors = RadioButtonDefaults.colors(selectedColor = TealPrimary)
            )
        }
    }
}
