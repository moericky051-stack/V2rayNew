package com.example.data.model

data class UserAccount(
    val id: String = "user_10294",
    val name: String = "Alex Rivera",
    val email: String = "alex.rivera@v2ray.secure",
    val plan: PlanType = PlanType.FREE,
    val expiryDate: String = "2026-12-31",
    val dataQuotaGb: Double = 50.0,
    val dataUsedGb: Double = 12.4,
    val token: String = "sec_tok_99182371923812",
    val isAdmin: Boolean = false,
    val isDeviceBound: Boolean = true,
    val referralCode: String = "V2RAY-ALEX99"
)

enum class PlanType {
    FREE,
    PREMIUM
}
