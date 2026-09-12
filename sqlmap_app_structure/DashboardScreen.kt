package com.sqlmap.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sqlmap.app.ui.theme.CyberColors
import kotlinx.coroutines.delay

@Composable
fun DashboardScreen(navController: NavController) {
    var targetUrl by remember { mutableStateOf("") }
    var lastTestResult by remember { mutableStateOf("No test result yet") }
    var isLoading by remember { mutableStateOf(false) }
    var wafDetected by remember { mutableStateOf<String?>(null) }
    var injectionVulnerable by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberColors.DarkBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Sol Panel - Kontroller
            Column(
                modifier = Modifier
                    .weight(0.35f)
                    .fillMaxHeight()
                    .background(CyberColors.CardBackground)
                    .padding(12.dp),
                verticalArrangement = Arrangement.Top
            ) {
                // Başlık
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SQLMap~",
                        fontSize = 20.sp,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        color = CyberColors.PrimaryOrange
                    )
                    
                    IconButton(
                        onClick = { navController.navigate("settings") },
                        modifier = Modifier.fillMaxHeight(0.08f)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings",
                            tint = CyberColors.PrimaryOrange
                        )
                    }
                }
                
                // Target URL Input
                Text(
                    text = "Target URL",
                    fontSize = 12.sp,
                    color = CyberColors.TextSecondary,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
                
                // TextField placeholder (Compose'da custom olur)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CyberColors.DarkerBackground, RoundedCornerShape(6.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Enter target URL...",
                        fontSize = 11.sp,
                        color = CyberColors.TextHint
                    )
                }
                
                // Test Buttons
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    item {
                        // WAF Detection Button
                        Button(
                            onClick = { },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(36.dp),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50).copy(alpha = 0.8f),
                                contentColor = Color.White
                            )
                        ) {
                            Text("🔍 Detect WAF", fontSize = 11.sp)
                        }
                    }
                    
                    item {
                        // SQL Injection Test Button
                        Button(
                            onClick = { },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(36.dp),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyberColors.PrimaryOrange,
                                contentColor = CyberColors.DarkerBackground
                            )
                        ) {
                            Text("💉 Test Injection", fontSize = 11.sp)
                        }
                    }
                    
                    item {
                        // Enum Databases Button
                        Button(
                            onClick = { },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(36.dp),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2196F3).copy(alpha = 0.8f),
                                contentColor = Color.White
                            )
                        ) {
                            Text("📊 Enum DBs", fontSize = 11.sp)
                        }
                    }
                    
                    item {
                        // Dump Data Button
                        Button(
                            onClick = { },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(36.dp),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF9534).copy(alpha = 0.8f),
                                contentColor = Color.White
                            )
                        ) {
                            Text("💾 Dump Data", fontSize = 11.sp)
                        }
                    }
                }
                
                // Status Cards
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    item {
                        StatusCard(
                            title = "WAF Status",
                            value = wafDetected ?: "Not Detected",
                            color = if (wafDetected != null) CyberColors.ErrorRed else CyberColors.SuccessGreen
                        )
                    }
                    
                    item {
                        StatusCard(
                            title = "Injection",
                            value = if (injectionVulnerable) "VULNERABLE" else "Safe",
                            color = if (injectionVulnerable) CyberColors.ErrorRed else CyberColors.SuccessGreen
                        )
                    }
                }
            }
            
            // Sağ Panel - Logs ve Sonuçlar
            Column(
                modifier = Modifier
                    .weight(0.65f)
                    .fillMaxHeight()
                    .background(CyberColors.DarkerBackground)
                    .padding(12.dp),
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = "Test Results & Logs",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberColors.PrimaryOrange,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                // Log Viewer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(CyberColors.CardBackground, RoundedCornerShape(6.dp))
                        .padding(8.dp)
                ) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        item {
                            Text(
                                text = lastTestResult,
                                fontSize = 10.sp,
                                color = CyberColors.TextPrimary,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            )
                        }
                        
                        items(20) { index ->
                            Text(
                                text = "[${System.currentTimeMillis()}] Log line $index",
                                fontSize = 9.sp,
                                color = CyberColors.TextSecondary,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusCard(
    title: String,
    value: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(CyberColors.DarkerBackground, RoundedCornerShape(6.dp))
            .padding(8.dp)
    ) {
        Column {
            Text(
                text = title,
                fontSize = 10.sp,
                color = CyberColors.TextSecondary
            )
            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}
