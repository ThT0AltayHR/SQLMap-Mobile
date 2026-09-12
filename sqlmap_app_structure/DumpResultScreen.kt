package com.sqlmap.app.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sqlmap.app.ui.theme.CyberColors
import com.sqlmap.app.ui.viewmodel.DumpResultViewModel

@Composable
fun DumpResultScreen(
    navController: NavController,
    databaseName: String,
    viewModel: DumpResultViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var copyStatus by remember { mutableStateOf<String?>(null) }

    // SAF — kullanıcı istediği klasörü ve ismi seçer
    val saveLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/zip")
    ) { uri: Uri? ->
        if (uri != null && state.zipFile != null) {
            val success = viewModel.writeZipToDestination(state.zipFile!!, uri)
            copyStatus = if (success) "✓ Kopyalandı" else "✗ Kopyalama başarısız"
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberColors.DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .background(CyberColors.CardBackground, RoundedCornerShape(12.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Başlık
            Text(
                text = "Dump Complete",
                fontSize = 20.sp,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
                color = CyberColors.SuccessGreen
            )

            // Database Dump görseli
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(CyberColors.DarkerBackground, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "💾", fontSize = 40.sp)
            }

            // ZIP dosya kartı — tek tıkla SAF açılır
            if (state.zipFile != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CyberColors.DarkerBackground, RoundedCornerShape(8.dp))
                        .border(1.dp, CyberColors.PrimaryOrange, RoundedCornerShape(8.dp))
                        .clickable {
                            // Tek tıklama = kopyalama seçeneği
                            saveLauncher.launch(state.zipFile!!.name)
                        }
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = state.zipFile!!.name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberColors.TextPrimary
                            )
                            Text(
                                text = state.zipSize,
                                fontSize = 10.sp,
                                color = CyberColors.TextSecondary
                            )
                            Text(
                                text = "Tap to copy to a folder",
                                fontSize = 10.sp,
                                color = CyberColors.PrimaryOrange,
                                fontStyle = FontStyle.Italic,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        Icon(
                            imageVector = Icons.Filled.FolderOpen,
                            contentDescription = "Copy",
                            modifier = Modifier.size(28.dp),
                            tint = CyberColors.PrimaryOrange
                        )
                    }
                }

                // Kopyalama status mesajı
                copyStatus?.let {
                    Text(
                        text = it,
                        fontSize = 12.sp,
                        color = if (it.startsWith("✓")) CyberColors.SuccessGreen else CyberColors.ErrorRed
                    )
                }

                // Paylaş butonu
                Button(
                    onClick = {
                        val shareIntent = viewModel.getShareIntent(state.zipFile!!)
                        context.startActivity(shareIntent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberColors.InfoBlue,
                        contentColor = CyberColors.DarkerBackground
                    )
                ) {
                    Icon(
                        Icons.Filled.Share, null,
                        modifier = Modifier.size(16.dp).padding(end = 8.dp)
                    )
                    Text("Share ZIP", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            } else if (state.isLoading) {
                androidx.compose.material3.CircularProgressIndicator(
                    modifier = Modifier.size(40.dp),
                    color = CyberColors.PrimaryOrange,
                    strokeWidth = 3.dp
                )
                Text(
                    text = "Creating ZIP archive...",
                    fontSize = 12.sp,
                    color = CyberColors.TextSecondary
                )
            } else if (state.errorMessage != null) {
                Text(
                    text = "Error: ${state.errorMessage}",
                    fontSize = 12.sp,
                    color = CyberColors.ErrorRed
                )
            }

            // Geri butonu
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyberColors.BorderColor,
                    contentColor = CyberColors.TextPrimary
                )
            ) {
                Text("Back to Dashboard", fontSize = 12.sp)
            }
        }
    }
}
