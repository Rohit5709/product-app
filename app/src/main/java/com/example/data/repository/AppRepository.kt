package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.CartItemEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.OrderEntity
import com.example.data.model.OrderItemEntity
import com.example.data.model.OrderStatus
import com.example.data.model.PaymentMethod
import com.example.data.model.PaymentStatus
import com.example.data.model.ProductEntity
import com.example.data.model.SettlementEntity
import com.example.data.model.SettlementStatus
import com.example.data.model.ShopEntity
import com.example.data.model.UserAddressEntity
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.data.model.WalletTransactionEntity
import com.example.data.model.WalletTransactionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID

data class CartItemDetail(
    val cartItem: CartItemEntity,
    val product: ProductEntity,
    val shop: ShopEntity?
)

data class ShopCartGroup(
    val shop: ShopEntity,
    val items: List<CartItemDetail>,
    val subtotal: Double,
    val deliveryFee: Double,
    val platformFee: Double,
    val discount: Double,
    val finalTotal: Double
)

class AppRepository(private val db: AppDatabase) {

    private val userDao = db.userDao()
    private val categoryDao = db.categoryDao()
    private val shopDao = db.shopDao()
    private val productDao = db.productDao()
    private val cartDao = db.cartDao()
    private val orderDao = db.orderDao()
    private val walletDao = db.walletDao()
    private val userAddressDao = db.userAddressDao()
    private val settlementDao = db.settlementDao()

    suspend fun ensureDatabaseSeeded() = withContext(Dispatchers.IO) {
        val user = userDao.getUserByIdDirect("user_cust_01")
        if (user == null) {
            db.seedInitialData()
        }
    }

    fun getCurrentUser(userId: String = "user_cust_01"): Flow<UserEntity?> {
        return userDao.getUserById(userId)
    }

    suspend fun getCurrentUserDirect(userId: String = "user_cust_01"): UserEntity? {
        return withContext(Dispatchers.IO) {
            userDao.getUserByIdDirect(userId)
        }
    }

    suspend fun loginOrRegister(phoneNumber: String, name: String, role: UserRole): UserEntity = withContext(Dispatchers.IO) {
        val user = UserEntity(
            id = "user_cust_01",
            name = name.ifBlank { "Rohit Jangir" },
            phoneNumber = phoneNumber.ifBlank { "+91 98765 43210" },
            email = "rohitjangir5709@gmail.com",
            role = role,
            walletBalance = 350.0
        )
        userDao.insertUser(user)
        user
    }

    suspend fun switchRole(userId: String, newRole: UserRole) = withContext(Dispatchers.IO) {
        val current = userDao.getUserByIdDirect(userId)
        if (current != null) {
            userDao.insertUser(current.copy(role = newRole))
        }
    }

    fun getCategories(): Flow<List<CategoryEntity>> = categoryDao.getAllCategories()

    fun getAllShops(): Flow<List<ShopEntity>> = shopDao.getAllShops()

    fun getShopsByCategory(categoryId: String): Flow<List<ShopEntity>> = shopDao.getShopsByCategory(categoryId)

    fun getShopById(shopId: String): Flow<ShopEntity?> = shopDao.getShopById(shopId)

    suspend fun getShopByIdDirect(shopId: String): ShopEntity? = withContext(Dispatchers.IO) {
        shopDao.getShopByIdDirect(shopId)
    }

    fun searchShops(query: String): Flow<List<ShopEntity>> = shopDao.searchShops(query)

    fun getProductsForShop(shopId: String): Flow<List<ProductEntity>> = productDao.getProductsForShop(shopId)

    fun getProductById(productId: String): Flow<ProductEntity?> = productDao.getProductById(productId)

    suspend fun getProductByIdDirect(productId: String): ProductEntity? = withContext(Dispatchers.IO) {
        productDao.getProductByIdDirect(productId)
    }

