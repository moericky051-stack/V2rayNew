package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payment_requests")
data class PaymentRequestEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val userName: String,
    val method: String, // KBZ Pay, Wave Money
    val planName: String, // 1 Month VIP, 3 Months Ultra, 1 Year Unlimited
    val amount: String,
    val transactionId: String,
    val status: String, // PENDING, APPROVED, REJECTED
    val timestamp: Long = System.currentTimeMillis()
)
