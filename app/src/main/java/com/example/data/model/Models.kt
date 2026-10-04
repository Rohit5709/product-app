package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class UserRole {
    CUSTOMER,
    SHOPKEEPER,
    DELIVERY_PARTNER,
    BIKE_DRIVER,
    TEMPO_DRIVER,
    SERVICE_PROVIDER,
    ADMIN
}

enum class OrderStatus {
    PENDING,
    SHOP_ACCEPTED,
    SHOP_REJECTED,
    PREPARING,
    READY_FOR_PICKUP,
    ASSIGNED_TO_DELIVERY,
    PICKED_UP,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED,
    REFUNDED
}

enum class PaymentStatus {
    PENDING,
    SUCCESS,
    FAILED,
    REFUNDED,
    PARTIALLY_REFUNDED
}

enum class PaymentMethod {
    UPI,
    WALLET,
    CASH_ON_DELIVERY,
    ONLINE_CARD
}

enum class SettlementStatus {
    PENDING,
    ELIGIBLE,
    PROCESSING,
    PAID,
    FAILED,
    ON_HOLD
}

enum class WalletTransactionType {
    CASHBACK,
    REFUND,
    CREDIT,
    DEBIT,
    ADJUSTMENT
}

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val phoneNumber: String,
    val email: String? = null,
    val role: UserRole = UserRole.CUSTOMER,
    val avatarUrl: String? = null,
    val currentCity: String = "Jaipur",
    val walletBalance: Double = 250.0,
    val isVerified: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val iconName: String,
    val imageUrl: String? = null,
    val displayOrder: Int = 0
)

@Entity(
    tableName = "shops",
    indices = [Index(value = ["city"]), Index(value = ["categoryId"])]
)
data class ShopEntity(
    @PrimaryKey val id: String,
    val name: String,
    val categoryId: String,
    val categoryName: String,
    val logoUrl: String,
    val coverUrl: String,
    val address: String,
    val city: String = "Jaipur",
    val latitude: Double = 26.9124,
    val longitude: Double = 75.7873,
    val contactNumber: String,
    val openingHours: String = "08:00 AM - 10:00 PM",
    val rating: Double = 4.8,
    val reviewCount: Int = 120,
    val deliveryAvailable: Boolean = true,
    val minOrderAmount: Double = 100.0,
    val estimatedDeliveryMins: Int = 30,
    val commissionRatePercent: Double = 10.0,
    val isActive: Boolean = true,
    val isVerified: Boolean = true
)

@Entity(
    tableName = "products",
    foreignKeys = [
        ForeignKey(
            entity = ShopEntity::class,
            parentColumns = ["id"],
            childColumns = ["shopId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["shopId"]), Index(value = ["categoryId"]), Index(value = ["name"])]
)
data class ProductEntity(
    @PrimaryKey val id: String,
    val shopId: String,
    val name: String,
    val description: String,
    val categoryId: String,
    val brand: String = "Local Choice",
    val sku: String,
    val sellingPrice: Double,
    val originalPrice: Double,
    val discountPercent: Int = 0,
    val stockQuantity: Int = 50,
    val unit: String = "piece", // kg, g, litre, packet, piece
    val weight: String = "1 unit",
    val isAvailable: Boolean = true,
    val imageUrl: String
)

@Entity(
    tableName = "cart_items",
    indices = [Index(value = ["userId"]), Index(value = ["shopId"])]
)
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val shopId: String,
    val productId: String,
    val quantity: Int = 1,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "orders",
    indices = [Index(value = ["customerId"]), Index(value = ["shopId"]), Index(value = ["orderStatus"])]
)
data class OrderEntity(
    @PrimaryKey val id: String,
    val orderNumber: String,
    val customerId: String,
    val customerName: String,
    val customerPhone: String,
    val shopId: String,
    val shopName: String,
    val shopAddress: String,
    val deliveryAddress: String,
    val deliveryCity: String,
    val totalItemAmount: Double,
    val deliveryFee: Double,
    val platformFee: Double = 5.0,
    val discountAmount: Double = 0.0,
    val finalPayableAmount: Double,
    val paymentMethod: PaymentMethod,
    val paymentStatus: PaymentStatus,
    val orderStatus: OrderStatus,
    val commissionAmount: Double,
    val shopSettlementAmount: Double,
    val deliveryPartnerId: String? = null,
    val deliveryPartnerName: String? = null,
    val specialInstructions: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "order_items",
    foreignKeys = [
        ForeignKey(
            entity = OrderEntity::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["orderId"])]
)
data class OrderItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderId: String,
    val productId: String,
    val productName: String,
    val productUnit: String,
    val unitPrice: Double,
    val quantity: Int,
    val totalPrice: Double,
    val imageUrl: String
)

@Entity(tableName = "wallet_transactions", indices = [Index(value = ["userId"])])
data class WalletTransactionEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val amount: Double,
    val type: WalletTransactionType,
    val description: String,
    val idempotencyKey: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_addresses", indices = [Index(value = ["userId"])])
data class UserAddressEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val label: String, // Home, Work, Other
    val fullAddress: String,
    val landmark: String = "",
    val city: String = "Jaipur",
    val pinCode: String = "302001",
    val isDefault: Boolean = false
)

@Entity(tableName = "settlements", indices = [Index(value = ["shopId"]), Index(value = ["orderId"])])
data class SettlementEntity(
    @PrimaryKey val id: String,
    val orderId: String,
    val shopId: String,
    val grossAmount: Double,
    val commissionAmount: Double,
    val netPayable: Double,
    val status: SettlementStatus,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "pricing_configs")
data class PricingConfigEntity(
    @PrimaryKey val key: String,
    val value: String,
    val description: String
)