    fun searchProducts(query: String, categoryId: String? = null): Flow<List<ProductEntity>> {
        return if (categoryId.isNullOrBlank() || categoryId == "all") {
            productDao.searchProducts(query)
        } else {
            productDao.searchProductsByCategory(categoryId, query)
        }
    }

    fun getCartDetails(userId: String = "user_cust_01"): Flow<List<ShopCartGroup>> {
        return cartDao.getCartItems(userId).map { items ->
            withContext(Dispatchers.IO) {
                val detailedItems = items.mapNotNull { item ->
                    val product = productDao.getProductByIdDirect(item.productId)
                    val shop = shopDao.getShopByIdDirect(item.shopId)
                    if (product != null) {
                        CartItemDetail(item, product, shop)
                    } else null
                }

                // Group by shop to adhere to strict single-shop or multi-shop cart rules
                val groupsByShop = detailedItems.groupBy { it.cartItem.shopId }
                groupsByShop.mapNotNull { (shopId, groupItems) ->
                    val shop = groupItems.firstOrNull()?.shop ?: shopDao.getShopByIdDirect(shopId)
                    if (shop != null) {
                        val subtotal = groupItems.sumOf { it.cartItem.quantity * it.product.sellingPrice }
                        val deliveryFee = if (subtotal >= shop.minOrderAmount) 25.0 else 40.0
                        val platformFee = 5.0
                        val discount = if (subtotal > 300) 25.0 else 0.0
                        val finalTotal = (subtotal + deliveryFee + platformFee - discount).coerceAtLeast(0.0)

                        ShopCartGroup(
                            shop = shop,
                            items = groupItems,
                            subtotal = subtotal,
                            deliveryFee = deliveryFee,
                            platformFee = platformFee,
                            discount = discount,
                            finalTotal = finalTotal
                        )
                    } else null
                }
            }
        }
    }

    suspend fun addToCart(userId: String = "user_cust_01", product: ProductEntity, quantity: Int = 1): Result<Unit> = withContext(Dispatchers.IO) {
        if (product.stockQuantity < quantity) {
            return@withContext Result.failure(Exception("Insufficient stock available"))
        }
        val existing = cartDao.getCartItem(userId, product.id)
        if (existing != null) {
            val newQty = existing.quantity + quantity
            if (newQty > product.stockQuantity) {
                return@withContext Result.failure(Exception("Cannot add more than available stock (${product.stockQuantity})"))
            }
            cartDao.updateQuantity(existing.id, newQty)
        } else {
            val newItem = CartItemEntity(
                userId = userId,
                shopId = product.shopId,
                productId = product.id,
                quantity = quantity
            )
            cartDao.insertCartItem(newItem)
        }
        Result.success(Unit)
    }

    suspend fun updateCartQuantity(cartItemId: Long, quantity: Int): Result<Unit> = withContext(Dispatchers.IO) {
        if (quantity <= 0) {
            cartDao.deleteCartItem(cartItemId)
        } else {
            cartDao.updateQuantity(cartItemId, quantity)
        }
        Result.success(Unit)
    }

    suspend fun removeCartItem(cartItemId: Long) = withContext(Dispatchers.IO) {
        cartDao.deleteCartItem(cartItemId)
    }

    suspend fun clearCartForShop(userId: String, shopId: String) = withContext(Dispatchers.IO) {
        cartDao.clearCartForShop(userId, shopId)
    }

