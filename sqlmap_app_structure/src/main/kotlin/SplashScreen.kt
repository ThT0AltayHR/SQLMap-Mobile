package com.sqlmap.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.sqlmap.app.R
import com.sqlmap.app.ui.theme.CyberColors
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    var showLogo by remember { mutableStateOf(false) }
    var showProgress by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        delay(500)
        showLogo = true
        delay(1000)
        showProgress = true

        // Simüle edilen yükleme
        for (i in 0..100 step 5) {
            progress = i / 100f
            delay(100)
        }

        delay(500)
        // Sonraki ekrana git
        navController.navigate("welcome") {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberColors.DarkerBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // SQLMap Logosu (Ana ikondan)
            AnimatedVisibility(
                visible = showLogo,
                enter = scaleIn() + fadeIn(),
                exit = fadeOut()
            ) {
                AsyncImage(
                    model = R.drawable.ic_sqlmap_logo,
                    contentDescription = "SQLMap Logo",
                    modifier = Modifier.size(200.dp),
                    contentScale = ContentScale.Fit
                )
            }

            // Uygulama adı
            AnimatedVisibility(
                visible = showLogo,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    text = "SQLMap~",
                    fontSize = 48.sp,
                    fontStyle = FontStyle.Italic,
                    color = CyberColors.PrimaryOrange,
                    modifier = Modifier
                        .padding(top = 24.dp)
                )
            }

            // Progress göstergesi
            AnimatedVisibility(
                visible = showProgress,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 40.dp)
                ) {
                    CircularProgressIndicator(
                        progress = progress,
                        modifier = Modifier.size(50.dp),
                        color = CyberColors.PrimaryOrange,
                        strokeWidth = 3.dp
                    )

                    Text(
                        text = "${(progress * 100).toInt()}%",
                        fontSize = 14.sp,
                        color = CyberColors.TextSecondary,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }
        }
    }
}
