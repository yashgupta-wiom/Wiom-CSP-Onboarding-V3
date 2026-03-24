package com.wiom.csp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wiom.csp.ui.theme.*
import com.wiom.csp.util.t

@Composable
fun PitchScreen(onGetStarted: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WiomSurface),
    ) {
        // Status bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
                .background(WiomHeader)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("9:41", color = WiomSurface, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            Text("100%", color = WiomSurface, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
        }

        // Content
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Wiom logo area
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(WiomPrimary),
                contentAlignment = Alignment.Center,
            ) {
                Text("W", color = WiomSurface, fontSize = 36.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Wiom Partner+",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = WiomPrimary,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = t(
                    "\u092D\u093E\u0930\u0924 \u0915\u093E \u0938\u092C\u0938\u0947 \u092D\u0930\u094B\u0938\u0947\u092E\u0902\u0926 \u0907\u0902\u091F\u0930\u0928\u0947\u091F \u092A\u093E\u0930\u094D\u091F\u0928\u0930 \u0928\u0947\u091F\u0935\u0930\u094D\u0915",
                    "India\u2019s most trusted internet partner network"
                ),
                fontSize = 14.sp,
                color = WiomTextSec,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Core CSP benefits (no business values)
            PitchBenefitRow("\uD83C\uDF10", t("\u0905\u092A\u0928\u0947 \u090F\u0930\u093F\u092F\u093E \u092E\u0947\u0902 \u0907\u0902\u091F\u0930\u0928\u0947\u091F \u0938\u0947\u0935\u093E \u092A\u094D\u0930\u0926\u093E\u0924\u093E \u092C\u0928\u0947\u0902", "Become an internet service provider in your area"))
            PitchBenefitRow("\uD83D\uDCB0", t("\u0939\u0930 \u0915\u0928\u0947\u0915\u094D\u0936\u0928 \u0914\u0930 \u0930\u0940\u091A\u093E\u0930\u094D\u091C \u092A\u0930 \u0915\u092E\u093E\u0908 \u0915\u0930\u0947\u0902", "Earn on every connection & recharge"))
            PitchBenefitRow("\uD83C\uDF93", t("\u092A\u0942\u0930\u0940 \u091F\u094D\u0930\u0947\u0928\u093F\u0902\u0917 \u0914\u0930 \u0928\u093F\u0930\u0902\u0924\u0930 \u0938\u092A\u094B\u0930\u094D\u091F", "Complete training & ongoing support"))
            PitchBenefitRow("\uD83D\uDE80", t("\u0915\u092E \u0928\u093F\u0935\u0947\u0936 \u092E\u0947\u0902 \u0905\u092A\u0928\u093E \u0916\u0941\u0926 \u0915\u093E \u092C\u093F\u091C\u0928\u0947\u0938", "Your own business with low investment"))

            Spacer(modifier = Modifier.height(24.dp))

            // Trust badges
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "\uD83D\uDD12 ${t("DOT \u0905\u0928\u0941\u092A\u093E\u0932\u0928", "DOT Compliant")}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = WiomPositive,
                    modifier = Modifier
                        .clip(RoundedCornerShape(888.dp))
                        .background(WiomPositive100)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "\u2713 ${t("TRAI \u0938\u094D\u0935\u0940\u0915\u0943\u0924", "TRAI Approved")}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = WiomPositive,
                    modifier = Modifier
                        .clip(RoundedCornerShape(888.dp))
                        .background(WiomPositive100)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                )
            }
        }

        // Bottom CTA
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Button(
                onClick = onGetStarted,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = WiomPrimary),
            ) {
                Text(
                    text = t("\u0936\u0941\u0930\u0942 \u0915\u0930\u0947\u0902", "Get Started"),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
fun PitchBenefitRow(emoji: String, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(WiomBgSec)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(emoji, fontSize = 20.sp)
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = WiomText)
    }
}
