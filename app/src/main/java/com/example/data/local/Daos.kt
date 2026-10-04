package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.model.CartItemEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.OrderEntity
import com.example.data.model.OrderItemEntity
import com.example.data.model.OrderStatus
import com.example.data.model.PaymentStatus
import com.example.data.model.PricingConfigEntity
import com.example.data.model.ProductEntity
import com.example.data.model.SettlementEntity
import com.example.data.model.ShopEntity
import com.example.data.model.UserAddressEntity
import com.example.data.model.UserEntity
import com.example.data.model.WalletTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id")
    fun getUserById(id: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserByIdDirect(id: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("UPDATE users SET walletBalance = :balance WHERE id = :userId")
    suspend fun updateWalletBalance(userId: String, balance: Double)
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY displayOrder ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)
}

@Dao
interface ShopDao {
    @Query("SELECT * FROM shops WHERE isActive = 1 ORDER BY rating DESC")
    fun getAllShops(): Flow<List<ShopEntity>>

    @Query("SELECT * FROM shops WHERE categoryId = :categoryId AND isActive = 1")
    fun getShopsByCategory(categoryId: String): Flow<List<ShopEntity>>

    @Query("SELECT * FROM shops WHERE id = :shopId")
    fun getShopById(shopId: String): Flow<ShopEntity?>

    @Query("SELECT * FROM shops WHERE id = :shopId")
    suspend fun getShopByIdDirect(shopId: String): ShopEntity?

    @Query("SELECT * FROM shops WHERE isActive = 1 AND (name LIKE '%' || :query || '%' OR categoryName LIKE '%' || :query || '%' OR address LIKE '%' || :query || '%')")
    fun searchShops(query: String): Flow<List<ShopEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShops(shops: List<ShopEntity>)
}

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE shopId = :shopId AND isAvailable = 1")
    fun getProductsForShop(shopId: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :productId")
    fun getProductById(productId: String): Flow<ProductEntity?>

    @Query("SELECT * FROM products WHERE id = :productId")
    suspend fun getProductByIdDirect(productId: String): ProductEntity?

    @Query("SELECT * FROM products WHERE isAvailable = 1 AND (name LIKE '%' || :query || '%' OR brand LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%')")
    fun searchProducts(query: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE categoryId = :categoryId AND isAvailable = 1 AND (name LIKE '%' || :query || '%' OR brand LIKE '%' || :query || '%')")
    fun searchProductsByCategory(categoryId: String, query: String): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Query("UPDATE products SET stockQuantity = :newStock WHERE id = :productId")
    suspend fun updateStock(productId: String, newStock: Int)
}

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items WHERE userId = :userId ORDER BY addedAt DESC")
    fun getCartItems(userId: String): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart_items WHERE userId = :userId")
    suspend fun getCartItemsDirect(userId: String): List<CartItemEntity>

    @Query("SELECT * FROM cart_items WHERE userId = :userId AND productId = :productId LIMIT 1")
    suspend fun getCartItem(userId: String, productId: String): CartItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItemEntity)

    @Query("UPDATE cart_items SET quantity = :quantity WHERE id = :id")
    suspend fun updateQuantity(id: Long, quantity: Int)

    @Query("DELETE FROM cart_items WHERE id = :id")
    suspend fun deleteCartItem(id: Long)

    @Query("DELETE FROM cart_items WHERE userId = :userId")
    suspend fun clearCart(userId: String)

    @Query("DELETE FROM cart_items WHERE userId = :userId AND shopId = :shopId")
    suspend fun clearCartForShop(userId: String, shopId: String)
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders WHERE customerId = :customerId ORDER BY createdAt DESC")
    fun getOrdersForCustomer(customerId: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE id = :orderId")
    fun getOrderById(orderId: String): Flow<OrderEntity?>

    @Query("SELECT * FROM orders WHERE id = :orderId")
    suspend fun getOrderByIdDirect(orderId: String): OrderEntity?

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    fun getOrderItems(orderId: String): Flow<List<OrderItemEntity>>

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getOrderItemsDirect(orderId: String): List<OrderItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<OrderItemEntity>)

    @Query("UPDATE orders SET orderStatus = :status, updatedAt = :updatedAt WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: String, status: OrderStatus, updatedAt: Long)

    @Query("UPDATE orders SET paymentStatus = :status, updatedAt = :updatedAt WHERE id = :orderId")
    suspend fun updatePaymentStatus(orderId: String, status: PaymentStatus, updatedAt: Long)
}

@Dao
interface WalletDao {
    @Query("SELECT * FROM wallet_transactions WHERE userId = :userId ORDER BY timestamp DESC")
    fun getTransactions(userId: String): Flow<List<WalletTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(tx: WalletTransactionEntity)
}

@Dao
interface UserAddressDao {
    @Query("SELECT * FROM user_addresses WHERE userId = :userId")
    fun getAddresses(userId: String): Flow<List<UserAddressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddress(address: UserAddressEntity)

    @Query("SELECT * FROM user_addresses WHERE userId = :userId AND isDefault = 1 LIMIT 1")
    fun getDefaultAddress(userId: String): Flow<UserAddressEntity?>

    @Query("SELECT * FROM user_addresses WHERE userId = :userId LIMIT 1")
    suspend fun getAnyAddressDirect(userId: String): UserAddressEntity?
}

@Dao
interface SettlementDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettlement(settlement: SettlementEntity)

    @Query("SELECT * FROM settlements WHERE shopId = :shopId ORDER BY createdAt DESC")
    fun getSettlementsForShop(shopId: String): Flow<List<SettlementEntity>>
}

@Dao
interface PricingConfigDao {
    @Query("SELECT * FROM pricing_configs WHERE key = :key LIMIT 1")
    suspend fun getConfig(key: String): PricingConfigEntity?

    @Query("SELECT * FROM pricing_configs")
    fun getAllConfigs(): Flow<List<PricingConfigEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfigs(configs: List<PricingConfigEntity>)
}
