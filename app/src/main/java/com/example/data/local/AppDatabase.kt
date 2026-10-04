package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.CartItemEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.OrderEntity
import com.example.data.model.OrderItemEntity
import com.example.data.model.PricingConfigEntity
import com.example.data.model.ProductEntity
import com.example.data.model.SettlementEntity
import com.example.data.model.ShopEntity
import com.example.data.model.UserAddressEntity
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.data.model.WalletTransactionEntity
import com.example.data.model.WalletTransactionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        CategoryEntity::class,
        ShopEntity::class,
        ProductEntity::class,
        CartItemEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        WalletTransactionEntity::class,
        UserAddressEntity::class,
        SettlementEntity::class,
        PricingConfigEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun shopDao(): ShopDao
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
    abstract fun walletDao(): WalletDao
    abstract fun userAddressDao(): UserAddressDao
    abstract fun settlementDao(): SettlementDao
    abstract fun pricingConfigDao(): PricingConfigDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "localsuper_marketplace.db"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            getInstance(context).seedInitialData()
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }
    }

    suspend fun seedInitialData() {
        val user = UserEntity(
            id = "user_cust_01",
            name = "Rohit Jangir",
            phoneNumber = "+91 98765 43210",
            email = "rohitjangir5709@gmail.com",
            role = UserRole.CUSTOMER,
            walletBalance = 350.0,
            currentCity = "Jaipur"
        )
        userDao().insertUser(user)

        val address1 = UserAddressEntity(
            id = "addr_01",
            userId = user.id,
            label = "Home",
            fullAddress = "Flat 402, Royal Palms Heights, Tonk Road",
            landmark = "Near Gandhi Nagar Station",
            city = "Jaipur",
            pinCode = "302015",
            isDefault = true
        )
        val address2 = UserAddressEntity(
            id = "addr_02",
            userId = user.id,
            label = "Work",
            fullAddress = "Tech Park Tower B, 3rd Floor, Malviya Nagar",
            landmark = "Opposite World Trade Park",
            city = "Jaipur",
            pinCode = "302017",
            isDefault = false
        )
        userAddressDao().insertAddress(address1)
        userAddressDao().insertAddress(address2)

        val categories = listOf(
            CategoryEntity("cat_grocery", "Grocery & Daily", "local_grocery_store", displayOrder = 1),
            CategoryEntity("cat_pharmacy", "Medical & Care", "local_pharmacy", displayOrder = 2),
            CategoryEntity("cat_fruits", "Fresh Fruits & Veg", "eco", displayOrder = 3),
            CategoryEntity("cat_electronics", "Electronics", "devices", displayOrder = 4),
            CategoryEntity("cat_bakery", "Bakery & Sweets", "bakery_dining", displayOrder = 5),
            CategoryEntity("cat_restaurants", "Restaurant & Cafe", "restaurant", displayOrder = 6),
            CategoryEntity("cat_stationery", "Books & Stationery", "menu_book", displayOrder = 7),
            CategoryEntity("cat_hardware", "Hardware & Tools", "hardware", displayOrder = 8)
        )
        categoryDao().insertCategories(categories)

        val shops = listOf(
            ShopEntity(
                id = "shop_01",
                name = "Jaipur Fresh Supermart",
                categoryId = "cat_grocery",
                categoryName = "Grocery & Daily",
                logoUrl = "https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&w=200&q=80",
                coverUrl = "https://images.unsplash.com/photo-1578916171728-46686eac8d58?auto=format&fit=crop&w=800&q=80",
                address = "Shop 12, Tonk Road, Jaipur",
                contactNumber = "+91 98290 11223",
                rating = 4.8,
                reviewCount = 142,
                minOrderAmount = 150.0,
                estimatedDeliveryMins = 25,
                commissionRatePercent = 10.0
            ),
            ShopEntity(
                id = "shop_02",
                name = "Sanjeevani Medicos & Wellness",
                categoryId = "cat_pharmacy",
                categoryName = "Medical & Care",
                logoUrl = "https://images.unsplash.com/photo-1586015555751-63bb77f4322a?auto=format&fit=crop&w=200&q=80",
                coverUrl = "https://images.unsplash.com/photo-1631549916768-4119b2e5f926?auto=format&fit=crop&w=800&q=80",
                address = "Opp. Fortis Hospital, Malviya Nagar",
                contactNumber = "+91 98290 44556",
                rating = 4.9,
                reviewCount = 88,
                minOrderAmount = 100.0,
                estimatedDeliveryMins = 20,
                commissionRatePercent = 8.0
            ),
            ShopEntity(
                id = "shop_03",
                name = "Kisan Fresh Organic Farm",
                categoryId = "cat_fruits",
                categoryName = "Fresh Fruits & Veg",
                logoUrl = "https://images.unsplash.com/photo-1610832958506-aa56368176cf?auto=format&fit=crop&w=200&q=80",
                coverUrl = "https://images.unsplash.com/photo-1488459716781-31db52582fe9?auto=format&fit=crop&w=800&q=80",
                address = "Kisan Mandi, Lal Kothi, Jaipur",
                contactNumber = "+91 98290 77889",
                rating = 4.7,
                reviewCount = 160,
                minOrderAmount = 80.0,
                estimatedDeliveryMins = 30,
                commissionRatePercent = 7.0
            ),
            ShopEntity(
                id = "shop_04",
                name = "Pink City Tech Store",
                categoryId = "cat_electronics",
                categoryName = "Electronics",
                logoUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=200&q=80",
                coverUrl = "https://images.unsplash.com/photo-1550009158-9ebf69173e03?auto=format&fit=crop&w=800&q=80",
                address = "G-4, Gaurav Tower, Malviya Nagar",
                contactNumber = "+91 98290 99001",
                rating = 4.6,
                reviewCount = 64,
                minOrderAmount = 300.0,
                estimatedDeliveryMins = 45,
                commissionRatePercent = 12.0
            ),
            ShopEntity(
                id = "shop_05",
                name = "Kanha Sweets & Bakery",
                categoryId = "cat_bakery",
                categoryName = "Bakery & Sweets",
                logoUrl = "https://images.unsplash.com/photo-1509440159596-0249088772ff?auto=format&fit=crop&w=200&q=80",
                coverUrl = "https://images.unsplash.com/photo-1517433670267-08bbd4be890f?auto=format&fit=crop&w=800&q=80",
                address = "Main Market, C-Scheme, Jaipur",
                contactNumber = "+91 98290 33221",
                rating = 4.9,
                reviewCount = 310,
                minOrderAmount = 150.0,
                estimatedDeliveryMins = 35,
                commissionRatePercent = 10.0
            )
        )
        shopDao().insertShops(shops)

        val products = listOf(
            ProductEntity(
                id = "prod_01",
                shopId = "shop_01",
                name = "Aashirvaad Shudh Chakki Atta",
                description = "100% pure whole wheat flour processed hygienically with zero maida.",
                categoryId = "cat_grocery",
                brand = "Aashirvaad",
                sku = "ATT-5KG-01",
                sellingPrice = 245.0,
                originalPrice = 280.0,
                discountPercent = 12,
                stockQuantity = 45,
                unit = "bag",
                weight = "5 kg",
                imageUrl = "https://images.unsplash.com/photo-1586201375761-83865001e31c?auto=format&fit=crop&w=400&q=80"
            ),
            ProductEntity(
                id = "prod_02",
                shopId = "shop_01",
                name = "Amul Taaza Fresh Toned Milk",
                description = "Homogenized toned milk with goodness of calcium and pure nutrients.",
                categoryId = "cat_grocery",
                brand = "Amul",
                sku = "MLK-1L-02",
                sellingPrice = 54.0,
                originalPrice = 56.0,
                discountPercent = 3,
                stockQuantity = 90,
                unit = "pouch",
                weight = "1000 ml",
                imageUrl = "https://images.unsplash.com/photo-1550583724-b2692b85b150?auto=format&fit=crop&w=400&q=80"
            ),
            ProductEntity(
                id = "prod_03",
                shopId = "shop_01",
                name = "Fortune Sunlite Refined Sunflower Oil",
                description = "Light and healthy cooking oil enriched with Vitamin A and D.",
                categoryId = "cat_grocery",
                brand = "Fortune",
                sku = "OIL-1L-03",
                sellingPrice = 145.0,
                originalPrice = 175.0,
                discountPercent = 17,
                stockQuantity = 30,
                unit = "bottle",
                weight = "1 Litre",
                imageUrl = "https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?auto=format&fit=crop&w=400&q=80"
            ),
            ProductEntity(
                id = "prod_04",
                shopId = "shop_01",
                name = "Tata Salt Vacuum Evaporated Iodised",
                description = "The pioneer of packaged iodized salt in India.",
                categoryId = "cat_grocery",
                brand = "Tata",
                sku = "SLT-1KG-04",
                sellingPrice = 28.0,
                originalPrice = 30.0,
                discountPercent = 6,
                stockQuantity = 80,
                unit = "packet",
                weight = "1 kg",
                imageUrl = "https://images.unsplash.com/photo-1518843875459-f738682238a6?auto=format&fit=crop&w=400&q=80"
            ),
            ProductEntity(
                id = "prod_05",
                shopId = "shop_02",
                name = "Dettol Antiseptic Liquid First Aid",
                description = "Proven safe and effective concentrated antiseptic solution for wound care and disinfection.",
                categoryId = "cat_pharmacy",
                brand = "Dettol",
                sku = "DET-250ML",
                sellingPrice = 160.0,
                originalPrice = 175.0,
                discountPercent = 8,
                stockQuantity = 60,
                unit = "bottle",
                weight = "250 ml",
                imageUrl = "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?auto=format&fit=crop&w=400&q=80"
            ),
            ProductEntity(
                id = "prod_06",
                shopId = "shop_02",
                name = "Fast&Up Charge Natural Vitamin C Tablets",
                description = "Effervescent immune booster tablets with 1000mg Amla extract and Zinc.",
                categoryId = "cat_pharmacy",
                brand = "Fast&Up",
                sku = "VIT-TAB-20",
                sellingPrice = 299.0,
                originalPrice = 390.0,
                discountPercent = 23,
                stockQuantity = 35,
                unit = "tube",
                weight = "20 tabs",
                imageUrl = "https://images.unsplash.com/photo-1584017911766-d451b3d0e843?auto=format&fit=crop&w=400&q=80"
            ),
            ProductEntity(
                id = "prod_07",
                shopId = "shop_03",
                name = "Farm Fresh Shimla Apples",
                description = "Crisp, sweet, and juicy handpicked royal delicious apples.",
                categoryId = "cat_fruits",
                brand = "Farm Fresh",
                sku = "FRT-APP-1KG",
                sellingPrice = 160.0,
                originalPrice = 200.0,
                discountPercent = 20,
                stockQuantity = 25,
                unit = "pack",
                weight = "1 kg",
                imageUrl = "https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?auto=format&fit=crop&w=400&q=80"
            ),
            ProductEntity(
                id = "prod_08",
                shopId = "shop_03",
                name = "Fresh Organic Tomatoes",
                description = "Ripe, firm red farm tomatoes rich in lycopene and flavour.",
                categoryId = "cat_fruits",
                brand = "Organic Fields",
                sku = "VEG-TOM-1KG",
                sellingPrice = 38.0,
                originalPrice = 50.0,
                discountPercent = 24,
                stockQuantity = 40,
                unit = "pack",
                weight = "1 kg",
                imageUrl = "https://images.unsplash.com/photo-1592924357228-91a4daadcfea?auto=format&fit=crop&w=400&q=80"
            ),
            ProductEntity(
                id = "prod_09",
                shopId = "shop_04",
                name = "boAt BassHeads 100 Wired Earphones",
                description = "In-ear earphones with 10mm dynamic drivers and hawk inspired ergonomic design.",
                categoryId = "cat_electronics",
                brand = "boAt",
                sku = "BOAT-BH100",
                sellingPrice = 399.0,
                originalPrice = 999.0,
                discountPercent = 60,
                stockQuantity = 18,
                unit = "box",
                weight = "1 piece",
                imageUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=400&q=80"
            ),
            ProductEntity(
                id = "prod_10",
                shopId = "shop_04",
                name = "Portronics 65W Fast Charging Type-C Cable",
                description = "Braided heavy-duty 1.2m Type-C to Type-C fast data sync and quick charging cable.",
                categoryId = "cat_electronics",
                brand = "Portronics",
                sku = "PORT-CB-65W",
                sellingPrice = 249.0,
                originalPrice = 499.0,
                discountPercent = 50,
                stockQuantity = 22,
                unit = "box",
                weight = "1.2 meter",
                imageUrl = "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?auto=format&fit=crop&w=400&q=80"
            ),
            ProductEntity(
                id = "prod_11",
                shopId = "shop_05",
                name = "Kanha Special Desi Ghee Ghevar",
                description = "Authentic traditional Rajasthani honeycomb sweet prepared in pure desi cow ghee with dry fruits.",
                categoryId = "cat_bakery",
                brand = "Kanha",
                sku = "KNH-GHV-500",
                sellingPrice = 340.0,
                originalPrice = 380.0,
                discountPercent = 10,
                stockQuantity = 15,
                unit = "box",
                weight = "500 grams",
                imageUrl = "https://images.unsplash.com/photo-1509440159596-0249088772ff?auto=format&fit=crop&w=400&q=80"
            )
        )
        productDao().insertProducts(products)

        val tx1 = WalletTransactionEntity(
            id = "tx_welcome_01",
            userId = user.id,
            amount = 100.0,
            type = WalletTransactionType.CREDIT,
            description = "Welcome Bonus credited to wallet",
            idempotencyKey = "tx_key_welcome_01"
        )
        val tx2 = WalletTransactionEntity(
            id = "tx_cashback_01",
            userId = user.id,
            amount = 250.0,
            type = WalletTransactionType.CASHBACK,
            description = "Verified Story Promotion Cashback (500 views)",
            idempotencyKey = "tx_key_cashback_01"
        )
        walletDao().insertTransaction(tx1)
        walletDao().insertTransaction(tx2)

        val pricing = listOf(
            PricingConfigEntity("bike_base_fare", "25.0", "Bike base fare in INR"),
            PricingConfigEntity("bike_per_km", "12.0", "Bike per km fare in INR"),
            PricingConfigEntity("tempo_base_fare", "150.0", "Tempo base fare in INR"),
            PricingConfigEntity("tempo_per_km", "25.0", "Tempo per km fare in INR"),
            PricingConfigEntity("standard_delivery_fee", "30.0", "Standard order delivery fee"),
            PricingConfigEntity("platform_fee", "5.0", "Standard customer platform fee"),
            PricingConfigEntity("commission_percent", "10.0", "Standard platform commission percent")
        )
        pricingConfigDao().insertConfigs(pricing)
    }
}
