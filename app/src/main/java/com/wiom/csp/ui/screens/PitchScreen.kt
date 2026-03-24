package com.wiom.csp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wiom.csp.data.OnboardingState
import com.wiom.csp.ui.components.*
import com.wiom.csp.ui.theme.*
import com.wiom.csp.util.t

@Composable
fun PitchScreen(onGetStarted: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(WiomSurface)) {
        // Status bar space
        Column(modifier = Modifier.fillMaxWidth().background(WiomHeader)) {
            Spacer(Modifier.windowInsetsPadding(WindowInsets.statusBars))
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(32.dp))

            // Wiom logo placeholder
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(WiomPrimary),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "W",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                "Wiom Partner+",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = WiomText,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                t(
                    "भारत का सबसे भरोसेमंद इंटरनेट पार्टनर नेटवर्क",
                    "India's Most Trusted Internet Partner Network"
                ),
                fontSize = 16.sp,
                color = WiomTextSec,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
            )

            Spacer(Modifier.height(32.dp))

            // Benefit cards
            val benefits = listOf(
                Triple("\uD83C\uDF10", "अपने एरिया में इंटरनेट सेवा प्रदाता बनें", "Become an Internet Service Provider in your area"),
                Triple("\uD83D\uDCB0", "हर कनेक्शन और रीचार्ज पर कमाई करें", "Earn on every connection and recharge"),
                Triple("\uD83D\uDCDA", "पूरी ट्रेनिंग और निरंतर सपोर्ट", "Complete training and continuous support"),
                Triple("\uD83C\uDFEA", "कम निवेश में अपना खुद का बिजनेस", "Your own business with low investment"),
            )

            benefits.forEach { (emoji, hi, en) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(emoji, fontSize = 28.sp)
                    Text(
                        t(hi, en),
                        fontSize = 15.sp,
                        color = WiomText,
                        lineHeight = 20.sp,
                        modifier = Modifier.weight(1f),
                    )
                }
                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(16.dp))

            // Trust badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            ) {
                TrustBadge("\uD83D\uDD12", t("DOT अनुपालन", "DOT Compliance"))
                TrustBadge("✓", t("TRAI स्वीकृत", "TRAI Approved"))
            }

            Spacer(Modifier.height(24.dp))
        }

        BottomBar {
            WiomButton(
                t("शुरू करें", "Get Started"),
                onClick = {
                    OnboardingState.pitchDismissed = true
                    onGetStarted()
                },
            )
        }
    }
}