    suspend fun createOrder(
        userId: String,
        shopId: String,
        deliveryAddress: String,
        deliveryCity: String,
        paymentMethod: PaymentMethod,
        specialInstructions: String = ""
    ): Result<OrderEntity> = withContext(Dispatchers.IO) {
        try {
            val user = userDao.getUserByIdDirect(userId) ?: return@withContext Result.failure(Exception("User not found"))
            val shop = shopDao.getShopByIdDirect(shopId) ?: return@withContext Result.failure(Exception("Shop not found"))

            val allCartItems = cartDao.getCartItemsDirect(userId).filter { it.shopId == shopId }
            if (allCartItems.isEmpty()) {
                return@withContext Result.failure(Exception("Cart is empty for this shop"))
            }

            // Verify stock and compute subtotal
            val orderItemsToInsert = mutableListOf<OrderItemEntity>()
            var subtotal = 0.0
            val orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8).uppercase()

            for (cartItem in allCartItems) {
                val product = productDao.getProductByIdDirect(cartItem.productId)
                    ?: return@withContext Result.failure(Exception("Product not found: ${cartItem.productId}"))

                if (product.stockQuantity < cartItem.quantity) {
                    return@withContext Result.failure(Exception("Product '${product.name}' is out of stock or requested quantity unavailable"))
                }

                val itemTotal = cartItem.quantity * product.sellingPrice
                subtotal += itemTotal

                orderItemsToInsert.add(
                    OrderItemEntity(
                        orderId = orderId,
                        productId = product.id,
                        productName = product.name,
                        productUnit = "${product.weight} (${product.unit})",
                        unitPrice = product.sellingPrice,
                        quantity = cartItem.quantity,
                        totalPrice = itemTotal,
                        imageUrl = product.imageUrl
                    )
                )

                // Deduct inventory
                productDao.updateStock(product.id, product.stockQuantity - cartItem.quantity)
            }

            val deliveryFee = if (subtotal >= shop.minOrderAmount) 25.0 else 40.0
            val platformFee = 5.0
            val discount = if (subtotal > 300) 25.0 else 0.0
            val finalTotal = (subtotal + deliveryFee + platformFee - discount).coerceAtLeast(0.0)

            // Handle payment verification
            val paymentStatus = when (paymentMethod) {
                PaymentMethod.WALLET -> {
                    if (user.walletBalance < finalTotal) {
                        return@withContext Result.failure(Exception("Insufficient wallet balance (Available: ₹${user.walletBalance})"))
                    }
                    val newBalance = user.walletBalance - finalTotal
                    userDao.updateWalletBalance(userId, newBalance)
                    walletDao.insertTransaction(
                        WalletTransactionEntity(
                            id = "TX-" + UUID.randomUUID().toString().substring(0, 8),
                            userId = userId,
                            amount = finalTotal,
                            type = WalletTransactionType.DEBIT,
                            description = "Payment for Order #$orderId",
                            idempotencyKey = "order_pay_$orderId"
                        )
                    )
                    PaymentStatus.SUCCESS
                }
                PaymentMethod.UPI, PaymentMethod.ONLINE_CARD -> PaymentStatus.SUCCESS
                PaymentMethod.CASH_ON_DELIVERY -> PaymentStatus.PENDING
            }

            // Calculate commission and shop settlement
            val commissionAmount = subtotal * (shop.commissionRatePercent / 100.0)
            val shopSettlementAmount = subtotal - commissionAmount

            val order = OrderEntity(
                id = orderId,
                orderNumber = "#LS" + (1000..9999).random(),
                customerId = user.id,
                customerName = user.name,
                customerPhone = user.phoneNumber,
                shopId = shop.id,
                shopName = shop.name,
                shopAddress = shop.address,
                deliveryAddress = deliveryAddress,
                deliveryCity = deliveryCity,
                totalItemAmount = subtotal,
                deliveryFee = deliveryFee,
                platformFee = platformFee,
                discountAmount = discount,
                finalPayableAmount = finalTotal,
                paymentMethod = paymentMethod,
                paymentStatus = paymentStatus,
                orderStatus = OrderStatus.PENDING,
                commissionAmount = commissionAmount,
                shopSettlementAmount = shopSettlementAmount,
                specialInstructions = specialInstructions
            )

            orderDao.insertOrder(order)
            orderDao.insertOrderItems(orderItemsToInsert)

            // Create settlement record for platform accounting
            val settlement = SettlementEntity(
                id = "SETTLE-" + UUID.randomUUID().toString().substring(0, 8),
                orderId = orderId,
                shopId = shop.id,
                grossAmount = subtotal,
                commissionAmount = commissionAmount,
                netPayable = shopSettlementAmount,
                status = SettlementStatus.PENDING
            )
            settlementDao.insertSettlement(settlement)

            // Clear cart for this shop
            cartDao.clearCartForShop(userId, shopId)

            Result.success(order)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCustomerOrders(customerId: String = "user_cust_01"): Flow<List<OrderEntity>> {
        return orderDao.getOrdersForCustomer(customerId)
    }

    fun getOrderById(orderId: String): Flow<OrderEntity?> = orderDao.getOrderById(orderId)

    fun getOrderItems(orderId: String): Flow<List<OrderItemEntity>> = orderDao.getOrderItems(orderId)

    suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus) = withContext(Dispatchers.IO) {
        orderDao.updateOrderStatus(orderId, newStatus, System.currentTimeMillis())
        if (newStatus == OrderStatus.DELIVERED) {
            orderDao.updatePaymentStatus(orderId, PaymentStatus.SUCCESS, System.currentTimeMillis())
        }
    }

