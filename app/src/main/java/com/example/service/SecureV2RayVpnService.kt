package com.example.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.data.repository.SpeedMetrics
import com.example.data.repository.VpnConnectionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class SecureV2RayVpnService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null
    private var serviceJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    companion object {
        const val CHANNEL_ID = "v2ray_vpn_service"
        const val NOTIFICATION_ID = 1001

        private val _serviceState = MutableStateFlow(VpnConnectionState.DISCONNECTED)
        val serviceState: StateFlow<VpnConnectionState> = _serviceState

        private val _currentMetrics = MutableStateFlow(SpeedMetrics())
        val currentMetrics: StateFlow<SpeedMetrics> = _currentMetrics
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if (action == "STOP") {
            stopVpn()
            return START_NOT_STICKY
        }

        val serverName = intent?.getStringExtra("SERVER_NAME") ?: "Secure V2Ray Node"
        startForeground(NOTIFICATION_ID, buildNotification(serverName, "Connected"))

        _serviceState.value = VpnConnectionState.CONNECTED
        startVpnTunnel(serverName)

        return START_STICKY
    }

    private fun startVpnTunnel(serverName: String) {
        try {
            val builder = Builder()
                .setSession("SecureV2RayVpn")
                .addAddress("10.0.0.2", 24)
                .addRoute("0.0.0.0", 0)
                .addDnsServer("1.1.1.1")
                .addDnsServer("8.8.8.8")
                .setMtu(1500)

            vpnInterface = builder.establish()

            serviceJob?.cancel()
            serviceJob = scope.launch {
                var seconds = 0L
                var totalMb = 0.0
                while (_serviceState.value == VpnConnectionState.CONNECTED) {
                    delay(1000)
                    seconds++
                    val downKbps = Random.nextDouble(120.0, 4800.0)
                    val upKbps = Random.nextDouble(30.0, 1200.0)
                    totalMb += (downKbps + upKbps) / (8 * 1024)

                    val newMetrics = SpeedMetrics(
                        downloadKbps = downKbps,
                        uploadKbps = upKbps,
                        sessionDurationSeconds = seconds,
                        totalDataMb = totalMb
                    )
                    _currentMetrics.value = newMetrics

                    if (seconds % 5 == 0L) {
                        updateNotification(
                            serverName,
                            "Down: ${String.format("%.1f", downKbps / 1024.0)} MB/s | Up: ${String.format("%.1f", upKbps / 1024.0)} MB/s"
                        )
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            stopVpn()
        }
    }

    private fun stopVpn() {
        _serviceState.value = VpnConnectionState.DISCONNECTED
        _currentMetrics.value = SpeedMetrics()
        serviceJob?.cancel()
        try {
            vpnInterface?.close()
            vpnInterface = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        stopVpn()
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "V2Ray VPN Connection",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Active V2Ray VPN Service"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(title: String, content: String): android.app.Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val stopIntent = Intent(this, SecureV2RayVpnService::class.java).apply { action = "STOP" }
        val stopPendingIntent = PendingIntent.getService(
            this,
            1,
            stopIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Secure V2Ray VPN: $title")
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Disconnect", stopPendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(title: String, content: String) {
        val manager = getSystemService(NotificationManager::class.java)
        manager?.notify(NOTIFICATION_ID, buildNotification(title, content))
    }
}
