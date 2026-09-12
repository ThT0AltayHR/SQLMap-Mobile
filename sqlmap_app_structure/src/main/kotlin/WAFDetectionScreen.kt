package com.sqlmap.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sqlmap.app.ui.theme.CyberColors
import com.sqlmap.app.ui.viewmodel.WAFDetectionViewModel

@Composable
fun WAFDetectionScreen(
    navController: NavController,
    targetUrl: String,
    viewModel: WAFDetectionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(targetUrl) {
        viewModel.startDetection(targetUrl)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberColors.DarkBackground)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Sol Panel
            Column(
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight()
                    .background(CyberColors.CardBackground)
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = "WAF Detection",
                    fontSize = 18.sp,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    color = CyberColors.PrimaryOrange,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (state.isDetecting) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(60.dp),
                            color = CyberColors.PrimaryOrange,
                            strokeWidth = 4.dp
                        )
                        Text(
                            text = state.statusText,
                            fontSize = 13.sp,
                            color = CyberColors.TextSecondary,
                            modifier = Modifier.padding(top = 12.dp),
                            fontStyle = FontStyle.Italic
                        )
                    }
                } else {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInHorizontally()
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .background(
                                        if (state.wafName != null)
                                            CyberColors.ErrorRed.copy(alpha = 0.2f)
                                        else
                                            CyberColors.SuccessGreen.copy(alpha = 0.2f),
                                        RoundedCornerShape(16.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (state.wafName != null)
                                        Icons.Filled.Close else Icons.Filled.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp),
                                    tint = if (state.wafName != null)
                                        CyberColors.ErrorRed else CyberColors.SuccessGreen
                                )
                            }

                            Text(
                                text = state.wafName ?: "No WAF Detected",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberColors.TextPrimary,
                                modifier = Modifier.padding(top = 12.dp)
                            )

                            Text(
                                text = "Confidence: ${state.confidence}%",
                                fontSize = 13.sp,
                                color = CyberColors.TextSecondary,
                                modifier = Modifier.padding(top = 4.dp)
                            )

                            LinearProgressIndicator(
                                progress = state.confidence / 100f,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .padding(top = 8.dp),
                                color = when {
                                    state.confidence > 80 -> CyberColors.ErrorRed
                                    state.confidence > 60 -> CyberColors.WarningYellow
                                    else -> CyberColors.SuccessGreen
                                },
                                trackColor = CyberColors.BorderColor
                            )

                            if (state.wafName != null) {
                                Button(
                                    onClick = {
                                        navController.navigate("injection_test?url=$targetUrl")
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp)
                                        .height(44.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = CyberColors.PrimaryOrange,
                                        contentColor = CyberColors.DarkerBackground
                                    )
                                ) {
                                    Text("Proceed to Injection", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Sağ Panel
            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxHeight()
                    .background(CyberColors.DarkerBackground)
                    .padding(12.dp)
            ) {
                if (!state.isDetecting) {
                    if (state.signatures.isNotEmpty()) {
                        Text(
                            text = "Detected Signatures",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberColors.PrimaryOrange,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.5f)
                                .background(CyberColors.CardBackground, RoundedCornerShape(6.dp))
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(state.signatures) { sig ->
                                Row(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                                    Text("▸ ", color = CyberColors.PrimaryOrange, fontSize = 10.sp)
                                    Text(sig, fontSize = 10.sp, color = CyberColors.TextPrimary)
                                }
                            }
                        }
                    }

                    if (state.bypassStrategies.isNotEmpty()) {
                        Text(
                            text = "Bypass Strategies",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberColors.PrimaryOrange,
                            modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
                        )

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .background(CyberColors.CardBackground, RoundedCornerShape(6.dp))
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(state.bypassStrategies) { strategy ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(CyberColors.DarkerBackground, RoundedCornerShape(4.dp))
                                        .padding(8.dp)
                                ) {
                                    Text("→ ", color = CyberColors.SuccessGreen, fontSize = 10.sp)
                                    Text(strategy, fontSize = 10.sp, color = CyberColors.TextPrimary)
                                }
                            }
                        }
                    }

                    if (state.errorMessage != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CyberColors.ErrorRed.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "Error: ${state.errorMessage}",
                                fontSize = 11.sp,
                                color = CyberColors.ErrorRed
                            )
                        }
                    }
                }
            }
        }
    }
}
