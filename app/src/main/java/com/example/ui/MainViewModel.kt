package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.PaymentRequestEntity
import com.example.data.model.UserAccount
import com.example.data.model.VpnServerEntity
import com.example.data.repository.SpeedMetrics
import com.example.data.repository.VpnConnectionState
import com.example.data.repository.VpnRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val repository = VpnRepository(application)

    val servers: StateFlow<List<VpnServerEntity>> = repository.allServers.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val payments: StateFlow<List<PaymentRequestEntity>> = repository.allPayments.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val connectionState: StateFlow<VpnConnectionState> = repository.connectionState
    val selectedServer: StateFlow<VpnServerEntity?> = repository.selectedServer
    val speedMetrics: StateFlow<SpeedMetrics> = repository.speedMetrics
    val userAccount: StateFlow<UserAccount> = repository.userAccount
    val isAdminMode: StateFlow<Boolean> = repository.isAdminMode

    init {
        viewModelScope.launch {
            servers.collect { serverList ->
                if (selectedServer.value == null && serverList.isNotEmpty()) {
                    repository.selectServer(serverList.first())
                }
            }
        }
    }

    fun selectServer(server: VpnServerEntity) {
        viewModelScope.launch {
            repository.selectServer(server)
        }
    }

    fun toggleVpnConnection() {
        viewModelScope.launch {
            repository.toggleVpnConnection()
        }
    }

    fun testServerPing(serverId: String) {
        viewModelScope.launch {
            repository.testServerPing(serverId)
        }
    }

    fun testAllPings() {
        viewModelScope.launch {
            repository.testAllPings(servers.value)
        }
    }

    fun importConfigUrl(url: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.importConfigUrl(url)
            onResult(success)
        }
    }

    fun addCustomServer(server: VpnServerEntity) {
        viewModelScope.launch {
            repository.addServer(server)
        }
    }

    fun deleteServer(server: VpnServerEntity) {
        viewModelScope.launch {
            repository.deleteServer(server)
        }
    }

    fun submitPayment(method: String, planName: String, amount: String, transactionId: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.submitPaymentRequest(method, planName, amount, transactionId)
            onComplete()
        }
    }

    fun updatePaymentStatus(paymentId: String, status: String) {
        viewModelScope.launch {
            repository.updatePaymentStatus(paymentId, status)
        }
    }

    fun grantRewardData() {
        repository.grantTemporaryVipData(5.0)
    }

    fun toggleAdminMode(enabled: Boolean) {
        repository.toggleAdminMode(enabled)
    }

    fun getShareUrl(server: VpnServerEntity): String {
        return repository.generateV2RayShareUrl(server)
    }
}
