# SQLMap~ Android Uygulaması - Implementasyon Rehberi

## 1. Proje Kurulumu

### Gradle Dosyaları
- `build.gradle.kts` - Proje yapılandırması
- Python desteği (Chaquopy)
- OkHttp3, Retrofit, Room, Hilt

### Bağımlılıklar
```
- androidx.compose.* (UI)
- androidx.lifecycle.* (ViewModel)
- com.google.dagger:hilt (DI)
- androidx.room:* (Database)
- com.squareup.okhttp3 (HTTP)
- com.squareup.retrofit2 (REST)
- com.chaquopy.python (Python)
```

## 2. Ekran Yapısı (Sırasıyla)

### A. Başlangıç Ekranları
1. **SplashScreen** (SplashScreen.kt)
   - SQLMap logosu göster
   - Yükleme animasyonu
   - ~3 saniye sonra WelcomeScreen'e git

2. **WelcomeScreen** (WelcomeScreen.kt)
   - "Welcome to SQLMap~" başlığı
   - "Continue" butonu
   - DisclaimerScreen'e git

3. **DisclaimerScreen** (DisclaimerScreen.kt)
   - Yasal sorumluluk reddi metni
   - Checkbox ile kabul
   - Accept/Decline butonları

4. **LanguageSelectionScreen**
   - 197 dil seçimi (kaydırılır liste)
   - Her dil için flag görseli
   - Seçimden sonra DashboardScreen'e git

### B. Ana Ekran
5. **DashboardScreen** (DashboardScreen.kt)
   - Sol panel: Kontroller ve durumu göster
   - Sağ panel: Gerçek zamanlı log
   - 2 tarafta görsel

### C. Test Ekranları
6. **WAFDetectionScreen**
   - Spinner (yükleme animasyonu)
   - Tespit edilen WAF adı
   - Güven yüzdesi
   - Bypass stratejileri listesi

7. **InjectionTestScreen**
   - SQL Injection görseli (1000070753.png)
   - "INJECTION STARTED!" mesajı
   - Her teknik için checkbox
   - İlerleme çubuğu
   - Bildirim gönder

8. **DatabaseEnumScreen**
   - Ağaç yapısı (veritabanı > tabloları)
   - Kayıt sayıları
   - Sütun türleri

9. **DumpProgressScreen**
   - Database Dump görseli (1000070752.png)
   - %75 gibi ilerleme göstergesi
   - Dökülmüş tablo listesi

10. **ResultsViewerScreen**
    - SQLite dosyası tablo olarak göster
    - Export butonu
    - Share butonu

### D. Ayarlar
11. **SettingsScreen**
    - Proxy yapılandırması
    - Timeout değerleri
    - User-Agent seçimi
    - Dil değiştirme

12. **HelpMenuScreen**
    - Seçili dilerde yardım

## 3. Bileşen Yapısı

### UI Components (components/)
- `TopBar.kt` - Başlık bar
- `ProgressBar.kt` - İlerleme çubuğu
- `LogViewer.kt` - Log görüntüleyici
- `WAFBadge.kt` - WAF etiketi
- `DatabaseViewer.kt` - Ağaç yapısı

### Core Managers (sqlmap/)
- `SQLMapCore.kt` - Ana çekirdek
- `InjectionManager.kt` - Enjeksiyon
- `DatabaseEnumManager.kt` - Veritabanı
- `DumpManager.kt` - Dump işlemleri

### WAF Detection (wafw00f/)
- `WAFDetector.kt` - Tespit
- `WAFPluginBase.kt` - Plugin sistemi
- `BypassStrategy.kt` - Bypass

### Network
- `HttpClient.kt` - HTTP istekleri
- `RequestBuilder.kt` - İstek yapılandırması
- `ResponseParser.kt` - Yanıt analizi

### Utils
- `LoggingHelper.kt` - Günlükleme
- `NotificationHelper.kt` - Bildirimler
- `FileHelper.kt` - Dosya işlemleri

## 4. Veri Akışı

```
User Input (URL, Parameter)
    ↓
WAF Detection (wafw00f)
    ├─ WAF Detected? → Notification
    └─ Get Bypass Strategies
    ↓
SQL Injection Test (SQLMap)
    ├─ Try Each Technique (UNION, Blind, Error, DNS)
    ├─ Found Vulnerable? → Notification + Log
    └─ Detect DBMS
    ↓
Database Enumeration
    ├─ List Databases
    ├─ List Tables
    ├─ List Columns
    └─ Get Record Counts
    ↓
Database Dump
    ├─ Extract Data
    ├─ Progress Updates → Notification
    └─ Save to SQLite File
    ↓
Results Viewer
    └─ Display & Export
```

## 5. Dosya Çıkış Yapısı

```
/sdcard/Android/data/com.sqlmap.app/
├── cache/
│   └── sqlmap_cache/
├── files/
│   ├── logs/
│   │   ├── sqlmap_20240112_143022.log
│   │   └── ...
│   ├── results/
│   │   ├── dump_20240112_143500.db
│   │   └── ...
│   └── config/
│       └── settings.json
└── Download/ (kullanıcı kopya yapabilir)
    └── dump_20240112_143500.db
```

## 6. Bildirim Stratejisi

- **WAF Detected** → HIGH priority + Sound
- **Injection Started** → DEFAULT + Vibration
- **Injection Result** → HIGH priority + Custom sound
- **Dump Progress** → ONGOING (silent)
- **Dump Complete** → HIGH priority + Sound

## 7. Hata Yönetimi

```kotlin
try {
    // İşlem
} catch (e: HttpException) {
    logger.logError(e, "HTTP Request")
    showErrorNotification(e.message)
} catch (e: TimeoutException) {
    logger.log("Request timeout")
    retryWithIncreasedTimeout()
} catch (e: Exception) {
    logger.logError(e, "Unknown")
    showGenericErrorDialog()
}
```

## 8. İyileştirmeler

- Background Tasks (WorkManager)
- Result Caching (Room + Preferences)
- Batch Processing
- Progress Callbacks
- Graceful Degradation (eksik bağımlılıklar)

## 9. Test Senaryoları

1. **WAF Bypass Test**
   - Cloudflare, Akamai, ModSecurity, AWS WAF
   - Farklı bypass stratejileri

2. **Injection Technique Test**
   - UNION-based (4+ joins)
   - Boolean Blind (5+ comparisons)
   - Time-based Blind (SLEEP/BENCHMARK)
   - Error-based (ExtractValue/UpdateXML)
   - DNS Exfiltration

3. **DBMS Detection**
   - MySQL, PostgreSQL, MSSQL, Oracle, SQLite
   - Her DBMS için özel sorgular

4. **Performance**
   - 1000+ kayıt dump (ilerleme tracking)
   - Proxy'ler aracılığıyla
   - Yavaş bağlantılarda timeout

## 10. Güvenlik Notları

- SSL Certificate Pinning (optional)
- Request/Response encryption (for sensitive data)
- Disclaimer check before any test
- Activity logging (auditable)
- APK code obfuscation (ProGuard)

