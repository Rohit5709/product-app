package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MarketplaceViewModel
import com.example.ui.Screen
import com.example.ui.components.SuperAppBottomNav
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CheckoutScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OrderDetailScreen
import com.example.ui.screens.OrdersScreen
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.PromoteAndEarnScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.ServiceRideBookingScreen
import com.example.ui.screens.ShopDetailScreen
import com.example.ui.screens.WalletScreen
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {

    private val viewModel: MarketplaceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: MarketplaceViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val cartGroups by viewModel.cartGroups.collectAsStateWithLifecycle()
    val totalCartItems = cartGroups.sumOf { group -> group.items.sumOf { it.cartItem.quantity } }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    val isBottomNavVisible = currentScreen is Screen.Home ||
            currentScreen is Screen.Search ||
            currentScreen is Screen.Orders ||
            currentScreen is Screen.Wallet ||
            currentScreen is Screen.Profile

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            if (isBottomNavVisible) {
                SuperAppBottomNav(
                    currentScreen = currentScreen,
                    cartItemCount = totalCartItems,
                    onTabSelected = { targetTab -> viewModel.navigateTo(targetTab) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val screen = currentScreen) {
                is Screen.Home -> {
                    HomeScreen(
                        viewModel = viewModel,
                        onNavigate = { dest -> viewModel.navigateTo(dest) }
                    )
                }
                is Screen.Search -> {
                    SearchScreen(
                        viewModel = viewModel,
                        onNavigate = { dest -> viewModel.navigateTo(dest) }
                    )
                }
                is Screen.Orders -> {
                    OrdersScreen(
                        viewModel = viewModel,
                        onNavigate = { dest -> viewModel.navigateTo(dest) }
                    )
                }
                is Screen.Wallet -> {
                    WalletScreen(
                        viewModel = viewModel,
                        onNavigate = { dest: Screen -> viewModel.navigateTo(dest) }
                    )
                }
                is Screen.Profile -> {
                    ProfileScreen(
                        viewModel = viewModel,
                        onNavigate = { dest -> viewModel.navigateTo(dest) }
                    )
                }
                is Screen.ShopDetail -> {
                    ShopDetailScreen(
                        viewModel = viewModel,
                        shopId = screen.shopId,
                        onNavigate = { dest -> viewModel.navigateTo(dest) },
                        onBack = { viewModel.navigateBack() }
                    )
                }
                is Screen.ProductDetail -> {
                    ProductDetailScreen(
                        viewModel = viewModel,
                        productId = screen.productId,
                        onNavigate = { dest -> viewModel.navigateTo(dest) },
                        onBack = { viewModel.navigateBack() }
                    )
                }
                is Screen.Cart -> {
                    CartScreen(
                        viewModel = viewModel,
                        targetShopId = screen.targetShopId,
                        onNavigate = { dest -> viewModel.navigateTo(dest) },
                        onBack = { viewModel.navigateBack() }
                    )
                }
                is Screen.Checkout -> {
                    CheckoutScreen(
                        viewModel = viewModel,
                        shopId = screen.shopId,
                        onNavigate = { dest -> viewModel.navigateTo(dest) },
                        onBack = { viewModel.navigateBack() }
                    )
                }
                is Screen.OrderDetail -> {
                    OrderDetailScreen(
                        viewModel = viewModel,
                        orderId = screen.orderId,
                        onBack = { viewModel.navigateBack() }
                    )
                }
                is Screen.PromoteAndEarn -> {
                    PromoteAndEarnScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() }
                    )
                }
                is Screen.ServiceBooking -> {
                    ServiceRideBookingScreen(
                        viewModel = viewModel,
                        title = screen.serviceName,
                        mode = "SERVICE",
                        onBack = { viewModel.navigateBack() }
                    )
                }
                is Screen.RideBooking -> {
                    ServiceRideBookingScreen(
                        viewModel = viewModel,
                        title = "${screen.rideType} Booking",
                        mode = if (screen.rideType == "Tempo") "TEMPO" else "RIDE",
                        onBack = { viewModel.navigateBack() }
                    )
                }
                is Screen.Auth -> {
                    AuthScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() }
                    )
                }
            }
        }
    }
}
