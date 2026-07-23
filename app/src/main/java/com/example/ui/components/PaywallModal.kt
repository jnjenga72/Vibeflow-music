package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PaymentRecord
import com.example.data.model.SubscriptionPlan
import com.example.ui.theme.CardDark
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.MpesaGreen
import com.example.ui.theme.PremiumGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VibeGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaywallModal(
    plans: List<SubscriptionPlan>,
    selectedPlan: SubscriptionPlan?,
    mPesaPhoneNumber: String,
    mPesaStatus: String,
    mPesaCountDown: Int,
    paymentRecords: List<PaymentRecord>,
    onDismiss: () -> Unit,
    onSelectPlan: (SubscriptionPlan) -> Unit,
    onPhoneChange: (String) -> Unit,
    onInitiateMpesa: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedMethodTab by remember { mutableStateOf("M-Pesa") }

    val paymentMethods = listOf("M-Pesa", "Stripe / Card", "Airtel Money", "Google Pay", "Apple Pay", "PayPal")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkBackground,
        modifier = Modifier
            .fillMaxSize()
            .testTag("paywall_modal_sheet")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            item {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(PremiumGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Premium",
                                tint = DarkBackground,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "VibeFlow Premium",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Unlock unlimited downloads & ad-free music",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Feature Highlights Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF2E7D32),
                                    Color(0xFF1B5E20)
                                )
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "⚡ FREE 3-DAY TRIAL INCLUDED",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PremiumGold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Listen offline anywhere. Cancel anytime in one tap.",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Select Subscription Plan",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Subscription Plan Cards
            items(plans) { plan ->
                val isSelected = selectedPlan?.id == plan.id

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) CardDark else DarkSurfaceVariant)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) VibeGreen else Color.White.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { onSelectPlan(plan) }
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = plan.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    if (plan.isPopular) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(PremiumGold)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "MOST POPULAR",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = DarkBackground
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = plan.billingCycle,
                                    fontSize = 12.sp,
                                    color = VibeGreen
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Ksh ${plan.priceKsh}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary
                                )
                            }
                        }

                        if (isSelected) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                plan.features.forEach { feature ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = VibeGreen,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = feature,
                                            fontSize = 12.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Payment Method",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Payment Method Selector Tabs
                ScrollableTabRow(
                    selectedTabIndex = paymentMethods.indexOf(selectedMethodTab).coerceAtLeast(0),
                    containerColor = DarkSurfaceVariant,
                    contentColor = VibeGreen,
                    edgePadding = 0.dp
                ) {
                    paymentMethods.forEach { method ->
                        Tab(
                            selected = selectedMethodTab == method,
                            onClick = { selectedMethodTab = method },
                            text = {
                                Text(
                                    text = method,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedMethodTab == method) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedMethodTab == method) VibeGreen else TextSecondary
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // M-Pesa STK Push Payment Details
                if (selectedMethodTab == "M-Pesa") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CardDark),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.PhoneAndroid,
                                    contentDescription = "M-Pesa",
                                    tint = MpesaGreen,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "M-Pesa Daraja STK Push",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = mPesaPhoneNumber,
                                onValueChange = onPhoneChange,
                                label = { Text("M-Pesa Phone Number (e.g. 254712345678)") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("mpesa_phone_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MpesaGreen,
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                    focusedLabelColor = MpesaGreen,
                                    unfocusedLabelColor = TextMuted,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            if (mPesaStatus == "PROCESSING") {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        color = MpesaGreen,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "STK Push sent to $mPesaPhoneNumber. Please enter M-Pesa PIN ($mPesaCountDown s)...",
                                        fontSize = 12.sp,
                                        color = MpesaGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else if (mPesaStatus == "SUCCESS") {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MpesaGreen.copy(alpha = 0.2f))
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "✓ Payment Received! Premium Activated Successfully.",
                                        color = MpesaGreen,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                Button(
                                    onClick = onInitiateMpesa,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("pay_mpesa_button"),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MpesaGreen,
                                        contentColor = TextPrimary
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "Pay Ksh ${selectedPlan?.priceKsh ?: 199} via M-Pesa STK",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Standard checkout for Stripe / Card / PayPal
                    Button(
                        onClick = {
                            onInitiateMpesa()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VibeGreen,
                            contentColor = DarkBackground
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Checkout Ksh ${selectedPlan?.priceKsh ?: 199} with $selectedMethodTab",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Feature Comparison Table
                Text(
                    text = "Free vs Premium Comparison",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(10.dp))

                ComparisonTable()

                Spacer(modifier = Modifier.height(24.dp))

                // Payment History Section
                if (paymentRecords.isNotEmpty()) {
                    Text(
                        text = "Recent Transactions",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    paymentRecords.forEach { rec ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkSurfaceVariant)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = rec.planName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${rec.paymentMethod} • Ref: ${rec.transactionRef}",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Ksh ${rec.amountKsh}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VibeGreen
                                )
                                Text(
                                    text = rec.status,
                                    fontSize = 10.sp,
                                    color = MpesaGreen
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun ComparisonTable() {
    val items = listOf(
        Triple("Ad-Free Music", false, true),
        Triple("Offline Downloads", false, true),
        Triple("High Quality Audio (320kbps)", false, true),
        Triple("Unlimited Song Skips", false, true),
        Triple("Listen Online", true, true),
        Triple("Playlist Creation", true, true)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurfaceVariant)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("FEATURE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted, modifier = Modifier.weight(1.8f))
            Text("FREE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
            Text("PREMIUM", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VibeGreen, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(8.dp))

        items.forEach { (feat, free, prem) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(feat, fontSize = 12.sp, color = TextPrimary, modifier = Modifier.weight(1.8f))

                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    if (free) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                    } else {
                        Icon(Icons.Default.Close, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                    }
                }

                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    if (prem) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = VibeGreen, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
