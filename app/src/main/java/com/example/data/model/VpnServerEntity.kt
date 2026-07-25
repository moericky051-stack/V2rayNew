package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vpn_servers")
data class VpnServerEntity(
    @PrimaryKey val id: String,
    val name: String,
    val region: String,
    val countryCode: String,
    val protocol: String, // VLESS, VMess, Trojan, Shadowsocks, Reality
    val address: String,
    val port: Int,
    val uuid: String,
    val network: String = "ws", // ws, grpc, tcp
    val pathOrServiceName: String = "/v2ray",
    val security: String = "tls", // tls, reality, none
    val sni: String = "",
    val publicKey: String = "",
    val flow: String = "",
    val isPremium: Boolean = false,
    val isCustom: Boolean = false,
    val pingMs: Int = -1,
    val isOnline: Boolean = true,
    val rawConfigUrl: String = ""
)
