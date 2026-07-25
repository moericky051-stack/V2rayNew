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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberCyanPrimary
import com.example.ui.theme.CyberCyanSecondary
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary

@Composable
fun VpsGuideScreen() {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Terminal, contentDescription = null, tint = CyberCyanPrimary, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "VPS SERVER DEPLOYMENT GUIDE",
                    color = CyberCyanPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Ubuntu 22.04 / 24.04 LTS + Xray Core + 3x-UI Panel",
                    color = CyberTextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Step 1: 3x-UI Panel 1-Click Script
        GuideStepCard(
            stepNumber = "1",
            title = "Install 3x-UI Panel & Xray-Core",
            description = "Run this one-click bash script on your clean Ubuntu VPS to install 3x-UI panel with multi-protocol support.",
            command = "bash <(curl -Ls https://raw.githubusercontent.com/mhsanaei/3x-ui/master/install.sh)",
            onCopy = {
                clipboardManager.setText(AnnotatedString("bash <(curl -Ls https://raw.githubusercontent.com/mhsanaei/3x-ui/master/install.sh)"))
                Toast.makeText(context, "Command copied!", Toast.LENGTH_SHORT).show()
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Step 2: SSL Certificate setup
        GuideStepCard(
            stepNumber = "2",
            title = "Issue Free Let's Encrypt SSL Certificate",
            description = "Generate SSL certificates for your domain to enable TLS and Reality encryption.",
            command = "x-ui cert --domain mydomain.com",
            onCopy = {
                clipboardManager.setText(AnnotatedString("x-ui cert --domain mydomain.com"))
                Toast.makeText(context, "Command copied!", Toast.LENGTH_SHORT).show()
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Step 3: Firewall configuration
        GuideStepCard(
            stepNumber = "3",
            title = "Configure UFW Firewall Ports",
            description = "Allow essential ports for SSH, HTTP, HTTPS, and V2Ray protocols.",
            command = "sudo ufw allow 80/tcp && sudo ufw allow 443/tcp && sudo ufw allow 2053/tcp && sudo ufw enable",
            onCopy = {
                clipboardManager.setText(AnnotatedString("sudo ufw allow 80/tcp && sudo ufw allow 443/tcp && sudo ufw allow 2053/tcp && sudo ufw enable"))
                Toast.makeText(context, "Command copied!", Toast.LENGTH_SHORT).show()
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // System Requirements Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CyberCyanSecondary.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = CyberCardBg)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "RECOMMENDED VPS SPECIFICATIONS",
                    color = CyberAmber,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("• OS: Ubuntu 20.04 / 22.04 / 24.04 LTS (64-bit)", color = CyberTextPrimary, fontSize = 12.sp)
                Text("• CPU: 1 vCPU or higher", color = CyberTextPrimary, fontSize = 12.sp)
                Text("• RAM: 1 GB minimum", color = CyberTextPrimary, fontSize = 12.sp)
                Text("• Bandwidth: 1Gbps Port with unmetered or 1TB+ traffic", color = CyberTextPrimary, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun GuideStepCard(
    stepNumber: String,
    title: String,
    description: String,
    command: String,
    onCopy: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CyberCardBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = CyberCardBg)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CyberCyanPrimary.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "STEP $stepNumber",
                        color = CyberCyanPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(title, color = CyberTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(description, color = CyberTextSecondary, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF0F172A),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = command,
                        color = CyberEmerald,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onCopy, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = CyberCyanPrimary, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}
