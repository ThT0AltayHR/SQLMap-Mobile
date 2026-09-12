# SQLMap~ Android Uygulaması - Proje Yapısı

## App Tanımları
- **Ad:** SQLMap~
- **Paket:** com.sqlmap.app
- **İkon:** Baykuş (1000070838.png)
- **Hedef SDK:** 34 (Android 14)
- **Min SDK:** 24 (Android 7.0)
- **Ekran:** Landscape (Yatay)
- **Boyut:** ~250-300 MB

## Dosya Yapısı (~150 dosya)

```
app/
├── src/main/
│   ├── kotlin/com/sqlmap/app/
│   │   ├── MainActivity.kt
│   │   ├── SplashActivity.kt
│   │   ├── 
│   │   ├── ui/
│   │   │   ├── screens/
│   │   │   │   ├── SplashScreen.kt
│   │   │   │   ├── WelcomeScreen.kt
│   │   │   │   ├── DisclaimerScreen.kt
│   │   │   │   ├── LanguageSelectionScreen.kt
│   │   │   │   ├── DashboardScreen.kt
│   │   │   │   ├── TargetSetupScreen.kt
│   │   │   │   ├── WAFDetectionScreen.kt
│   │   │   │   ├── InjectionTestScreen.kt
│   │   │   │   ├── DatabaseEnumScreen.kt
│   │   │   │   ├── DumpProgressScreen.kt
│   │   │   │   ├── ResultsViewerScreen.kt
│   │   │   │   ├── SettingsScreen.kt
│   │   │   │   └── HelpMenuScreen.kt
│   │   │   │
│   │   │   ├── components/
│   │   │   │   ├── TopBar.kt
│   │   │   │   ├── NavigationDrawer.kt
│   │   │   │   ├── ProgressBar.kt
│   │   │   │   ├── LogViewer.kt
│   │   │   │   ├── ResultsCard.kt
│   │   │   │   ├── WAFBadge.kt
│   │   │   │   ├── InjectionLevelIndicator.kt
│   │   │   │   ├── DatabaseViewer.kt
│   │   │   │   ├── CustomDialog.kt
│   │   │   │   ├── GraphicalChart.kt
│   │   │   │   └── CustomButton.kt
│   │   │   │
│   │   │   ├── theme/
│   │   │   │   ├── Color.kt
│   │   │   │   ├── Typography.kt
│   │   │   │   ├── Shapes.kt
│   │   │   │   └── Theme.kt
│   │   │   │
│   │   │   └── navigation/
│   │   │       ├── NavigationGraph.kt
│   │   │       └── Screen.kt
│   │   │
│   │   ├── data/
│   │   │   ├── models/
│   │   │   │   ├── TargetModel.kt
│   │   │   │   ├── WAFModel.kt
│   │   │   │   ├── InjectionModel.kt
│   │   │   │   ├── DatabaseModel.kt
│   │   │   │   ├── TestResultModel.kt
│   │   │   │   └── ConfigModel.kt
│   │   │   │
│   │   │   ├── repository/
│   │   │   │   ├── TargetRepository.kt
│   │   │   │   ├── ResultRepository.kt
│   │   │   │   └── ConfigRepository.kt
│   │   │   │
│   │   │   ├── database/
│   │   │   │   ├── AppDatabase.kt
│   │   │   │   ├── ResultsDao.kt
│   │   │   │   └── ConfigDao.kt
│   │   │   │
│   │   │   └── local/
│   │   │       └── SharedPreferencesManager.kt
│   │   │
│   │   ├── domain/
│   │   │   ├── usecase/
│   │   │   │   ├── WAFDetectionUseCase.kt
│   │   │   │   ├── InjectionUseCase.kt
│   │   │   │   ├── DatabaseEnumUseCase.kt
│   │   │   │   └── DumpUseCase.kt
│   │   │   │
│   │   │   └── repository/
│   │   │       ├── ITargetRepository.kt
│   │   │       ├── IResultRepository.kt
│   │   │       └── IConfigRepository.kt
│   │   │
│   │   ├── utils/
│   │   │   ├── extensions/
│   │   │   │   ├── StringExt.kt
│   │   │   │   ├── ContextExt.kt
│   │   │   │   └── ViewExt.kt
│   │   │   │
│   │   │   ├── constants/
│   │   │   │   ├── AppConstants.kt
│   │   │   │   ├── UIConstants.kt
│   │   │   │   └── LanguageConstants.kt
│   │   │   │
│   │   │   ├── helpers/
│   │   │   │   ├── LoggingHelper.kt
│   │   │   │   ├── NotificationHelper.kt
│   │   │   │   ├── FileHelper.kt
│   │   │   │   └── LanguageHelper.kt
│   │   │   │
│   │   │   └── security/
│   │   │       └── DisclaimerManager.kt
│   │   │
│   │   ├── sqlmap/
│   │   │   ├── core/
│   │   │   │   ├── SQLMapCore.kt
│   │   │   │   ├── PayloadGenerator.kt
│   │   │   │   ├── TechniqueManager.kt
│   │   │   │   └── DatabaseManager.kt
│   │   │   │
│   │   │   ├── manager/
│   │   │   │   ├── InjectionManager.kt
│   │   │   │   ├── EnumerationManager.kt
│   │   │   │   └── DumpManager.kt
│   │   │   │
│   │   │   ├── payloads/
│   │   │   │   ├── UnionPayloads.kt
│   │   │   │   ├── BlindPayloads.kt
│   │   │   │   ├── ErrorPayloads.kt
│   │   │   │   └── DNSPayloads.kt
│   │   │   │
│   │   │   └── techniques/
│   │   │       ├── UnionTechnique.kt
│   │   │       ├── BlindTechnique.kt
│   │   │       ├── ErrorTechnique.kt
│   │   │       ├── TimeBased.kt
│   │   │       └── DNSTechnique.kt
│   │   │
│   │   ├── wafw00f/
│   │   │   ├── core/
│   │   │   │   └── WAFDetector.kt
│   │   │   │
│   │   │   ├── plugins/
│   │   │   │   ├── CloudflareDetector.kt
│   │   │   │   ├── AkamaiDetector.kt
│   │   │   │   ├── ModSecurityDetector.kt
│   │   │   │   ├── FortinetDetector.kt
│   │   │   │   ├── PaloAltoDetector.kt
│   │   │   │   ├── AWSWAFDetector.kt
│   │   │   │   └── WAFPluginBase.kt
│   │   │   │
│   │   │   ├── bypass/
│   │   │   │   ├── BypassStrategy.kt
│   │   │   │   ├── TamperScripts.kt
│   │   │   │   └── HeaderBypass.kt
│   │   │   │
│   │   │   └── signatures/
│   │   │       └── WAFSignatures.kt
│   │   │
│   │   ├── network/
│   │   │   ├── http/
│   │   │   │   ├── HttpClient.kt
│   │   │   │   ├── RequestBuilder.kt
│   │   │   │   └── ResponseParser.kt
│   │   │   │
│   │   │   ├── proxy/
│   │   │   │   ├── ProxyManager.kt
│   │   │   │   └── ProxyConfig.kt
│   │   │   │
│   │   │   └── ssl/
│   │   │       ├── SSLConfiguration.kt
│   │   │       └── CertificateManager.kt
│   │   │
│   │   ├── viewmodel/
│   │   │   ├── SplashViewModel.kt
│   │   │   ├── TargetSetupViewModel.kt
│   │   │   ├── WAFDetectionViewModel.kt
│   │   │   ├── InjectionViewModel.kt
│   │   │   ├── DatabaseViewModel.kt
│   │   │   ├── DumpViewModel.kt
│   │   │   └── SettingsViewModel.kt
│   │   │
│   │   ├── service/
│   │   │   ├── LoggingService.kt
│   │   │   ├── NotificationService.kt
│   │   │   ├── WorkerService.kt
│   │   │   └── BackgroundTaskService.kt
│   │   │
│   │   └── di/
│   │       ├── AppModule.kt
│   │       ├── NetworkModule.kt
│   │       ├── DatabaseModule.kt
│   │       ├── RepositoryModule.kt
│   │       └── UseCaseModule.kt
│   │
│   ├── res/
│   │   ├── drawable/
│   │   │   ├── ic_owl_icon.png (1000070838.png)
│   │   │   ├── ic_sql_injection.png (1000070753.png)
│   │   │   ├── ic_database_dump.png (1000070752.png)
│   │   │   ├── ic_sqlmap_logo.png (1000070751.png)
│   │   │   ├── ic_launcher_foreground.xml
│   │   │   ├── ic_launcher_background.xml
│   │   │   ├── gradient_dark_cyber.xml
│   │   │   ├── button_style.xml
│   │   │   ├── rounded_button.xml
│   │   │   └── custom_shapes.xml
│   │   │
│   │   ├── layout/
│   │   │   ├── activity_main.xml
│   │   │   ├── activity_splash.xml
│   │   │   ├── fragment_dashboard.xml
│   │   │   └── [tüm composable'lar için]
│   │   │
│   │   ├── values/
│   │   │   ├── strings.xml (Multi-language)
│   │   │   ├── colors.xml
│   │   │   ├── dimens.xml
│   │   │   ├── styles.xml
│   │   │   └── attrs.xml
│   │   │
│   │   ├── values-tr/
│   │   │   └── strings.xml
│   │   │
│   │   ├── values-ar/
│   │   ├── values-es/
│   │   ├── values-fr/
│   │   ├── ... (197 dil için)
│   │   │
│   │   ├── menu/
│   │   │   ├── menu_main.xml
│   │   │   ├── menu_settings.xml
│   │   │   └── menu_options.xml
│   │   │
│   │   ├── anim/
│   │   │   ├── fade_in.xml
│   │   │   ├── slide_in.xml
│   │   │   └── scale_animation.xml
│   │   │
│   │   └── font/
│   │       ├── italic_font.ttf
│   │       └── custom_font.ttf
│   │
│   ├── AndroidManifest.xml
│   ├── java/com/sqlmap/app/ (Java interop için)
│   └── assets/
│       ├── sqlmap/ (SQLMap Python kodu)
│       ├── wafw00f/ (wafw00f Python kodu)
│       ├── payloads/
│       │   ├── union.xml
│       │   ├── blind.xml
│       │   ├── error.xml
│       │   └── dns.xml
│       ├── databases/
│       │   ├── mysql.xml
│       │   ├── postgresql.xml
│       │   ├── mssql.xml
│       │   ├── oracle.xml
│       │   └── sqlite.xml
│       ├── waf_signatures.json
│       ├── tamper_scripts.json
│       └── help/
│           ├── en.md
│           ├── tr.md
│           └── ... (tüm diller)
│
├── build.gradle.kts
├── proguard-rules.pro
└── lint.xml

## Teknoloji Stack

### Android/Kotlin
- Jetpack Compose (UI)
- ViewModel + StateFlow
- Coroutines
- Room (Local Database)
- Dagger-Hilt (DI)
- Retrofit (HTTP Client)
- OkHttp3 (Advanced Networking)
- WorkManager (Background Tasks)
- DataStore (Preferences)

### Python Integration
- Chaquopy (Kotlin ↔ Python)
- Paramiko (SSH, SFTP)
- Requests (HTTP)
- sqlite3 (Database)

### Libraries
- Material Design 3
- Navigation Component
- Accompanist (Compose Extensions)
- MPAndroidChart (Grafik)
- Timber (Logging)
- Moshi (JSON)

## Dil Desteği (197 Dil)
- Afrikaans, Shqip, አማርኛ, العربية, Հայերեն, Azərbaycanca, Български, 中文...
- Her dil için strings.xml + values-XX/

## Tema Özellikleri
- Siber güvenlik teması (Siyah, Koyu Gri, Turuncu aksent)
- İtalik yazı tipi (Özel)
- Modern SVG ikonlar (Custom, Emoji yok)
- Gradient arka planlar
- Smooth animasyonlar

## APK Bilgileri
- **Format:** Universal APK
- **Boyut:** ~250-300 MB
- **Minimum RAM:** 2GB
- **İzinler:** INTERNET, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, POST_NOTIFICATIONS

