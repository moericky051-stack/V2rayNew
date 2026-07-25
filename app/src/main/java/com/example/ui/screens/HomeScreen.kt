package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.PlanType
import com.example.data.repository.VpnConnectionState
import com.example.ui.MainViewModel
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberCyanPrimary
import com.example.ui.theme.CyberCyanSecondary
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberRose
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.CyberViolet

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToServers: () -> Unit
) {
    val connectionState by viewModel.connectionState.collectAsState()
    val selectedServer by viewModel.selectedServer.collectAsState()
    val metrics by viewModel.speedMetrics.collectAsState()
    val userAccount by viewModel.userAccount.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Banner Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, CyberCardBorder, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = CyberCardBg)
        ) {
            Box {
                Image(
                    painter = painterResource(id = R.drawable.img_vpn_banner),
                    contentDescription = "VPN Banner",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    contentScale = ContentScale.Crop,
                    alpha = 0.35f
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Secure V2Ray VPN",
                                color = CyberCyanPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = CyberCyanPrimary.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "UNLIMITED",
                                    color = CyberCyanPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Remaining Quota: ${String.format("%.1f", userAccount.dataQuotaGb - userAccount.dataUsedGb)} GB / ${userAccount.dataQuotaGb} GB",
                            color = CyberTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Central Shield Connect Button
        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
        val pulseScale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = if (connectionState == VpnConnectionState.CONNECTED) 1.12f else 1.04f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse_scale"
        )

        val shieldColor = when (connectionState) {
            VpnConnectionState.CONNECTED -> CyberEmerald
            VpnConnectionState.CONNECTING -> CyberAmber
            VpnConnectionState.RECONNECTING -> CyberViolet
            VpnConnectionState.DISCONNECTED -> CyberCyanPrimary
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(vertical = 12.dp)
                .size(200.dp)
        ) {
            // Animated Outer Ring
            Box(
                modifier = Modifier
                    .size(190.dp)
                    .scale(if (connectionState == VpnConnectionState.CONNECTED || connectionState == VpnConnectionState.CONNECTING) pulseScale else 1f)
                    .clip(CircleShape)
                    .background(shieldColor.copy(alpha = 0.15f))
                    .border(2.dp, shieldColor.copy(alpha = 0.4f), CircleShape)
            )

            // Button Core
            Surface(
                onClick = { viewModel.toggleVpnConnection() },
                shape = CircleShape,
                color = CyberCardBg,
                shadowElevation = 12.dp,
                modifier = Modifier
                    .size(140.dp)
                    .border(
                        width = 3.dp,
                        brush = Brush.linearGradient(listOf(shieldColor, shieldColor.copy(alpha = 0.5f))),
                        shape = CircleShape
                    )
                    .testTag("vpn_connect_button")
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PowerSettingsNew,
                        contentDescription = "Connect VPN",
                        tint = shieldColor,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = when (connectionState) {
                            VpnConnectionState.CONNECTED -> "DISCONNECT"
                            VpnConnectionState.CONNECTING -> "CONNECTING"
                            VpnConnectionState.RECONNECTING -> "RECONNECTING"
                            VpnConnectionState.DISCONNECTED -> "TAP TO CONNECT"
                        },
                        color = shieldColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Connection Status Label
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = shieldColor.copy(alpha = 0.15f),
            modifier = Modifier.padding(bottom = 20.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(shieldColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when (connectionState) {
                        VpnConnectionState.CONNECTED -> "PROTECTED WITH AES-256 / REALITY"
                        VpnConnectionState.CONNECTING -> "ESTABLISHING XRAY TUNNEL..."
                        VpnConnectionState.RECONNECTING -> "OPTIMIZING ROUTE..."
                        VpnConnectionState.DISCONNECTED -> "UNPROTECTED CONNECTION"
                    },
                    color = shieldColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Speed Meter & Duration Dashboard
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CyberCardBorder, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = CyberCardBg)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "LIVE TRAFFIC MONITOR",
                    color = CyberTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Download
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = CyberEmerald.copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDownward,
                                    contentDescription = "Download",
                                    tint = CyberEmerald,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Download", color = CyberTextSecondary, fontSize = 11.sp)
                            Text(
                                text = if (metrics.downloadKbps > 1024) "${String.format("%.2f", metrics.downloadKbps / 1024.0)} MB/s" else "${metrics.downloadKbps.toInt()} KB/s",
                                color = CyberTextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Upload
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = CyberCyanSecondary.copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.ArrowUpward,
                                    contentDescription = "Upload",
                                    tint = CyberCyanSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Upload", color = CyberTextSecondary, fontSize = 11.sp)
                            Text(
                                text = if (metrics.uploadKbps > 1024) "${String.format("%.2f", metrics.uploadKbps / 1024.0)} MB/s" else "${metrics.uploadKbps.toInt()} KB/s",
                                color = CyberTextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Session Time: ${formatDuration(metrics.sessionDurationSeconds)}",
                        color = CyberTextSecondary,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Data Used: ${String.format("%.2f", metrics.totalDataMb)} MB",
                        color = CyberTextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selected Server Quick Selector Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToServers() }
                .border(1.dp, CyberCyanPrimary.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = CyberCardBg)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = CyberCyanPrimary.copy(alpha = 0.15f),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Dns,
                                contentDescription = "Server Node",
                                tint = CyberCyanPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = selectedServer?.name ?: "Select a VPN Server",
                            color = CyberTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = CyberViolet.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = selectedServer?.protocol ?: "VLESS",
                                    color = CyberViolet,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${selectedServer?.address}:${selectedServer?.port}",
                                color = CyberTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    val ping = selectedServer?.pingMs ?: -1
                    val pingColor = when {
                        ping in 1..80 -> CyberEmerald
                        ping in 81..150 -> CyberAmber
                        ping > 150 -> CyberRose
                        else -> CyberTextSecondary
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = pingColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = if (ping > 0) "${ping} ms" else "-- ms",
                            color = pingColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Change", color = CyberCyanPrimary, fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // AdMob Banner Placeholder & Rewarded Ad Promotion for Free Tier
        if (userAccount.plan == PlanType.FREE) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberAmber.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = CyberCardBg)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PlayCircle, contentDescription = null, tint = CyberAmber, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("FREE REWARDED AD", color = CyberAmber, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF334155)) {
                            Text("AdMob", color = CyberTextSecondary, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Watch a 15-sec video ad to unlock +5GB High-Speed VIP Data Quota!",
                        color = CyberTextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = { viewModel.grantRewardData() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("reward_ad_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberAmber)
                    ) {
                        Icon(Icons.Default.PlayCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Watch Video Ad (+5GB Data)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private fun formatDuration(seconds: Long): String {
    val hrs = seconds / 3600
    val mins = (seconds % 3600) / 60
    val secs = seconds % 60
    return if (hrs > 0) {
        String.format("%02d:%02d:%02d", hrs, mins, secs)
    } else {
        String.format("%02d:%02d", mins, secs)
    }
}
