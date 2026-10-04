package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.OrderStatus
import com.example.ui.MarketplaceViewModel
import com.example.ui.components.OrderStatusBadge
import com.example.ui.components.PriceRow
import com.example.ui.theme.CoralSecondary
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TealPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    viewModel: MarketplaceViewModel,
    orderId: String,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val order by viewModel.selectedOrder.collectAsStateWithLifecycle()
    val items by viewModel.selectedOrderItems.collectAsStateWithLifecycle()

    val currentOrder = order

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Track Order ${currentOrder?.orderNumber ?: ""}",
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack, modifier = Modifier.testTag("order_detail_back_btn")) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
        )

        if (currentOrder == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Order details not found", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(currentOrder.createdAt))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Tracking Status Stepper Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Live Order Tracking",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            OrderStatusBadge(status = currentOrder.orderStatus)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Visual Stepper
                        OrderProgressTracker(status = currentOrder.orderStatus)

                        Spacer(modifier = Modifier.height(16.dp))

                        // Simulation / Testing Controls
                        if (currentOrder.orderStatus != OrderStatus.DELIVERED && currentOrder.orderStatus != OrderStatus.CANCELLED) {
                            val nextStatus = when (currentOrder.orderStatus) {
                                OrderStatus.PENDING -> OrderStatus.SHOP_ACCEPTED
                                OrderStatus.SHOP_ACCEPTED -> OrderStatus.PREPARING
                                OrderStatus.PREPARING -> OrderStatus.READY_FOR_PICKUP
                                OrderStatus.READY_FOR_PICKUP -> OrderStatus.OUT_FOR_DELIVERY
                                OrderStatus.ASSIGNED_TO_DELIVERY, OrderStatus.PICKED_UP, OrderStatus.OUT_FOR_DELIVERY -> OrderStatus.DELIVERED
                                else -> null
                            }

                            if (nextStatus != null) {
                                Button(
                                    onClick = { viewModel.advanceOrderStatus(currentOrder.id, nextStatus) },
                                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("simulate_advance_status_btn")
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Simulate Status: ${nextStatus.name.replace("_", " ")}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Shop & Delivery Details
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Storefront, contentDescription = null, tint = TealPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(text = currentOrder.shopName, fontWeight = FontWeight.Bold)
                                Text(text = currentOrder.shopAddress, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = CoralSecondary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(text = "Delivery Location", fontWeight = FontWeight.Bold)
                                Text(text = currentOrder.deliveryAddress, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }

                // Ordered Items
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Items Ordered (${items.size})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        items.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                ) {
                                    AsyncImage(
                                        model = item.imageUrl,
                                        contentDescription = item.productName,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.matchParentSize()
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = item.productName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                    Text(text = "${item.quantity} x ₹${item.unitPrice.toInt()} (${item.productUnit})", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text(text = "₹${item.totalPrice.toInt()}", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Financial Breakdown & Settlement Info
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Payment & Settlement",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        PriceRow(title = "Item Subtotal", amount = "₹${currentOrder.totalItemAmount.toInt()}")
                        PriceRow(title = "Delivery Fee", amount = "₹${currentOrder.deliveryFee.toInt()}")
                        PriceRow(title = "Platform Fee", amount = "₹${currentOrder.platformFee.toInt()}")
                        if (currentOrder.discountAmount > 0) {
                            PriceRow(title = "Discount", amount = "-₹${currentOrder.discountAmount.toInt()}", isGreen = true)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        PriceRow(title = "Total Paid (${currentOrder.paymentMethod})", amount = "₹${currentOrder.finalPayableAmount.toInt()}")

                        Spacer(modifier = Modifier.height(8.dp))

                        // Transparent marketplace ledger note
                        Surface(
                            color = TealPrimary.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = "Platform Accounting Ledger",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TealPrimary
                                )
                                Text(
                                    text = "Platform Commission: ₹${currentOrder.commissionAmount.toInt()} • Shop Settlement: ₹${currentOrder.shopSettlementAmount.toInt()}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Cancel Order button (if still pending/accepted)
                if (currentOrder.orderStatus != OrderStatus.DELIVERED && currentOrder.orderStatus != OrderStatus.CANCELLED) {
                    OutlinedButton(
                        onClick = { viewModel.cancelOrder(currentOrder.id) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("cancel_order_btn")
                    ) {
                        Text("Cancel Order & Refund to Wallet", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun OrderProgressTracker(status: OrderStatus) {
    val steps = listOf(
        "Order Placed",
        "Shop Confirmed",
        "Preparing",
        "Out for Delivery",
        "Delivered"
    )

    val currentStepIndex = when (status) {
        OrderStatus.PENDING -> 0
        OrderStatus.SHOP_ACCEPTED -> 1
        OrderStatus.PREPARING -> 2
        OrderStatus.READY_FOR_PICKUP, OrderStatus.ASSIGNED_TO_DELIVERY, OrderStatus.PICKED_UP, OrderStatus.OUT_FOR_DELIVERY -> 3
        OrderStatus.DELIVERED -> 4
        OrderStatus.CANCELLED, OrderStatus.SHOP_REJECTED, OrderStatus.REFUNDED -> -1
    }

    if (currentStepIndex == -1) {
        Surface(
            color = ErrorRed.copy(alpha = 0.12f),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Order is ${status.name.replace("_", " ")}",
                color = ErrorRed,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier.padding(12.dp)
            )
        }
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        steps.forEachIndexed { index, title ->
            val isDone = index <= currentStepIndex
            val isCurrent = index == currentStepIndex

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            color = if (isDone) TealPrimary else MaterialTheme.colorScheme.outlineVariant,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isDone) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    } else {
                        Text(
                            text = "${index + 1}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                    color = if (isDone) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
