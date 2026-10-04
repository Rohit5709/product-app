package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CategoryEntity
import com.example.data.model.ProductEntity
import com.example.data.model.ShopEntity
import com.example.ui.MarketplaceViewModel
import com.example.ui.Screen
import com.example.ui.components.ProductCard
import com.example.ui.components.ShopCard
import com.example.ui.components.SuperAppTopBar
import com.example.ui.theme.CoralSecondary
import com.example.ui.theme.IndigoTertiary
import com.example.ui.theme.TealPrimary

data class QuickService(
    val title: String,
    val icon: ImageVector,
    val tintColor: Color,
    val action: () -> Unit
)

@Composable
fun HomeScreen(
    viewModel: MarketplaceViewModel,
    onNavigate: (Screen) -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val shops by viewModel.shops.collectAsStateWithLifecycle()
    val cartGroups by viewModel.cartGroups.collectAsStateWithLifecycle()
    val totalCartItems = cartGroups.sumOf { group -> group.items.sumOf { it.cartItem.quantity } }

    var selectedCategoryId by remember { mutableStateOf<String?>(null) }

    val filteredShops = if (selectedCategoryId == null) {
        shops
    } else {
        shops.filter { it.categoryId == selectedCategoryId }
    }

    val quickServices = listOf(
        QuickService("Shops", Icons.Default.Storefront, TealPrimary) {
            onNavigate(Screen.Search)
        },
        QuickService("Bike Ride", Icons.Default.DirectionsBike, Color(0xFF0284C7)) {
            onNavigate(Screen.RideBooking("Bike"))
        },
        QuickService("Tempo Truck", Icons.Default.LocalShipping, Color(0xFFEA580C)) {
            onNavigate(Screen.RideBooking("Tempo"))
        },
        QuickService("Services", Icons.Default.Build, Color(0xFF7C3AED)) {
            onNavigate(Screen.ServiceBooking("Plumbing & Electrical"))
        },
        QuickService("Earn ₹₹", Icons.Default.Campaign, CoralSecondary) {
            onNavigate(Screen.PromoteAndEarn)
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        SuperAppTopBar(
            currentCity = currentUser?.currentCity ?: "Jaipur",
            userRole = currentUser?.role ?: com.example.data.model.UserRole.CUSTOMER,
            cartItemCount = totalCartItems,
            onLocationClick = { viewModel.navigateTo(Screen.Profile) },
            onRoleBadgeClick = { viewModel.navigateTo(Screen.Profile) },
            onCartClick = { onNavigate(Screen.Cart()) }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Search Bar Button
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clickable { onNavigate(Screen.Search) }
                        .testTag("home_search_bar"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Search 'atta, milk, medicines, shops...'",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // Super App Multi-Services Quick Row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    quickServices.forEach { service ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { service.action() }
                                .padding(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .background(service.tintColor.copy(alpha = 0.12f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = service.icon,
                                    contentDescription = service.title,
                                    tint = service.tintColor,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = service.title,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Promote & Earn Story Cashback Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clickable { onNavigate(Screen.PromoteAndEarn) }
                        .testTag("promote_and_earn_banner"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(TealPrimary, CoralSecondary)
                                )
                            )
                            .padding(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    color = Color.White.copy(alpha = 0.25f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "PROMOTE & EARN CASHBACK",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Share Story & Earn ₹25–₹500",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "50 verified views = ₹25 cashback directly to in-app wallet!",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 12.sp
                                )
                            }
                            Button(
                                onClick = { onNavigate(Screen.PromoteAndEarn) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "Earn Now",
                                    color = TealPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            // Categories horizontal filter
            item {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Text(
                        text = "Categories",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedCategoryId == null,
                            onClick = { selectedCategoryId = null },
                            label = { Text("All Shops") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TealPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                        categories.forEach { category ->
                            FilterChip(
                                selected = selectedCategoryId == category.id,
                                onClick = {
                                    selectedCategoryId = if (selectedCategoryId == category.id) null else category.id
                                },
                                label = { Text(category.name) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = TealPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Nearby Verified Shops
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Nearby Verified Shops",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${filteredShops.size} active",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            items(filteredShops) { shop ->
                ShopCard(
                    shop = shop,
                    onClick = { onNavigate(Screen.ShopDetail(shop.id)) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }
    }
}
