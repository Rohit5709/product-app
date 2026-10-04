package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.CategoryEntity
import com.example.data.model.OrderEntity
import com.example.data.model.OrderItemEntity
import com.example.data.model.OrderStatus
import com.example.data.model.PaymentMethod
import com.example.data.model.ProductEntity
import com.example.data.model.ShopEntity
import com.example.data.model.UserAddressEntity
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.data.model.WalletTransactionEntity
import com.example.data.repository.AppRepository
import com.example.data.repository.ShopCartGroup
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen {
    object Auth : Screen()
    object Home : Screen()
    object Search : Screen()
    object Orders : Screen()
    object Wallet : Screen()
    object Profile : Screen()
    data class ShopDetail(val shopId: String) : Screen()
    data class ProductDetail(val productId: String) : Screen()
    data class Cart(val targetShopId: String? = null) : Screen()
    data class Checkout(val shopId: String) : Screen()
    data class OrderDetail(val orderId: String) : Screen()
    data class ServiceBooking(val serviceName: String) : Screen()
    data class RideBooking(val rideType: String) : Screen()
    object PromoteAndEarn : Screen()
}

@OptIn(ExperimentalCoroutinesApi::class)
class MarketplaceViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = AppRepository(db)

    private val _navigationStack = MutableStateFlow<List<Screen>>(listOf(Screen.Home))
    val currentScreen: StateFlow<Screen> = _navigationStack
        .combine(MutableStateFlow(Unit)) { stack, _ -> stack.lastOrNull() ?: Screen.Home }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Screen.Home)

    val currentUser: StateFlow<UserEntity?> = repository.getCurrentUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val categories: StateFlow<List<CategoryEntity>> = repository.getCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val shops: StateFlow<List<ShopEntity>> = repository.getAllShops()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartGroups: StateFlow<List<ShopCartGroup>> = repository.getCartDetails()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orders: StateFlow<List<OrderEntity>> = repository.getCustomerOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val walletTransactions: StateFlow<List<WalletTransactionEntity>> = repository.getWalletTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userAddresses: StateFlow<List<UserAddressEntity>> = repository.getUserAddresses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Shop Detail screen state
    private val _selectedShopId = MutableStateFlow<String?>(null)
    val selectedShop: StateFlow<ShopEntity?> = _selectedShopId.flatMapLatest { id ->
        if (id != null) repository.getShopById(id) else flowOf(null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val selectedShopProducts: StateFlow<List<ProductEntity>> = _selectedShopId.flatMapLatest { id ->
        if (id != null) repository.getProductsForShop(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Product Detail screen state
    private val _selectedProductId = MutableStateFlow<String?>(null)
    val selectedProduct: StateFlow<ProductEntity?> = _selectedProductId.flatMapLatest { id ->
        if (id != null) repository.getProductById(id) else flowOf(null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Order Detail screen state
    private val _selectedOrderId = MutableStateFlow<String?>(null)
    val selectedOrder: StateFlow<OrderEntity?> = _selectedOrderId.flatMapLatest { id ->
        if (id != null) repository.getOrderById(id) else flowOf(null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val selectedOrderItems: StateFlow<List<OrderItemEntity>> = _selectedOrderId.flatMapLatest { id ->
        if (id != null) repository.getOrderItems(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Global Search state
    val searchQuery = MutableStateFlow("")
    val searchCategoryFilter = MutableStateFlow<String?>(null)

    val searchResults: StateFlow<List<ProductEntity>> = combine(
        searchQuery,
        searchCategoryFilter
    ) { query, catId ->
        Pair(query, catId)
    }.flatMapLatest { (query, catId) ->
        if (query.isBlank() && catId == null) {
            flowOf(emptyList())
        } else {
            repository.searchProducts(query.trim(), catId)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Feedback messages
    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage = _snackbarMessage.asSharedFlow()

    init {
        viewModelScope.launch {
            repository.ensureDatabaseSeeded()
        }
    }

    fun navigateTo(screen: Screen) {
        val currentStack = _navigationStack.value.toMutableList()
        // If bottom tab destination, clear stack and set root
        when (screen) {
            Screen.Home, Screen.Search, Screen.Orders, Screen.Wallet, Screen.Profile -> {
                _navigationStack.value = listOf(screen)
            }
            is Screen.ShopDetail -> {
                _selectedShopId.value = screen.shopId
                currentStack.add(screen)
                _navigationStack.value = currentStack
            }
            is Screen.ProductDetail -> {
                _selectedProductId.value = screen.productId
                currentStack.add(screen)
                _navigationStack.value = currentStack
            }
            is Screen.OrderDetail -> {
                _selectedOrderId.value = screen.orderId
                currentStack.add(screen)
                _navigationStack.value = currentStack
            }
            else -> {
                currentStack.add(screen)
                _navigationStack.value = currentStack
            }
        }
    }

    fun navigateBack(): Boolean {
        val currentStack = _navigationStack.value.toMutableList()
        if (currentStack.size > 1) {
            currentStack.removeAt(currentStack.size - 1)
            _navigationStack.value = currentStack
            return true
        }
        return false
    }

    fun loginOrRegister(phoneNumber: String, name: String, role: UserRole) {
        viewModelScope.launch {
            repository.loginOrRegister(phoneNumber, name, role)
            _snackbarMessage.emit("Welcome, $name! Authenticated as ${role.name.replace("_", " ")}")
            navigateTo(Screen.Home)
        }
    }

    fun switchRole(role: UserRole) {
        viewModelScope.launch {
            val user = currentUser.value
            if (user != null) {
                repository.switchRole(user.id, role)
                _snackbarMessage.emit("Role switched to ${role.name.replace("_", " ")}")
            }
        }
    }

    fun addToCart(product: ProductEntity, quantity: Int = 1) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            val result = repository.addToCart(user.id, product, quantity)
            if (result.isSuccess) {
                _snackbarMessage.emit("Added '${product.name}' to cart")
            } else {
                _snackbarMessage.emit(result.exceptionOrNull()?.message ?: "Failed to add to cart")
            }
        }
    }

    fun updateCartQuantity(cartItemId: Long, newQuantity: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(cartItemId, newQuantity)
        }
    }

    fun removeCartItem(cartItemId: Long) {
        viewModelScope.launch {
            repository.removeCartItem(cartItemId)
            _snackbarMessage.emit("Item removed from cart")
        }
    }

    fun placeOrder(
        shopId: String,
        deliveryAddress: String,
        deliveryCity: String,
        paymentMethod: PaymentMethod,
        specialInstructions: String = "",
        onSuccess: (String) -> Unit
    ) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            val result = repository.createOrder(
                userId = user.id,
                shopId = shopId,
                deliveryAddress = deliveryAddress,
                deliveryCity = deliveryCity,
                paymentMethod = paymentMethod,
                specialInstructions = specialInstructions
            )
            if (result.isSuccess) {
                val order = result.getOrThrow()
                _snackbarMessage.emit("Order placed successfully! Order ID: ${order.orderNumber}")
                onSuccess(order.id)
            } else {
                _snackbarMessage.emit(result.exceptionOrNull()?.message ?: "Order creation failed")
            }
        }
    }

    fun cancelOrder(orderId: String) {
        viewModelScope.launch {
            val result = repository.cancelOrder(orderId)
            if (result.isSuccess) {
                _snackbarMessage.emit("Order cancelled successfully. Refund processed to in-app wallet.")
            } else {
                _snackbarMessage.emit(result.exceptionOrNull()?.message ?: "Failed to cancel order")
            }
        }
    }

    fun advanceOrderStatus(orderId: String, nextStatus: OrderStatus) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, nextStatus)
            _snackbarMessage.emit("Order status updated to: ${nextStatus.name.replace("_", " ")}")
        }
    }

    fun addMoneyToWallet(amount: Double) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            repository.addMoneyToWallet(user.id, amount)
            _snackbarMessage.emit("₹$amount added to in-app wallet successfully!")
        }
    }

    fun addNewAddress(label: String, address: String, landmark: String, pinCode: String) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            val newAddr = UserAddressEntity(
                id = "addr_" + System.currentTimeMillis(),
                userId = user.id,
                label = label,
                fullAddress = address,
                landmark = landmark,
                pinCode = pinCode,
                city = user.currentCity
            )
            repository.addAddress(newAddr)
            _snackbarMessage.emit("Address saved successfully")
        }
    }
}
