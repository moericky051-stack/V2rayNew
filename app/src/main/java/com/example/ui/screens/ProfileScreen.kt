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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhonelinkLock
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PlanType
import com.example.ui.MainViewModel
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberCyanPrimary
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.CyberViolet

@Composable
fun ProfileScreen(
    viewModel: MainViewModel
) {
    val userAccount by viewModel.userAccount.collectAsState()
    val isAdminMode by viewModel.isAdminMode.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var deviceBindingEnabled by remember { mutableStateOf(userAccount.isDeviceBound) }
    var encryptLocalConfig by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Title
        Text(
            text = "USER PROFILE & SECURITY",
            color = CyberCyanPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Profile Avatar Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CyberCardBorder, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = CyberCardBg)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = CyberCyanPrimary.copy(alpha = 0.2f),
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = CyberCyanPrimary, modifier = Modifier.size(32.dp))
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(userAccount.name, color = CyberTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(userAccount.email, color = CyberTextSecondary, fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(6.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = CyberCyanPrimary.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "ACTIVE PLAN",
                            color = CyberCyanPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Data Usage Quota Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CyberCardBorder, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = CyberCardBg)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Monthly Data Quota", color = CyberTextSecondary, fontSize = 12.sp)
                    Text("${userAccount.dataUsedGb} GB / ${userAccount.dataQuotaGb} GB", color = CyberTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                val progress = (userAccount.dataUsedGb / userAccount.dataQuotaGb).toFloat().coerceIn(0f, 1f)
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = CyberCyanPrimary,
                    trackColor = Color(0xFF1E293B)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Referral Share Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
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
                Column {
                    Text("Referral Code", color = CyberTextSecondary, fontSize = 11.sp)
                    Text(userAccount.referralCode, color = CyberCyanPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Earn +2GB for every friend invited", color = CyberEmerald, fontSize = 11.sp)
                }

                IconButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(userAccount.referralCode))
                        Toast.makeText(context, "Referral code copied!", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = CyberCyanPrimary)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Security Toggles
        Text(
            text = "SECURITY & ADVANCED OPTIONS",
            color = CyberCyanPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Device Binding Option
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
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.PhonelinkLock, contentDescription = null, tint = CyberCyanPrimary, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Device Binding Protection", color = CyberTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Lock subscription token to current HWID", color = CyberTextSecondary, fontSize = 11.sp)
                    }
                }
                Switch(
                    checked = deviceBindingEnabled,
                    onCheckedChange = { deviceBindingEnabled = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = CyberCyanPrimary, checkedTrackColor = CyberCyanPrimary.copy(alpha = 0.3f))
                )
            }
        }

        // Local Config Encryption
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
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = CyberViolet, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Encrypt Local Configurations", color = CyberTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Encrypt AES-256 local database storage", color = CyberTextSecondary, fontSize = 11.sp)
                    }
                }
                Switch(
                    checked = encryptLocalConfig,
                    onCheckedChange = { encryptLocalConfig = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = CyberViolet, checkedTrackColor = CyberViolet.copy(alpha = 0.3f))
                )
            }
        }

        // Admin Mode Toggle
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .border(1.dp, CyberAmber.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = CyberCardBg)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = CyberAmber, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("In-App Admin Mode", color = CyberAmber, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Enable Admin tab for VPS & Payments", color = CyberTextSecondary, fontSize = 11.sp)
                    }
                }
                Switch(
                    checked = isAdminMode,
                    onCheckedChange = { viewModel.toggleAdminMode(it) },
                    colors = SwitchDefaults.colors(checkedThumbColor = CyberAmber, checkedTrackColor = CyberAmber.copy(alpha = 0.3f)),
                    modifier = Modifier.testTag("admin_mode_switch")
                )
            }
        }
    }
}
