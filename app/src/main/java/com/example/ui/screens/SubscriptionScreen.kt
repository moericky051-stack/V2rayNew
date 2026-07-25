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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PlanType
import com.example.ui.MainViewModel
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberCyanPrimary
import com.example.ui.theme.CyberCyanSecondary
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.CyberViolet

@Composable
fun SubscriptionScreen(
    viewModel: MainViewModel
) {
    val userAccount by viewModel.userAccount.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var selectedPlan by remember { mutableStateOf("1 Month VIP") }
    var paymentMethod by remember { mutableStateOf("KBZ Pay") }
    var transactionIdInput by remember { mutableStateOf("") }
    var amountInput by remember { mutableStateOf("15,000 MMK") }
    var isSubmitted by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Title
        Text(
            text = "VIP SUBSCRIPTIONS",
            color = CyberCyanPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Unlock Ultra-Fast 1Gbps Servers & Ad-Free Experience",
            color = CyberTextSecondary,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Plan Cards Selection
        val plans = listOf(
            Triple("1 Month VIP", "15,000 MMK", "Unlimited Data | 1Gbps Speed"),
            Triple("3 Months VIP", "40,000 MMK", "Save 12% | VIP Myanmar Nodes"),
            Triple("1 Year Ultra", "120,000 MMK", "Best Value | Priority Support & No Ads")
        )

        plans.forEach { (planName, price, desc) ->
            val isSelected = selectedPlan == planName
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable {
                        selectedPlan = planName
                        amountInput = price
                    }
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) CyberAmber else CyberCardBorder,
                        shape = RoundedCornerShape(16.dp)
                    ),
                colors = CardDefaults.cardColors(containerColor = CyberCardBg)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(planName, color = CyberTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            if (planName.contains("1 Year")) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(shape = RoundedCornerShape(6.dp), color = CyberAmber.copy(alpha = 0.2f)) {
                                    Text("POPULAR", color = CyberAmber, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(desc, color = CyberTextSecondary, fontSize = 12.sp)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(price, color = CyberAmber, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        if (isSelected) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Selected", tint = CyberAmber, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Payment Method Selector
        Text(
            text = "SELECT PAYMENT METHOD",
            color = CyberCyanPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf("KBZ Pay", "Wave Money").forEach { method ->
                val isSelected = paymentMethod == method
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { paymentMethod = method }
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) CyberCyanPrimary else CyberCardBorder,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    colors = CardDefaults.cardColors(containerColor = CyberCardBg)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhoneAndroid,
                            contentDescription = method,
                            tint = if (isSelected) CyberCyanPrimary else CyberTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(method, color = CyberTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Payment Account Info Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CyberCyanSecondary.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = CyberCardBg)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "MERCHANT PAYMENT DETAILS",
                    color = CyberTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$paymentMethod Number: 09-981-289-401",
                    color = CyberCyanPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Account Name: Secure V2Ray Services",
                    color = CyberTextPrimary,
                    fontSize = 13.sp
                )
                Text(
                    text = "Please send $amountInput and paste the Last 6-12 digits Transaction ID below.",
                    color = CyberTextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Transaction ID Form
        OutlinedTextField(
            value = transactionIdInput,
            onValueChange = { transactionIdInput = it },
            label = { Text("Transaction ID / Ref Number", color = CyberTextSecondary) },
            placeholder = { Text("e.g. 202607259928301") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("transaction_id_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CyberCyanPrimary,
                unfocusedBorderColor = CyberCardBorder,
                focusedLabelColor = CyberCyanPrimary
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (transactionIdInput.isBlank()) {
                    Toast.makeText(context, "Please enter Transaction ID!", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.submitPayment(
                        method = paymentMethod,
                        planName = selectedPlan,
                        amount = amountInput,
                        transactionId = transactionIdInput
                    ) {
                        isSubmitted = true
                        Toast.makeText(context, "Payment Submitted! Pending Admin Approval.", Toast.LENGTH_LONG).show()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("submit_payment_button"),
            colors = ButtonDefaults.buttonColors(containerColor = CyberCyanPrimary, contentColor = Color.Black),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Submit Payment for Approval", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        if (isSubmitted) {
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = CyberEmerald.copy(alpha = 0.15f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CyberEmerald)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Your payment request is submitted. It will be verified by our Admin within 5-15 minutes.",
                        color = CyberEmerald,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
