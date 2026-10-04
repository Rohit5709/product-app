package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.R
import com.example.data.local.AppDatabase
import com.example.data.model.PaymentMethod
import com.example.data.model.PaymentStatus
import com.example.data.model.ProductEntity
import com.example.data.model.UserRole
import com.example.data.repository.AppRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    private lateinit var db: AppDatabase
    private lateinit var repository: AppRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = AppRepository(db)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("LocalSuper", appName)
    }

    @Test
    fun `test initial database seed and product search`() = runBlocking {
        db.seedInitialData()

        val shops = repository.getAllShops().first()
        assertTrue("Shops should be pre-seeded", shops.isNotEmpty())
        assertEquals(5, shops.size)

        val products = repository.searchProducts("Atta").first()
        assertTrue("Should find Atta product", products.isNotEmpty())
        assertEquals("Aashirvaad Shudh Chakki Atta", products[0].name)
    }

    @Test
    fun `test add to cart and order creation with settlement calculation`() = runBlocking {
        db.seedInitialData()

        val user = repository.getCurrentUser().first()
        assertNotNull(user)
        // Top up wallet so user has sufficient balance for order
        repository.addMoneyToWallet(user!!.id, 500.0)
        val userAfterTopup = repository.getCurrentUserDirect(user.id)
        val initialWalletBalance = userAfterTopup!!.walletBalance

        val product = repository.getProductByIdDirect("prod_01")
        assertNotNull(product)

        // Add 2 units of Atta to cart
        val addResult = repository.addToCart(user.id, product!!, 2)
        assertTrue(addResult.isSuccess)

        val cartDetails = repository.getCartDetails(user.id).first()
        assertTrue(cartDetails.isNotEmpty())
        val shopCart = cartDetails[0]
        assertEquals(1, shopCart.items.size)
        assertEquals(2, shopCart.items[0].cartItem.quantity)
        assertEquals(490.0, shopCart.subtotal, 0.01)

        // Place order with WALLET payment
        val orderResult = repository.createOrder(
            userId = user.id,
            shopId = product.shopId,
            deliveryAddress = "Flat 402, Tonk Road, Jaipur",
            deliveryCity = "Jaipur",
            paymentMethod = PaymentMethod.WALLET,
            specialInstructions = "Leave at door"
        )

        assertTrue("Order should be placed successfully", orderResult.isSuccess)
        val order = orderResult.getOrThrow()
        assertEquals(PaymentStatus.SUCCESS, order.paymentStatus)

        // Verify stock was deducted: was 45, now should be 43
        val updatedProduct = repository.getProductByIdDirect(product.id)
        assertEquals(43, updatedProduct!!.stockQuantity)

        // Verify platform commission calculated
        assertTrue(order.commissionAmount > 0)
        assertEquals(order.totalItemAmount - order.commissionAmount, order.shopSettlementAmount, 0.01)

        // Verify wallet was debited
        val updatedUser = repository.getCurrentUserDirect(user.id)
        assertEquals(initialWalletBalance - order.finalPayableAmount, updatedUser!!.walletBalance, 0.01)

        // Verify cart is now cleared for this shop
        val cartAfter = repository.getCartDetails(user.id).first()
        assertTrue("Cart should be cleared after order", cartAfter.isEmpty())
    }
}
