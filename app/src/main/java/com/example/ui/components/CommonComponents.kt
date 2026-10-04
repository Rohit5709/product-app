package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.OrderStatus
import com.example.data.model.PaymentStatus
import com.example.data.model.ProductEntity
import com.example.data.model.ShopEntity
import com.example.data.model.UserRole
import com.example.ui.Screen
import com.example.ui.theme.CoralSecondary
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.WarningAmber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuperAppTopBar(
    currentCity: String = "Jaipur",
    userRole: UserRole = UserRole.CUSTOMER,
    cartItemCount: Int = 0,
    onLocationClick: () -> Unit = {},
    onRoleBadgeClick: () -> Unit = {},
    onCartClick: () -> Unit = {}
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        title = {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onLocationClick() }
                        .padding(vertical = 2.dp)
                        .testTag("location_picker_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = TealPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$currentCity, Rajasthan",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "Local Super Marketplace",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        actions = {
            // Role switcher tag
            Surface(
                color = CoralSecondary.copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clickable { onRoleBadgeClick() }
                    .testTag("role_badge_btn")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(CoralSecondary, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = userRole.name.replace("_", " "),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CoralSecondary
                    )
                }
            }

            // Cart icon with badge
            IconButton(
                onClick = onCartClick,
                modifier = Modifier.testTag("top_bar_cart_btn")
            ) {
                BadgedBox(
                    badge = {
                        if (cartItemCount > 0) {
                            Badge(
                                containerColor = CoralSecondary,
                                contentColor = Color.White
                            ) {
                                Text(text = "$cartItemCount")
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Cart",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    )
}

@Composable
fun SuperAppBottomNav(
    currentScreen: Screen,
    cartItemCount: Int = 0,
    onTabSelected: (Screen) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentScreen is Screen.Home,
            onClick = { onTabSelected(Screen.Home) },
            icon = { Icon(Icons.Outlined.Home, contentDescription = "Home") },
            label = { Text("Home") },
            modifier = Modifier.testTag("bottom_nav_home")
        )
        NavigationBarItem(
            selected = currentScreen is Screen.Search,
            onClick = { onTabSelected(Screen.Search) },
            icon = { Icon(Icons.Outlined.Search, contentDescription = "Search") },
            label = { Text("Search") },
            modifier = Modifier.testTag("bottom_nav_search")
        )
        NavigationBarItem(
            selected = currentScreen is Screen.Orders,
            onClick = { onTabSelected(Screen.Orders) },
            icon = { Icon(Icons.Outlined.ReceiptLong, contentDescription = "Orders") },
            label = { Text("Orders") },
            modifier = Modifier.testTag("bottom_nav_orders")
        )
        NavigationBarItem(
            selected = currentScreen is Screen.Wallet,
            onClick = { onTabSelected(Screen.Wallet) },
            icon = { Icon(Icons.Outlined.AccountBalanceWallet, contentDescription = "Wallet") },
            label = { Text("Wallet") },
            modifier = Modifier.testTag("bottom_nav_wallet")
        )
        NavigationBarItem(
            selected = currentScreen is Screen.Profile,
            onClick = { onTabSelected(Screen.Profile) },
            icon = { Icon(Icons.Outlined.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            modifier = Modifier.testTag("bottom_nav_profile")
        )
    }
}

@Composable
fun ShopCard(
    shop: ShopEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("shop_card_${shop.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                AsyncImage(
                    model = shop.coverUrl,
                    contentDescription = shop.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
                // Rating pill
                Surface(
                    color = Color.Black.copy(alpha = 0.75f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = WarningAmber,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${shop.rating}",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = shop.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    if (shop.isVerified) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Verified",
                            tint = TealPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${shop.categoryName} • ${shop.address}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "⏱ ${shop.estimatedDeliveryMins} mins • Min ₹${shop.minOrderAmount.toInt()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TealPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = shop.openingHours,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ProductCard(
    product: ProductEntity,
    onProductClick: () -> Unit,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onProductClick() }
            .testTag("product_card_${product.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(10.dp))
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
                if (product.discountPercent > 0) {
                    Surface(
                        color = CoralSecondary,
                        shape = RoundedCornerShape(bottomEnd = 8.dp),
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Text(
                            text = "${product.discountPercent}% OFF",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = product.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${product.weight} • ${product.unit}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "₹${product.sellingPrice.toInt()}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (product.originalPrice > product.sellingPrice) {
                        Text(
                            text = "₹${product.originalPrice.toInt()}",
                            style = MaterialTheme.typography.labelSmall.copy(textDecoration = TextDecoration.LineThrough),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Button(
                    onClick = onAddToCart,
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("add_to_cart_btn_${product.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("ADD", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun OrderStatusBadge(status: OrderStatus) {
    val (color, label) = when (status) {
        OrderStatus.PENDING -> Pair(WarningAmber, "PENDING")
        OrderStatus.SHOP_ACCEPTED -> Pair(TealPrimary, "SHOP ACCEPTED")
        OrderStatus.SHOP_REJECTED -> Pair(ErrorRed, "REJECTED")
        OrderStatus.PREPARING -> Pair(TealPrimary, "PREPARING")
        OrderStatus.READY_FOR_PICKUP -> Pair(TealPrimary, "READY FOR PICKUP")
        OrderStatus.ASSIGNED_TO_DELIVERY -> Pair(TealPrimary, "DRIVER ASSIGNED")
        OrderStatus.PICKED_UP -> Pair(TealPrimary, "PICKED UP")
        OrderStatus.OUT_FOR_DELIVERY -> Pair(CoralSecondary, "OUT FOR DELIVERY")
        OrderStatus.DELIVERED -> Pair(SuccessGreen, "DELIVERED")
        OrderStatus.CANCELLED -> Pair(ErrorRed, "CANCELLED")
        OrderStatus.REFUNDED -> Pair(SuccessGreen, "REFUNDED")
    }

    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = label,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun PriceRow(title: String, amount: String, isGreen: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = amount,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = if (isGreen) SuccessGreen else MaterialTheme.colorScheme.onSurface
        )
    }
}
