package com.sqlmap.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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

data class Language(
    val name: String,
    val code: String,
    val flag: String,
    val nativeName: String
)

@Composable
fun LanguageSelectionScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var isVisible by remember { mutableStateOf(false) }
    
    val languages = listOf(
        Language("Afrikaans", "af", "🇿🇦", "Afrikaans"),
        Language("Albanian", "sq", "🇦🇱", "Shqip"),
        Language("Amharic", "am", "🇪🇹", "አማርኛ"),
        Language("Arabic", "ar", "🇸🇦", "العربية"),
        Language("Armenian", "hy", "🇦🇲", "Հայերեն"),
        Language("Azerbaijani", "az", "🇦🇿", "Azərbaycanca"),
        Language("Basque", "eu", "🇪🇸", "Euskara"),
        Language("Belarusian", "be", "🇧🇾", "Беларусь"),
        Language("Bengali", "bn", "🇧🇩", "বাংলা"),
        Language("Bosnian", "bs", "🇧🇦", "Bosanski"),
        Language("Bulgarian", "bg", "🇧🇬", "Български"),
        Language("Catalan", "ca", "🇪🇸", "Català"),
        Language("Cebuano", "ceb", "🇵🇭", "Cebuano"),
        Language("Chinese (Simplified)", "zh-CN", "🇨🇳", "中文(简体)"),
        Language("Chinese (Traditional)", "zh-TW", "🇹🇼", "中文(繁體)"),
        Language("Croatian", "hr", "🇭🇷", "Hrvatski"),
        Language("Czech", "cs", "🇨🇿", "Čeština"),
        Language("Danish", "da", "🇩🇰", "Dansk"),
        Language("Dutch", "nl", "🇳🇱", "Nederlands"),
        Language("English", "en", "🇬🇧", "English"),
        Language("Esperanto", "eo", "🌍", "Esperanto"),
        Language("Estonian", "et", "🇪🇪", "Eesti"),
        Language("Finnish", "fi", "🇫🇮", "Suomi"),
        Language("French", "fr", "🇫🇷", "Français"),
        Language("Galician", "gl", "🇪🇸", "Galego"),
        Language("Georgian", "ka", "🇬🇪", "ქართული"),
        Language("German", "de", "🇩🇪", "Deutsch"),
        Language("Greek", "el", "🇬🇷", "Ελληνικά"),
        Language("Gujarati", "gu", "🇮🇳", "ગુજરાતી"),
        Language("Haitian Creole", "ht", "🇭🇹", "Kreyòl Ayisyen"),
        Language("Hebrew", "he", "🇮🇱", "עברית"),
        Language("Hindi", "hi", "🇮🇳", "हिन्दी"),
        Language("Hungarian", "hu", "🇭🇺", "Magyar"),
        Language("Icelandic", "is", "🇮🇸", "Íslenska"),
        Language("Indonesian", "id", "🇮🇩", "Bahasa Indonesia"),
        Language("Irish", "ga", "🇮🇪", "Gaeilge"),
        Language("Italian", "it", "🇮🇹", "Italiano"),
        Language("Japanese", "ja", "🇯🇵", "日本語"),
        Language("Javanese", "jv", "🇮🇩", "Basa Jawa"),
        Language("Kannada", "kn", "🇮🇳", "ಕನ್ನಡ"),
        Language("Kazakh", "kk", "🇰🇿", "Қазақ"),
        Language("Khmer", "km", "🇰🇭", "ខ្មែរ"),
        Language("Kinyarwanda", "rw", "🇷🇼", "Kinyarwanda"),
        Language("Korean", "ko", "🇰🇷", "한국어"),
        Language("Kurdish", "ku", "🇮🇷", "کوردی"),
        Language("Kyrgyz", "ky", "🇰🇬", "Кыргызча"),
        Language("Lao", "lo", "🇱🇦", "ລາວ"),
        Language("Latin", "la", "🇻🇦", "Latīna"),
        Language("Latvian", "lv", "🇱🇻", "Latviešu"),
        Language("Lithuanian", "lt", "🇱🇹", "Lietuvių"),
        Language("Luxembourgish", "lb", "🇱🇺", "Lëtzebuergesch"),
        Language("Macedonian", "mk", "🇲🇰", "Македонски"),
        Language("Malagasy", "mg", "🇲🇬", "Malagasy"),
        Language("Malay", "ms", "🇲🇾", "Bahasa Melayu"),
        Language("Malayalam", "ml", "🇮🇳", "മലയാളം"),
        Language("Maltese", "mt", "🇲🇹", "Malti"),
        Language("Marathi", "mr", "🇮🇳", "मराठी"),
        Language("Mongolian", "mn", "🇲🇳", "Монгол"),
        Language("Nepali", "ne", "🇳🇵", "नेपाली"),
        Language("Norwegian", "no", "🇳🇴", "Norsk"),
        Language("Odia", "or", "🇮🇳", "ଓଡ଼ିଆ"),
        Language("Pashto", "ps", "🇦🇫", "پښتو"),
        Language("Persian", "fa", "🇮🇷", "فارسی"),
        Language("Polish", "pl", "🇵🇱", "Polski"),
        Language("Portuguese", "pt", "🇵🇹", "Português"),
        Language("Punjabi", "pa", "🇮🇳", "ਪੰਜਾਬੀ"),
        Language("Romanian", "ro", "🇷🇴", "Română"),
        Language("Russian", "ru", "🇷🇺", "Русский"),
        Language("Sanskrit", "sa", "🇮🇳", "संस्कृतम्"),
        Language("Serbian", "sr", "🇷🇸", "Српски"),
        Language("Sindhi", "sd", "🇵🇰", "سنڌي"),
        Language("Sinhala", "si", "🇱🇰", "සිංහල"),
        Language("Slovak", "sk", "🇸🇰", "Slovenčina"),
        Language("Slovenian", "sl", "🇸🇮", "Slovenščina"),
        Language("Somali", "so", "🇸🇴", "Soomaali"),
        Language("Spanish", "es", "🇪🇸", "Español"),
        Language("Sundanese", "su", "🇮🇩", "Basa Sunda"),
        Language("Swahili", "sw", "🇰🇪", "Swahili"),
        Language("Swedish", "sv", "🇸🇪", "Svenska"),
        Language("Tajik", "tg", "🇹🇯", "Тоҷикӣ"),
        Language("Tamil", "ta", "🇮🇳", "தமிழ்"),
        Language("Tatar", "tt", "🇷🇺", "Татар"),
        Language("Telugu", "te", "🇮🇳", "తెలుగు"),
        Language("Thai", "th", "🇹🇭", "ไทย"),
        Language("Tigrinya", "ti", "🇪🇷", "ትግርኛ"),
        Language("Turkish", "tr", "🇹🇷", "Türkçe"),
        Language("Turkmen", "tk", "🇹🇲", "Türkmen"),
        Language("Twi", "tw", "🇬🇭", "Twi"),
        Language("Ukrainian", "uk", "🇺🇦", "Українська"),
        Language("Urdu", "ur", "🇵🇰", "اردو"),
        Language("Uyghur", "ug", "🇨🇳", "ئۇيغۇرچە"),
        Language("Uzbek", "uz", "🇺🇿", "Oʻzbekcha"),
        Language("Vietnamese", "vi", "🇻🇳", "Tiếng Việt"),
        Language("Welsh", "cy", "🇬🇧", "Cymraeg"),
        Language("Xhosa", "xh", "🇿🇦", "Xhosa"),
        Language("Yiddish", "yi", "🇮🇱", "ייִדיש"),
        Language("Yoruba", "yo", "🇳🇬", "Yorùbá"),
        Language("Zulu", "zu", "🇿🇦", "Zulu")
    )
    
    val filteredLanguages = if (searchQuery.isEmpty()) {
        languages
    } else {
        languages.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.nativeName.contains(searchQuery, ignoreCase = true)
        }
    }
    
    LaunchedEffect(Unit) {
        delay(300)
        isVisible = true
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberColors.DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Başlık
            Text(
                text = "Select Language",
                fontSize = 28.sp,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
                color = CyberColors.PrimaryOrange,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Arama kutusu
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                placeholder = { Text("Search language...", color = CyberColors.TextHint) },
                leadingIcon = {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = CyberColors.PrimaryOrange
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = CyberColors.CardBackground,
                    unfocusedContainerColor = CyberColors.CardBackground,
                    focusedTextColor = CyberColors.TextPrimary,
                    unfocusedTextColor = CyberColors.TextPrimary,
                    focusedIndicatorColor = CyberColors.PrimaryOrange,
                    unfocusedIndicatorColor = CyberColors.BorderColor
                ),
                shape = RoundedCornerShape(8.dp)
            )
            
            // Dil listesi
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(filteredLanguages) { language ->
                    LanguageItem(
                        language = language,
                        onClick = {
                            // Dili kaydet ve dashboard'a git
                            navController.navigate("dashboard") {
                                popUpTo("language_selection") { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LanguageItem(
    language: Language,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(CyberColors.CardBackground, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = language.flag,
                    fontSize = 24.sp,
                    modifier = Modifier.size(32.dp)
                )
                
                Column {
                    Text(
                        text = language.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberColors.TextPrimary
                    )
                    Text(
                        text = language.nativeName,
                        fontSize = 12.sp,
                        color = CyberColors.TextSecondary
                    )
                }
            }
            
            Text(
                text = language.code,
                fontSize = 10.sp,
                color = CyberColors.TextHint,
                fontStyle = FontStyle.Italic
            )
        }
    }
}
