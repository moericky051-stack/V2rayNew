package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Speed
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberCyanPrimary
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberRose
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.CyberViolet

@Composable
fun AdminPanelScreen(
    viewModel: MainViewModel
) {
    val servers by viewModel.servers.collectAsState()
    val payments by viewModel.payments.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Title Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = CyberCyanPrimary, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "ADMIN CONTROL PANEL",
                    color = CyberCyanPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Manage VPS Servers, Subscriptions & Payments",
                    color = CyberTextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Stats Dashboard
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AdminStatCard("Total Users", "1,420", Icons.Default.Group, CyberCyanPrimary, Modifier.weight(1f))
            AdminStatCard("Active VPN", "384", Icons.Default.Speed, CyberEmerald, Modifier.weight(1f))
            AdminStatCard("VPS Nodes", "${servers.size}", Icons.Default.Dns, CyberViolet, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Pending Payment Approval Queue
        Text(
            text = "PENDING PAYMENT APPROVALS",
            color = CyberCyanPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (payments.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberCardBorder, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = CyberCardBg)
            ) {
                Text(
                    text = "No pending payment requests.",
                    color = CyberTextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            payments.forEach { pay ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .border(1.dp, CyberCardBorder, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = CyberCardBg)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(pay.userName, color = CyberTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(shape = RoundedCornerShape(4.dp), color = CyberViolet.copy(alpha = 0.2f)) {
                                    Text(pay.method, color = CyberViolet, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Plan: ${pay.planName} | Amount: ${pay.amount}", color = CyberTextSecondary, fontSize = 11.sp)
                            Text("TXID: ${pay.transactionId}", color = CyberCyanPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        if (pay.status == "PENDING") {
                            Row {
                                IconButton(
                                    onClick = {
                                        viewModel.updatePaymentStatus(pay.id, "APPROVED")
                                        Toast.makeText(context, "Payment Approved!", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = "Approve", tint = CyberEmerald)
                                }
                                IconButton(
                                    onClick = {
                                        viewModel.updatePaymentStatus(pay.id, "REJECTED")
                                        Toast.makeText(context, "Payment Rejected!", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Reject", tint = CyberRose)
                                }
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (pay.status == "APPROVED") CyberEmerald.copy(alpha = 0.2f) else CyberRose.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = pay.status,
                                    color = if (pay.status == "APPROVED") CyberEmerald else CyberRose,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // VPS Servers Management Table
        Text(
            text = "VPS SERVER LIST MANAGEMENT",
            color = CyberCyanPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        servers.forEach { server ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .border(1.dp, CyberCardBorder, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = CyberCardBg)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(server.name, color = CyberTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("${server.address}:${server.port} (${server.protocol})", color = CyberTextSecondary, fontSize = 11.sp)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (server.isOnline) CyberEmerald.copy(alpha = 0.2f) else CyberRose.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = if (server.isOnline) "ONLINE" else "OFFLINE",
                                color = if (server.isOnline) CyberEmerald else CyberRose,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminStatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.border(1.dp, CyberCardBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = CyberCardBg)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = CyberTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(title, color = CyberTextSecondary, fontSize = 10.sp)
        }
    }
}
