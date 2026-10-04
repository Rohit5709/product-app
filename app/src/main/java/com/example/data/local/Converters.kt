package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.OrderStatus
import com.example.data.model.PaymentMethod
import com.example.data.model.PaymentStatus
import com.example.data.model.SettlementStatus
import com.example.data.model.UserRole
import com.example.data.model.WalletTransactionType

class Converters {
    @TypeConverter
    fun fromUserRole(value: UserRole): String = value.name

    @TypeConverter
    fun toUserRole(value: String): UserRole = try {
        UserRole.valueOf(value)
    } catch (_: Exception) {
        UserRole.CUSTOMER
    }

    @TypeConverter
    fun fromOrderStatus(value: OrderStatus): String = value.name

    @TypeConverter
    fun toOrderStatus(value: String): OrderStatus = try {
        OrderStatus.valueOf(value)
    } catch (_: Exception) {
        OrderStatus.PENDING
    }

    @TypeConverter
    fun fromPaymentStatus(value: PaymentStatus): String = value.name

    @TypeConverter
    fun toPaymentStatus(value: String): PaymentStatus = try {
        PaymentStatus.valueOf(value)
    } catch (_: Exception) {
        PaymentStatus.PENDING
    }

    @TypeConverter
    fun fromPaymentMethod(value: PaymentMethod): String = value.name

    @TypeConverter
    fun toPaymentMethod(value: String): PaymentMethod = try {
        PaymentMethod.valueOf(value)
    } catch (_: Exception) {
        PaymentMethod.UPI
    }

    @TypeConverter
    fun fromSettlementStatus(value: SettlementStatus): String = value.name

    @TypeConverter
    fun toSettlementStatus(value: String): SettlementStatus = try {
        SettlementStatus.valueOf(value)
    } catch (_: Exception) {
        SettlementStatus.PENDING
    }

    @TypeConverter
    fun fromWalletTransactionType(value: WalletTransactionType): String = value.name

    @TypeConverter
    fun toWalletTransactionType(value: String): WalletTransactionType = try {
        WalletTransactionType.valueOf(value)
    } catch (_: Exception) {
        WalletTransactionType.CREDIT
    }
}
