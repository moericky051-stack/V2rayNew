package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.PaymentRequestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payment_requests ORDER BY timestamp DESC")
    fun getAllPayments(): Flow<List<PaymentRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentRequestEntity)

    @Query("UPDATE payment_requests SET status = :status WHERE id = :id")
    suspend fun updatePaymentStatus(id: String, status: String)
}
