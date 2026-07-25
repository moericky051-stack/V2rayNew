package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.VpnServerEntity
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
import java.util.UUID

@Composable
fun ServerListScreen(
    viewModel: MainViewModel,
    onServerSelected: (VpnServerEntity) -> Unit
) {
    val servers by viewModel.servers.collectAsState()
    val selectedServer by viewModel.selectedServer.collectAsState()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var selectedFilter by remember { mutableStateOf("ALL") }
    var showImportDialog by remember { mutableStateOf(false) }

    val filteredServers = remember(servers, selectedFilter) {
        when (selectedFilter) {
            "FREE" -> servers.filter { !it.isPremium }
            "VIP" -> servers.filter { it.isPremium }
            "VLESS" -> servers.filter { it.protocol.equals("VLESS", ignoreCase = true) }
            "VMESS" -> servers.filter { it.protocol.equals("VMess", ignoreCase = true) }
            "TROJAN" -> servers.filter { it.protocol.equals("Trojan", ignoreCase = true) }
            "REALITY" -> servers.filter { it.protocol.equals("Reality", ignoreCase = true) }
            "CUSTOM" -> servers.filter { it.isCustom }
            else -> servers
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "V2RAY SERVERS",
                        color = CyberCyanPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${filteredServers.size} Nodes Available",
                        color = CyberTextSecondary,
                        fontSize = 12.sp
                    )
                }

                Button(
                    onClick = { viewModel.testAllPings() },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCardBg, contentColor = CyberCyanPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .border(1.dp, CyberCardBorder, RoundedCornerShape(12.dp))
                        .testTag("test_pings_button")
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Ping", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Ping All", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Filter Chips Row
            val filters = listOf("ALL", "FREE", "VIP", "VLESS", "VMESS", "TROJAN", "REALITY", "CUSTOM")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(filters) { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyberCyanPrimary,
                            selectedLabelColor = Color.Black,
                            containerColor = CyberCardBg,
                            labelColor = CyberTextSecondary
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.testTag("filter_$filter")
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Server List
            if (filteredServers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Dns,
                            contentDescription = null,
                            tint = CyberTextSecondary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No servers found in this filter category.",
                            color = CyberTextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredServers, key = { it.id }) { server ->
                        val isSelected = selectedServer?.id == server.id
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onServerSelected(server)
                                }
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) CyberCyanPrimary else CyberCardBorder,
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) CyberCardBg.copy(alpha = 0.95f) else CyberCardBg
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (server.isPremium) CyberAmber.copy(alpha = 0.15f) else CyberCyanPrimary.copy(alpha = 0.15f)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = server.countryCode,
                                            color = if (server.isPremium) CyberAmber else CyberCyanPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = server.name,
                                                color = CyberTextPrimary,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            if (server.isPremium) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Icon(
                                                    imageVector = Icons.Default.Star,
                                                    contentDescription = "VIP",
                                                    tint = CyberAmber,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(CyberViolet.copy(alpha = 0.2f))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = server.protocol,
                                                    color = CyberViolet,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "${server.network.uppercase()} | ${server.security.uppercase()}",
                                                color = CyberTextSecondary,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val ping = server.pingMs
                                    val pingColor = when {
                                        ping in 1..80 -> CyberEmerald
                                        ping in 81..150 -> CyberAmber
                                        ping > 150 -> CyberRose
                                        else -> CyberTextSecondary
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = if (ping > 0) "$ping ms" else "--",
                                            color = pingColor,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = server.region,
                                            color = CyberTextSecondary,
                                            fontSize = 10.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    IconButton(
                                        onClick = {
                                            val shareUrl = viewModel.getShareUrl(server)
                                            clipboardManager.setText(AnnotatedString(shareUrl))
                                            Toast.makeText(context, "Config copied to clipboard!", Toast.LENGTH_SHORT).show()
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy Config",
                                            tint = CyberTextSecondary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    if (server.isCustom) {
                                        IconButton(onClick = { viewModel.deleteServer(server) }) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete",
                                                tint = CyberRose,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Floating Action Button to Import Config
        FloatingActionButton(
            onClick = { showImportDialog = true },
            containerColor = CyberCyanPrimary,
            contentColor = Color.Black,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("add_server_fab")
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Server")
        }

        // Import Dialog
        if (showImportDialog) {
            ImportConfigDialog(
                onDismiss = { showImportDialog = false },
                onImportLink = { link ->
                    viewModel.importConfigUrl(link) { success ->
                        if (success) {
                            Toast.makeText(context, "Server imported successfully!", Toast.LENGTH_SHORT).show()
                            showImportDialog = false
                        } else {
                            Toast.makeText(context, "Invalid V2Ray link format!", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                onAddManual = { manualServer ->
                    viewModel.addCustomServer(manualServer)
                    Toast.makeText(context, "Custom server added!", Toast.LENGTH_SHORT).show()
                    showImportDialog = false
                }
            )
        }
    }
}

@Composable
fun ImportConfigDialog(
    onDismiss: () -> Unit,
    onImportLink: (String) -> Unit,
    onAddManual: (VpnServerEntity) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var linkInput by remember { mutableStateOf("") }

    // Manual Form states
    var manualName by remember { mutableStateOf("") }
    var manualAddress by remember { mutableStateOf("") }
    var manualPort by remember { mutableStateOf("443") }
    var manualUuid by remember { mutableStateOf("") }
    var manualProtocol by remember { mutableStateOf("VLESS") }
    var manualNetwork by remember { mutableStateOf("ws") }
    var manualPath by remember { mutableStateOf("/v2ray") }
    var manualSecurity by remember { mutableStateOf("tls") }
    var manualSni by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CyberCardBg,
        title = {
            Text("IMPORT V2RAY CONFIG", color = CyberCyanPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
        text = {
            Column {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = CyberCardBg,
                    contentColor = CyberCyanPrimary
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("URL Link", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("QR Code", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Manual", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                when (selectedTab) {
                    0 -> {
                        OutlinedTextField(
                            value = linkInput,
                            onValueChange = { linkInput = it },
                            label = { Text("vless://, vmess://, trojan:// or ss://", fontSize = 11.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("import_link_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberCyanPrimary,
                                unfocusedBorderColor = CyberCardBorder,
                                focusedLabelColor = CyberCyanPrimary
                            )
                        )
                    }
                    1 -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.QrCode, contentDescription = null, tint = CyberCyanPrimary, modifier = Modifier.size(64.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Simulate Camera QR Code Scanner", color = CyberTextSecondary, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    linkInput = "vless://7c9e6679-7425-40de-944b-e07fc1f90ae7@qr-node.v2ray-secure.com:443?type=ws&security=tls&path=%2Fvless#QR-Scanned-Node"
                                    selectedTab = 0
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberCyanSecondary, contentColor = Color.Black)
                            ) {
                                Text("Scan Sample QR Code Payload", fontSize = 11.sp)
                            }
                        }
                    }
                    2 -> {
                        Column {
                            OutlinedTextField(
                                value = manualName,
                                onValueChange = { manualName = it },
                                label = { Text("Node Name") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = manualAddress,
                                onValueChange = { manualAddress = it },
                                label = { Text("Server Address / IP") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = manualPort,
                                    onValueChange = { manualPort = it },
                                    label = { Text("Port") },
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = manualProtocol,
                                    onValueChange = { manualProtocol = it },
                                    label = { Text("Protocol") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = manualUuid,
                                onValueChange = { manualUuid = it },
                                label = { Text("UUID / Password") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedTab == 2) {
                        val server = VpnServerEntity(
                            id = "custom_" + UUID.randomUUID().toString().take(6),
                            name = if (manualName.isNotBlank()) manualName else "Custom Node",
                            region = "Manual Node",
                            countryCode = "UN",
                            protocol = manualProtocol,
                            address = if (manualAddress.isNotBlank()) manualAddress else "127.0.0.1",
                            port = manualPort.toIntOrNull() ?: 443,
                            uuid = manualUuid,
                            network = manualNetwork,
                            pathOrServiceName = manualPath,
                            security = manualSecurity,
                            sni = manualSni,
                            isCustom = true
                        )
                        onAddManual(server)
                    } else {
                        onImportLink(linkInput)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberCyanPrimary, contentColor = Color.Black),
                modifier = Modifier.testTag("confirm_import_button")
            ) {
                Text("Import Config", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = CyberTextSecondary)
            }
        }
    )
}
