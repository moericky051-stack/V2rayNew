package com.example.data.repository

import android.content.Context
import android.util.Base64
import com.example.data.dao.PaymentDao
import com.example.data.dao.VpnServerDao
import com.example.data.database.AppDatabase
import com.example.data.model.PaymentRequestEntity
import com.example.data.model.PlanType
import com.example.data.model.UserAccount
import com.example.data.model.VpnServerEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URI
import java.net.URLDecoder
import java.util.UUID
import kotlin.random.Random

enum class VpnConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    RECONNECTING
}

data class SpeedMetrics(
    val downloadKbps: Double = 0.0,
    val uploadKbps: Double = 0.0,
    val sessionDurationSeconds: Long = 0,
    val totalDataMb: Double = 0.0
)

class VpnRepository(context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val serverDao: VpnServerDao = database.vpnServerDao()
    private val paymentDao: PaymentDao = database.paymentDao()

    val allServers: Flow<List<VpnServerEntity>> = serverDao.getAllServers()
    val allPayments: Flow<List<PaymentRequestEntity>> = paymentDao.getAllPayments()

    private val _connectionState = MutableStateFlow(VpnConnectionState.DISCONNECTED)
    val connectionState: StateFlow<VpnConnectionState> = _connectionState.asStateFlow()

    private val _selectedServer = MutableStateFlow<VpnServerEntity?>(null)
    val selectedServer: StateFlow<VpnServerEntity?> = _selectedServer.asStateFlow()

    private val _speedMetrics = MutableStateFlow(SpeedMetrics())
    val speedMetrics: StateFlow<SpeedMetrics> = _speedMetrics.asStateFlow()

    private val _userAccount = MutableStateFlow(UserAccount())
    val userAccount: StateFlow<UserAccount> = _userAccount.asStateFlow()

    private val _isAdminMode = MutableStateFlow(false)
    val isAdminMode: StateFlow<Boolean> = _isAdminMode.asStateFlow()

    suspend fun selectServer(server: VpnServerEntity) {
        _selectedServer.value = server
    }

    suspend fun toggleVpnConnection() {
        when (_connectionState.value) {
            VpnConnectionState.DISCONNECTED -> {
                _connectionState.value = VpnConnectionState.CONNECTING
                delay(1200) // Simulate protocol handshake & socket setup
                _connectionState.value = VpnConnectionState.CONNECTED
            }
            VpnConnectionState.CONNECTED, VpnConnectionState.CONNECTING, VpnConnectionState.RECONNECTING -> {
                _connectionState.value = VpnConnectionState.DISCONNECTED
                _speedMetrics.value = SpeedMetrics()
            }
        }
    }

    fun updateMetrics(metrics: SpeedMetrics) {
        _speedMetrics.value = metrics
    }

    fun toggleAdminMode(enabled: Boolean) {
        _isAdminMode.value = enabled
    }

    suspend fun testServerPing(serverId: String): Int = withContext(Dispatchers.IO) {
        delay(Random.nextLong(200, 600))
        val ping = Random.nextInt(15, 180)
        serverDao.updatePing(serverId, ping)
        return@withContext ping
    }

    suspend fun testAllPings(servers: List<VpnServerEntity>) = withContext(Dispatchers.IO) {
        servers.forEach { server ->
            val ping = Random.nextInt(18, 210)
            serverDao.updatePing(server.id, ping)
        }
    }

    suspend fun addServer(server: VpnServerEntity) = withContext(Dispatchers.IO) {
        serverDao.insertServer(server)
    }

    suspend fun deleteServer(server: VpnServerEntity) = withContext(Dispatchers.IO) {
        serverDao.deleteServer(server)
    }

    suspend fun importConfigUrl(rawUrl: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val trimmed = rawUrl.trim()
            val parsedServer = when {
                trimmed.startsWith("vless://") -> parseVless(trimmed)
                trimmed.startsWith("vmess://") -> parseVmess(trimmed)
                trimmed.startsWith("trojan://") -> parseTrojan(trimmed)
                trimmed.startsWith("ss://") -> parseShadowsocks(trimmed)
                else -> null
            }
            if (parsedServer != null) {
                serverDao.insertServer(parsedServer)
                _selectedServer.value = parsedServer
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun submitPaymentRequest(
        method: String,
        planName: String,
        amount: String,
        transactionId: String
    ) = withContext(Dispatchers.IO) {
        val payment = PaymentRequestEntity(
            id = "pay_" + UUID.randomUUID().toString().take(8),
            userId = _userAccount.value.id,
            userName = _userAccount.value.name,
            method = method,
            planName = planName,
            amount = amount,
            transactionId = transactionId,
            status = "PENDING"
        )
        paymentDao.insertPayment(payment)
    }

    suspend fun updatePaymentStatus(paymentId: String, status: String) = withContext(Dispatchers.IO) {
        paymentDao.updatePaymentStatus(paymentId, status)
        if (status == "APPROVED") {
            _userAccount.value = _userAccount.value.copy(plan = PlanType.PREMIUM)
        }
    }

    fun grantTemporaryVipData(gb: Double) {
        val current = _userAccount.value
        _userAccount.value = current.copy(
            dataQuotaGb = current.dataQuotaGb + gb,
            plan = PlanType.PREMIUM
        )
    }

    private fun parseVless(url: String): VpnServerEntity? {
        // Example: vless://uuid@host:port?type=ws&security=tls&path=%2Fvless#Name
        val uri = URI(url)
        val userInfo = uri.userInfo ?: return null
        val host = uri.host ?: return null
        val port = if (uri.port != -1) uri.port else 443
        val query = parseQuery(uri.rawQuery ?: "")
        val fragment = URLDecoder.decode(uri.fragment ?: "Imported VLESS Node", "UTF-8")

        return VpnServerEntity(
            id = "imp_vless_" + UUID.randomUUID().toString().take(6),
            name = fragment,
            region = "Custom VLESS",
            countryCode = "UN",
            protocol = "VLESS",
            address = host,
            port = port,
            uuid = userInfo,
            network = query["type"] ?: "ws",
            pathOrServiceName = query["path"] ?: "/",
            security = query["security"] ?: "tls",
            sni = query["sni"] ?: host,
            publicKey = query["pbk"] ?: "",
            flow = query["flow"] ?: "",
            isCustom = true,
            rawConfigUrl = url
        )
    }

    private fun parseVmess(url: String): VpnServerEntity? {
        // vmess://base64json
        val base64Data = url.substringAfter("vmess://")
        val decodedJsonStr = String(Base64.decode(base64Data, Base64.DEFAULT), Charsets.UTF_8)
        val json = JSONObject(decodedJsonStr)

        return VpnServerEntity(
            id = "imp_vmess_" + UUID.randomUUID().toString().take(6),
            name = json.optString("ps", "Imported VMess Node"),
            region = "Custom VMess",
            countryCode = "UN",
            protocol = "VMess",
            address = json.optString("add", "127.0.0.1"),
            port = json.optInt("port", 443),
            uuid = json.optString("id", ""),
            network = json.optString("net", "ws"),
            pathOrServiceName = json.optString("path", "/"),
            security = json.optString("tls", "tls"),
            sni = json.optString("sni", json.optString("add")),
            isCustom = true,
            rawConfigUrl = url
        )
    }

    private fun parseTrojan(url: String): VpnServerEntity? {
        val uri = URI(url)
        val userInfo = uri.userInfo ?: return null
        val host = uri.host ?: return null
        val port = if (uri.port != -1) uri.port else 443
        val fragment = URLDecoder.decode(uri.fragment ?: "Imported Trojan Node", "UTF-8")

        return VpnServerEntity(
            id = "imp_trojan_" + UUID.randomUUID().toString().take(6),
            name = fragment,
            region = "Custom Trojan",
            countryCode = "UN",
            protocol = "Trojan",
            address = host,
            port = port,
            uuid = userInfo,
            network = "ws",
            security = "tls",
            sni = host,
            isCustom = true,
            rawConfigUrl = url
        )
    }

    private fun parseShadowsocks(url: String): VpnServerEntity? {
        val uri = URI(url)
        val fragment = URLDecoder.decode(uri.fragment ?: "Imported Shadowsocks Node", "UTF-8")
        val host = uri.host ?: "127.0.0.1"
        val port = if (uri.port != -1) uri.port else 8388

        return VpnServerEntity(
            id = "imp_ss_" + UUID.randomUUID().toString().take(6),
            name = fragment,
            region = "Custom SS",
            countryCode = "UN",
            protocol = "Shadowsocks",
            address = host,
            port = port,
            uuid = uri.userInfo ?: "",
            network = "tcp",
            security = "none",
            isCustom = true,
            rawConfigUrl = url
        )
    }

    private fun parseQuery(query: String): Map<String, String> {
        return query.split("&").mapNotNull {
            val parts = it.split("=")
            if (parts.size == 2) {
                URLDecoder.decode(parts[0], "UTF-8") to URLDecoder.decode(parts[1], "UTF-8")
            } else null
        }.toMap()
    }

    fun generateV2RayShareUrl(server: VpnServerEntity): String {
        return when (server.protocol) {
            "VLESS", "Reality" -> {
                "vless://${server.uuid}@${server.address}:${server.port}?type=${server.network}&security=${server.security}&path=${server.pathOrServiceName}&sni=${server.sni}&pbk=${server.publicKey}&flow=${server.flow}#${server.name}"
            }
            "VMess" -> {
                val json = JSONObject().apply {
                    put("v", "2")
                    put("ps", server.name)
                    put("add", server.address)
                    put("port", server.port)
                    put("id", server.uuid)
                    put("net", server.network)
                    put("path", server.pathOrServiceName)
                    put("tls", server.security)
                    put("sni", server.sni)
                }
                "vmess://" + Base64.encodeToString(json.toString().toByteArray(), Base64.NO_WRAP)
            }
            "Trojan" -> {
                "trojan://${server.uuid}@${server.address}:${server.port}?peer=${server.sni}#${server.name}"
            }
            else -> {
                "ss://${server.uuid}@${server.address}:${server.port}#${server.name}"
            }
        }
    }
}
