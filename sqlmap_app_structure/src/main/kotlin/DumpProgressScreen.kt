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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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

data class DumpTableProgress(
    val tableName: String,
    val totalRecords: Int,
    val dumpedRecords: Int,
    val isComplete: Boolean = false
)

@Composable
fun DumpProgressScreen(
    navController: NavController,
    database: String = "users_db"
) {
    var overallProgress by remember { mutableStateOf(0f) }
    var currentTable by remember { mutableStateOf("users") }
    var tables by remember { mutableStateOf<List<DumpTableProgress>>(emptyList()) }
    var isDumping by remember { mutableStateOf(true) }
    var fileSize by remember { mutableStateOf("0 KB") }
    
    val allTables = listOf(
        DumpTableProgress("users", 1523, 0),
        DumpTableProgress("products", 3421, 0),
        DumpTableProgress("orders", 5632, 0),
        DumpTableProgress("payments", 2145, 0),
        DumpTableProgress("sessions", 8932, 0)
    )
    
    LaunchedEffect(Unit) {
        for ((index, table) in allTables.withIndex()) {
            currentTable = table.tableName
            
            // Simüle edilen dump
            for (record in 0..table.totalRecords step (table.totalRecords / 20)) {
                delay(100)
                
                val updatedTables = tables.toMutableList()
                if (index < updatedTables.size) {
                    updatedTables[index] = updatedTables[index].copy(dumpedRecords = record)
                }
                tables = updatedTables
                
                // Calculate overall progress
                val totalDumped = updatedTables.sumOf { it.dumpedRecords }
                val totalRecords = updatedTables.sumOf { it.totalRecords }
                overallProgress = (totalDumped.toFloat() / totalRecords).coerceIn(0f, 1f)
                
                // Update file size
                fileSize = "${(totalDumped * 0.5).toInt()} KB"
            }
            
            delay(500)
            
            // Mark table as complete
            val updatedTables = tables.toMutableList()
            if (index < updatedTables.size) {
                updatedTables[index] = updatedTables[index].copy(
                    dumpedRecords = table.totalRecords,
                    isComplete = true
                )
            } else {
                updatedTables.add(table.copy(
                    dumpedRecords = table.totalRecords,
                    isComplete = true
                ))
            }
            tables = updatedTables
        }
        
        overallProgress = 1f
        isDumping = false
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
            // Sol Panel - Dump Status
            Column(
                modifier = Modifier
                    .weight(0.35f)
                    .fillMaxHeight()
                    .background(CyberColors.CardBackground)
                    .padding(12.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Database Dump Görseli
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(CyberColors.DarkerBackground, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "💾",
                        fontSize = 40.sp
                    )
                }
                
                // Başlık
                Text(
                    text = "DATABASE DUMP",
                    fontSize = 14.sp,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    color = CyberColors.PrimaryOrange,
                    modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
                )
                
                // Database Name
                Text(
                    text = database,
                    fontSize = 12.sp,
                    color = CyberColors.TextSecondary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                // Overall Progress
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${(overallProgress * 100).toInt()}%",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberColors.PrimaryOrange
                    )
                    
                    LinearProgressIndicator(
                        progress = overallProgress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .padding(top = 8.dp),
                        color = CyberColors.PrimaryOrange,
                        trackColor = CyberColors.BorderColor
                    )
                }
                
                // File Size
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .background(CyberColors.DarkerBackground, RoundedCornerShape(6.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Dumped Size",
                        fontSize = 10.sp,
                        color = CyberColors.TextHint
                    )
                    
                    Text(
                        text = fileSize,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberColors.SuccessGreen
                    )
                }
                
                // Current Table
                if (isDumping) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .background(CyberColors.DarkerBackground, RoundedCornerShape(6.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Current Table",
                            fontSize = 10.sp,
                            color = CyberColors.TextHint
                        )
                        
                        Text(
                            text = currentTable,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberColors.InfoBlue,
                            fontStyle = FontStyle.Italic
                        )
                    }
                }
            }
            
            // Sağ Panel - Tablo Listesi
            Column(
                modifier = Modifier
                    .weight(0.65f)
                    .fillMaxHeight()
                    .background(CyberColors.DarkerBackground)
                    .padding(12.dp),
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = "Table Progress",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberColors.PrimaryOrange,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(allTables) { table ->
                        val currentProgress = tables.find { it.tableName == table.tableName }
                        
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn()
                        ) {
                            DumpTableProgressItem(
                                table = currentProgress ?: table,
                                isCurrentTable = currentTable == table.tableName
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DumpTableProgressItem(
    table: DumpTableProgress,
    isCurrentTable: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isCurrentTable) CyberColors.InfoBlue.copy(alpha = 0.1f)
                else if (table.isComplete) CyberColors.SuccessGreen.copy(alpha = 0.1f)
                else CyberColors.CardBackground,
                RoundedCornerShape(6.dp)
            )
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = table.tableName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberColors.TextPrimary
                    )
                    
                    Text(
                        text = "${table.dumpedRecords} / ${table.totalRecords} records",
                        fontSize = 10.sp,
                        color = CyberColors.TextSecondary
                    )
                }
                
                // Status Icon
                if (table.isComplete) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Complete",
                        modifier = Modifier.size(24.dp),
                        tint = CyberColors.SuccessGreen
                    )
                }
            }
            
            // Progress Bar
            if (table.totalRecords > 0) {
                val progress = (table.dumpedRecords.toFloat() / table.totalRecords).coerceIn(0f, 1f)
                
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .padding(top = 6.dp),
                    color = if (table.isComplete) CyberColors.SuccessGreen else CyberColors.PrimaryOrange,
                    trackColor = CyberColors.BorderColor
                )
                
                Text(
                    text = "${(progress * 100).toInt()}%",
                    fontSize = 9.sp,
                    color = CyberColors.TextHint,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
