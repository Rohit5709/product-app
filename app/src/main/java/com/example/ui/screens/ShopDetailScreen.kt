package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.ProductEntity
import com.example.ui.MarketplaceViewModel
import com.example.ui.Screen
import com.example.ui.theme.CoralSecondary
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.WarningAmber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopDetailScreen(
    viewModel: MarketplaceViewModel,
    shopId: String,
    onNavigate: (Screen) -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val shop by viewModel.selectedShop.collectAsStateWithLifecycle()
    val products by viewModel.selectedShopProducts.collectAsStateWithLifecycle()
    val cartGroups by viewModel.cartGroups.collectAsStateWithLifecycle()

    var queryInsideShop by remember { mutableStateOf("") }

    val shopCartGroup = cartGroups.firstOrNull { it.shop.id == shopId }
    val cartCount = shopCartGroup?.items?.sumOf { it.cartItem.quantity } ?: 0
    val cartSubtotal = shopCartGroup?.subtotal ?: 0.0

    val filteredProducts = if (queryInsideShop.isBlank()) {
        products
    } else {
        products.filter {
            it.name.contains(queryInsideShop, ignoreCase = true) ||
            it.brand.contains(queryInsideShop, ignoreCase = true) ||
            it.description.contains(queryInsideShop, ignoreCase = true)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = if (cartCount > 0) 90.dp else 24.dp)
        ) {
            // Shop Hero Cover with Overlay
            item {
                Box(modifier = Modifier.fillMaxWidth().height(220.dp)) {
                    AsyncImage(
                        model = shop?.coverUrl,
                        contentDescription = shop?.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.5f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.8f)
                                    )
                                )
                            )
                    )
                    // Back button
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.TopStart)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            .testTag("shop_detail_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    // Shop Name & Category at bottom of header
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = shop?.name ?: "Shop Details",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            if (shop?.isVerified == true) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Verified Shop",
                                    tint = TealPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Text(
                            text = "${shop?.categoryName} • ${shop?.address}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            // Shop Info Badge Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = WarningAmber,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "${shop?.rating ?: 4.8}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            Text(
                                text = "${shop?.reviewCount ?: 100}+ reviews",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Box(modifier = Modifier.width(1.dp).height(30.dp).background(MaterialTheme.colorScheme.outlineVariant))

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${shop?.estimatedDeliveryMins ?: 30} mins",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TealPrimary
                            )
                            Text(
                                text = "Delivery Time",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Box(modifier = Modifier.width(1.dp).height(30.dp).background(MaterialTheme.colorScheme.outlineVariant))

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "₹${shop?.minOrderAmount?.toInt() ?: 100}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Min Order",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // In-shop Search Box
            item {
                OutlinedTextField(
                    value = queryInsideShop,
                    onValueChange = { queryInsideShop = it },
                    placeholder = { Text("Search inside ${shop?.name ?: "shop"}...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = TealPrimary)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = TealPrimary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .testTag("search_inside_shop_input")
                )
            }

            item {
                Text(
                    text = "Products (${filteredProducts.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // Product Items List
            items(filteredProducts) { product ->
                val cartItem = shopCartGroup?.items?.firstOrNull { it.product.id == product.id }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clickable { onNavigate(Screen.ProductDetail(product.id)) }
                        .testTag("shop_product_item_${product.id}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
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
                                    shape = RoundedCornerShape(bottomEnd = 6.dp),
                                    modifier = Modifier.align(Alignment.TopStart)
                                ) {
                                    Text(
                                        text = "${product.discountPercent}%",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = product.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${product.weight} • ${product.brand}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "₹${product.sellingPrice.toInt()}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                if (product.originalPrice > product.sellingPrice) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "₹${product.originalPrice.toInt()}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            textDecoration = TextDecoration.LineThrough
                                        ),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        // Add / Quantity Controls
                        if (cartItem == null) {
                            Button(
                                onClick = { viewModel.addToCart(product, 1) },
                                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .height(36.dp)
                                    .testTag("shop_add_btn_${product.id}")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("ADD", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = TealPrimary.copy(alpha = 0.12f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, TealPrimary)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    IconButton(
                                        onClick = {
                                            viewModel.updateCartQuantity(
                                                cartItem.cartItem.id,
                                                cartItem.cartItem.quantity - 1
                                            )
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Remove,
                                            contentDescription = "Minus",
                                            tint = TealPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Text(
                                        text = "${cartItem.cartItem.quantity}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = TealPrimary,
                                        modifier = Modifier.padding(horizontal = 6.dp)
                                    )
                                    IconButton(
                                        onClick = {
                                            viewModel.updateCartQuantity(
                                                cartItem.cartItem.id,
                                                cartItem.cartItem.quantity + 1
                                            )
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Plus",
                                            tint = TealPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Floating Cart Summary Bar
        if (cartCount > 0) {
            Surface(
                color = TealPrimary,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .clickable { onNavigate(Screen.Cart(shopId)) }
                    .testTag("floating_cart_bar")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Cart",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "$cartCount item${if (cartCount > 1) "s" else ""} added",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Subtotal: ₹${cartSubtotal.toInt()}",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 12.sp
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "View Cart",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Proceed",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