    suspend fun cancelOrder(orderId: String): Result<Unit> = withContext(Dispatchers.IO) {
        val order = orderDao.getOrderByIdDirect(orderId) ?: return@withContext Result.failure(Exception("Order not found"))
        if (order.orderStatus == OrderStatus.DELIVERED || order.orderStatus == OrderStatus.CANCELLED) {
            return@withContext Result.failure(Exception("Order cannot be cancelled at this stage"))
        }

        orderDao.updateOrderStatus(orderId, OrderStatus.CANCELLED, System.currentTimeMillis())

        // Restock products
        val items = orderDao.getOrderItemsDirect(orderId)
        for (item in items) {
            val product = productDao.getProductByIdDirect(item.productId)
            if (product != null) {
                productDao.updateStock(product.id, product.stockQuantity + item.quantity)
            }
        }

        // Refund if already paid
        if (order.paymentStatus == PaymentStatus.SUCCESS) {
            val user = userDao.getUserByIdDirect(order.customerId)
            if (user != null) {
                userDao.updateWalletBalance(user.id, user.walletBalance + order.finalPayableAmount)
                walletDao.insertTransaction(
                    WalletTransactionEntity(
                        id = "REFUND-" + UUID.randomUUID().toString().substring(0, 8),
                        userId = user.id,
                        amount = order.finalPayableAmount,
                        type = WalletTransactionType.REFUND,
                        description = "Refund for cancelled Order #${order.orderNumber}",
                        idempotencyKey = "refund_${order.id}"
                    )
                )
                orderDao.updatePaymentStatus(orderId, PaymentStatus.REFUNDED, System.currentTimeMillis())
            }
        }

        Result.success(Unit)
    }

    fun getUserAddresses(userId: String = "user_cust_01"): Flow<List<UserAddressEntity>> =
        userAddressDao.getAddresses(userId)

    suspend fun addAddress(address: UserAddressEntity) = withContext(Dispatchers.IO) {
        userAddressDao.insertAddress(address)
    }

    fun getWalletTransactions(userId: String = "user_cust_01"): Flow<List<WalletTransactionEntity>> =
        walletDao.getTransactions(userId)

    suspend fun addMoneyToWallet(userId: String, amount: Double) = withContext(Dispatchers.IO) {
        val user = userDao.getUserByIdDirect(userId)
        if (user != null) {
            val newBal = user.walletBalance + amount
            userDao.updateWalletBalance(userId, newBal)
            walletDao.insertTransaction(
                WalletTransactionEntity(
                    id = "TOPUP-" + UUID.randomUUID().toString().substring(0, 8),
                    userId = userId,
                    amount = amount,
                    type = WalletTransactionType.CREDIT,
                    description = "Wallet Top-up via UPI",
                    idempotencyKey = "topup_${System.currentTimeMillis()}"
                )
            )
        }
    }
}
