package com.sqlmap.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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

@Composable
fun SettingsScreen(navController: NavController) {
    var proxyEnabled by remember { mutableStateOf(false) }
    var proxyHost by remember { mutableStateOf("") }
    var proxyPort by remember { mutableStateOf("8080") }
    var timeout by remember { mutableStateOf("30") }
    var userAgent by remember { mutableStateOf("Mozilla/5.0") }
    var logLevel by remember { mutableStateOf("INFO") }
    var verifySsl by remember { mutableStateOf(true) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberColors.DarkBackground)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberColors.CardBackground)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = CyberColors.PrimaryOrange
                    )
                }
                
                Text(
                    text = "Settings",
                    fontSize = 18.sp,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    color = CyberColors.PrimaryOrange
                )
                
                Icon(
                    Icons.Filled.Settings,
                    contentDescription = "Settings",
                    tint = CyberColors.PrimaryOrange,
                    modifier = Modifier.fillMaxWidth(0.1f)
                )
            }
            
            // Settings Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    SettingsSection(title = "Network")
                }
                
                item {
                    SettingToggle(
                        label = "Enable Proxy",
                        checked = proxyEnabled,
                        onCheckedChange = { proxyEnabled = it }
                    )
                }
                
                if (proxyEnabled) {
                    item {
                        SettingTextField(
                            label = "Proxy Host",
                            value = proxyHost,
                            onValueChange = { proxyHost = it }
                        )
                    }
                    
                    item {
                        SettingTextField(
                            label = "Proxy Port",
                            value = proxyPort,
                            onValueChange = { proxyPort = it }
                        )
                    }
                }
                
                item {
                    SettingTextField(
                        label = "Timeout (seconds)",
                        value = timeout,
                        onValueChange = { timeout = it }
                    )
                }
                
                item {
                    SettingToggle(
                        label = "Verify SSL Certificate",
                        checked = verifySsl,
                        onCheckedChange = { verifySsl = it }
                    )
                }
                
                item {
                    SettingsSection(title = "Requests")
                }
                
                item {
                    SettingTextField(
                        label = "User-Agent",
                        value = userAgent,
                        onValueChange = { userAgent = it }
                    )
                }
                
                item {
                    SettingsSection(title = "Logging")
                }
                
                item {
                    LogLevelSelector(
                        selected = logLevel,
                        onSelect = { logLevel = it }
                    )
                }
                
                item {
                    SettingsSection(title = "Actions")
                }
                
                item {
                    Button(
                        onClick = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyberColors.InfoBlue,
                            contentColor = CyberColors.DarkerBackground
                        )
                    ) {
                        Text("Clear Cache")
                    }
                }
                
                item {
                    Button(
                        onClick = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyberColors.WarningYellow,
                            contentColor = CyberColors.DarkerBackground
                        )
                    ) {
                        Text("Export Logs")
                    }
                }
                
                item {
                    Button(
                        onClick = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyberColors.ErrorRed,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Clear All Data")
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsSection(title: String) {
    Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = CyberColors.PrimaryOrange,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

@Composable
fun SettingTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(
            text = label,
            fontSize = 11.sp,
            color = CyberColors.TextSecondary,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = CyberColors.CardBackground,
                unfocusedContainerColor = CyberColors.CardBackground,
                focusedTextColor = CyberColors.TextPrimary,
                unfocusedTextColor = CyberColors.TextPrimary,
                focusedIndicatorColor = CyberColors.PrimaryOrange,
                unfocusedIndicatorColor = CyberColors.BorderColor
            ),
            shape = RoundedCornerShape(6.dp)
        )
    }
}

@Composable
fun SettingToggle(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CyberColors.CardBackground, RoundedCornerShape(6.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = CyberColors.TextPrimary
        )
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = CyberColors.PrimaryOrange,
                checkedTrackColor = CyberColors.PrimaryOrange.copy(alpha = 0.5f),
                uncheckedThumbColor = CyberColors.TextHint,
                uncheckedTrackColor = CyberColors.BorderColor
            )
        )
    }
}

@Composable
fun LogLevelSelector(
    selected: String,
    onSelect: (String) -> Unit
) {
    val levels = listOf("DEBUG", "INFO", "WARNING", "ERROR")
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        levels.forEach { level ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (selected == level)
                            CyberColors.PrimaryOrange.copy(alpha = 0.2f)
                        else
                            CyberColors.CardBackground,
                        RoundedCornerShape(6.dp)
                    )
                    .clickable { onSelect(level) }
                    .padding(12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = level,
                    fontSize = 12.sp,
                    color = if (selected == level)
                        CyberColors.PrimaryOrange
                    else
                        CyberColors.TextPrimary
                )
            }
        }
    }
}
