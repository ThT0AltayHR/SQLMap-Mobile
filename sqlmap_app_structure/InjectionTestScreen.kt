package com.sqlmap.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
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
import com.sqlmap.app.ui.viewmodel.InjectionViewModel

@Composable
fun InjectionTestScreen(
    navController: NavController,
    targetUrl: String,
    parameter: String,
    viewModel: InjectionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(targetUrl, parameter) {
        viewModel.startTest(targetUrl, parameter)
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
                    .weight(0.35f)
                    .fillMaxHeight()
                    .background(CyberColors.CardBackground)
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // SQL Injection image placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(CyberColors.DarkerBackground, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "💉", fontSize = 40.sp)
                }

                val headerText = when {
                    state.isTesting -> "INJECTION STARTED!"
                    state.vulnerable -> "VULNERABLE FOUND!"
                    state.isComplete -> "TEST COMPLETE"
                    else -> "READY"
                }

                Text(
                    text = headerText,
                    fontSize = 13.sp,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        state.isTesting -> CyberColors.WarningYellow
                        state.vulnerable -> CyberColors.ErrorRed
                        else -> CyberColors.SuccessGreen
                    },
                    modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
                )

                if (state.isTesting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(50.dp),
                        color = CyberColors.PrimaryOrange,
                        strokeWidth = 3.dp
                    )
                    Text(
                        text = state.currentTechnique,
                        fontSize = 11.sp,
                        color = CyberColors.TextSecondary,
                        modifier = Modifier.padding(top = 12.dp),
                        fontStyle = FontStyle.Italic
                    )
                } else if (state.isComplete) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(
                                if (state.vulnerable) CyberColors.ErrorRed.copy(alpha = 0.2f)
                                else CyberColors.SuccessGreen.copy(alpha = 0.2f),
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (state.vulnerable) Icons.Filled.Close else Icons.Filled.Check,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = if (state.vulnerable) CyberColors.ErrorRed else CyberColors.SuccessGreen
                        )
                    }

                    if (state.vulnerable) {
                        Text(
                            text = "Technique: ${state.foundTechnique}",
                            fontSize = 11.sp,
                            color = CyberColors.TextSecondary,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        if (state.detectedDbms != null) {
                            Text(
                                text = "DBMS: ${state.detectedDbms}",
                                fontSize = 11.sp,
                                color = CyberColors.InfoBlue,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        Button(
                            onClick = {
                                navController.navigate("dump_progress?url=$targetUrl&param=$parameter")
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
                            Text("Dump Database", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Progress Bar
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text(
                        text = "${(state.progress * 100).toInt()}%",
                        fontSize = 10.sp,
                        color = CyberColors.TextSecondary
                    )
                    LinearProgressIndicator(
                        progress = state.progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .padding(top = 4.dp),
                        color = CyberColors.PrimaryOrange,
                        trackColor = CyberColors.BorderColor
                    )
                }

                if (state.errorMessage != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .background(CyberColors.ErrorRed.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = state.errorMessage!!,
                            fontSize = 10.sp,
                            color = CyberColors.ErrorRed
                        )
                    }
                }
            }

            // Sağ Panel - Teknik sonuçları
            Column(
                modifier = Modifier
                    .weight(0.65f)
                    .fillMaxHeight()
                    .background(CyberColors.DarkerBackground)
                    .padding(12.dp)
            ) {
                Text(
                    text = "Injection Techniques",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberColors.PrimaryOrange,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(state.testResults) { result ->
                        AnimatedVisibility(visible = true, enter = fadeIn()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        when (result.isVulnerable) {
                                            true -> CyberColors.ErrorRed.copy(alpha = 0.15f)
                                            false -> CyberColors.SuccessGreen.copy(alpha = 0.15f)
                                            null -> CyberColors.CardBackground
                                        },
                                        RoundedCornerShape(6.dp)
                                    )
                                    .padding(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = result.technique,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CyberColors.TextPrimary
                                        )
                                        Text(
                                            text = result.payload,
                                            fontSize = 9.sp,
                                            color = CyberColors.TextHint,
                                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                        )
                                        if (result.responseTime > 0) {
                                            Text(
                                                text = "${result.responseTime}ms",
                                                fontSize = 9.sp,
                                                color = CyberColors.TextHint
                                            )
                                        }
                                    }

                                    when (result.isVulnerable) {
                                        true -> Icon(
                                            Icons.Filled.Close, null,
                                            modifier = Modifier.size(22.dp),
                                            tint = CyberColors.ErrorRed
                                        )
                                        false -> Icon(
                                            Icons.Filled.Check, null,
                                            modifier = Modifier.size(22.dp),
                                            tint = CyberColors.SuccessGreen
                                        )
                                        null -> CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            color = CyberColors.TextHint,
                                            strokeWidth = 2.dp
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
}
