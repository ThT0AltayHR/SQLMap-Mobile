package com.sqlmap.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sqlmap.app.ui.theme.CyberColors
import kotlinx.coroutines.delay

@Composable
fun DisclaimerScreen(navController: NavController) {
    var isVisible by remember { mutableStateOf(false) }
    var accepted by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(300)
        isVisible = true
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberColors.DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Başlık
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn()
            ) {
                Text(
                    text = "Legal Disclaimer",
                    fontSize = 28.sp,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    color = CyberColors.PrimaryOrange,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            
            // Disclaimer metni
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(),
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .background(CyberColors.CardBackground, RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    val disclaimerText = """
                        This tool is intended for authorized security testing and educational purposes only.
                        
                        • Unauthorized access to computer systems is illegal
                        • Only use this tool on systems you own or have explicit permission to test
                        • The authors assume no liability for misuse
                        • Users are solely responsible for their actions
                        • Ensure compliance with all applicable laws and regulations
                        
                        By using this application, you acknowledge and agree to this disclaimer.
                    """.trimIndent()
                    
                    Text(
                        text = disclaimerText,
                        fontSize = 13.sp,
                        color = CyberColors.TextPrimary,
                        lineHeight = 18.sp
                    )
                }
            }
            
            // Checkbox + Butonlar
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Kabul checkbox'ı
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = accepted,
                            onCheckedChange = { accepted = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = CyberColors.PrimaryOrange,
                                uncheckedColor = CyberColors.BorderColor
                            )
                        )
                        Text(
                            text = "I accept the disclaimer and acknowledge the risks",
                            fontSize = 13.sp,
                            color = CyberColors.TextPrimary,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    
                    // Butonlar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyberColors.BorderColor,
                                contentColor = CyberColors.TextPrimary
                            )
                        ) {
                            Text("Decline")
                        }
                        
                        Button(
                            onClick = {
                                if (accepted) {
                                    navController.navigate("language_selection")
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (accepted) CyberColors.SuccessGreen else CyberColors.BorderColor,
                                contentColor = CyberColors.DarkerBackground
                            ),
                            enabled = accepted
                        ) {
                            Text("Accept")
                        }
                    }
                }
            }
        }
    }
}
