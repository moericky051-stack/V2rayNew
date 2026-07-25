package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.PaymentDao
import com.example.data.dao.VpnServerDao
import com.example.data.model.PaymentRequestEntity
import com.example.data.model.VpnServerEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [VpnServerEntity::class, PaymentRequestEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vpnServerDao(): VpnServerDao
    abstract fun paymentDao(): PaymentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "v2ray_vpn_db"
                )
                .addCallback(DatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialServers(database.vpnServerDao())
                        populateInitialPayments(database.paymentDao())
                    }
                }
            }

            suspend fun populateInitialServers(dao: VpnServerDao) {
                val defaultServers = listOf(
                    VpnServerEntity(
                        id = "srv_sg_01",
                        name = "Singapore Ultra-Fast VLESS",
                        region = "Singapore",
                        countryCode = "SG",
                        protocol = "VLESS",
                        address = "sg1.v2ray-secure.com",
                        port = 443,
                        uuid = "7c9e6679-7425-40de-944b-e07fc1f90ae7",
                        network = "ws",
                        pathOrServiceName = "/vless-ws",
                        security = "tls",
                        sni = "sg1.v2ray-secure.com",
                        isPremium = false,
                        pingMs = 38
                    ),
                    VpnServerEntity(
                        id = "srv_jp_01",
                        name = "Tokyo High-Speed VMess",
                        region = "Japan",
                        countryCode = "JP",
                        protocol = "VMess",
                        address = "jp1.v2ray-secure.com",
                        port = 443,
                        uuid = "2a819077-8c4d-44a1-b847-194121aef71a",
                        network = "grpc",
                        pathOrServiceName = "v2ray-grpc",
                        security = "tls",
                        sni = "jp1.v2ray-secure.com",
                        isPremium = false,
                        pingMs = 62
                    ),
                    VpnServerEntity(
                        id = "srv_us_reality",
                        name = "USA Reality Stealth Node",
                        region = "United States",
                        countryCode = "US",
                        protocol = "Reality",
                        address = "us-west.v2ray-secure.com",
                        port = 443,
                        uuid = "b92138a0-2f9a-4c28-98e3-4712028c9f0b",
                        network = "tcp",
                        pathOrServiceName = "",
                        security = "reality",
                        sni = "www.apple.com",
                        publicKey = "q8R27346vNkY281B94n1zMkN3L9",
                        flow = "xtls-rprx-vision",
                        isPremium = true,
                        pingMs = 145
                    ),
                    VpnServerEntity(
                        id = "srv_hk_trojan",
                        name = "Hong Kong VIP Trojan",
                        region = "Hong Kong",
                        countryCode = "HK",
                        protocol = "Trojan",
                        address = "hk1.v2ray-secure.com",
                        port = 443,
                        uuid = "hk_pass_8823192",
                        network = "ws",
                        pathOrServiceName = "/trojan-ws",
                        security = "tls",
                        sni = "hk1.v2ray-secure.com",
                        isPremium = true,
                        pingMs = 28
                    ),
                    VpnServerEntity(
                        id = "srv_de_ss",
                        name = "Germany Shadowsocks 2022",
                        region = "Germany",
                        countryCode = "DE",
                        protocol = "Shadowsocks",
                        address = "de1.v2ray-secure.com",
                        port = 8388,
                        uuid = "2022-blake3-aes-128-gcm:SecretKey998",
                        network = "tcp",
                        security = "none",
                        isPremium = false,
                        pingMs = 180
                    ),
                    VpnServerEntity(
                        id = "srv_mm_vip",
                        name = "Myanmar Gaming Express (Low Ping)",
                        region = "Myanmar VIP",
                        countryCode = "MM",
                        protocol = "VLESS",
                        address = "mm-vip.v2ray-secure.com",
                        port = 443,
                        uuid = "e8293710-1892-4d2b-aa90-092317c8a11b",
                        network = "grpc",
                        pathOrServiceName = "mm-grpc",
                        security = "reality",
                        sni = "www.microsoft.com",
                        publicKey = "8aNkY281B94n1zMkN3L9q8R27346vN",
                        flow = "xtls-rprx-vision",
                        isPremium = true,
                        pingMs = 18
                    )
                )
                dao.insertServers(defaultServers)
            }

            suspend fun populateInitialPayments(dao: PaymentDao) {
                val initialPayment = PaymentRequestEntity(
                    id = "pay_9812",
                    userId = "user_10294",
                    userName = "Alex Rivera",
                    method = "KBZ Pay",
                    planName = "1 Month VIP",
                    amount = "15,000 MMK",
                    transactionId = "202607251029384",
                    status = "APPROVED"
                )
                dao.insertPayment(initialPayment)
            }
        }
    }
}
