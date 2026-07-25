package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.VpnServerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VpnServerDao {
    @Query("SELECT * FROM vpn_servers ORDER BY isPremium ASC, name ASC")
    fun getAllServers(): Flow<List<VpnServerEntity>>

    @Query("SELECT * FROM vpn_servers WHERE id = :id")
    suspend fun getServerById(id: String): VpnServerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServer(server: VpnServerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServers(servers: List<VpnServerEntity>)

    @Update
    suspend fun updateServer(server: VpnServerEntity)

    @Delete
    suspend fun deleteServer(server: VpnServerEntity)

    @Query("DELETE FROM vpn_servers")
    suspend fun clearServers()

    @Query("UPDATE vpn_servers SET pingMs = :pingMs WHERE id = :id")
    suspend fun updatePing(id: String, pingMs: Int)
}
