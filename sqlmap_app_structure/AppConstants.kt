package com.sqlmap.app.utils.constants

object AppConstants {
    const val APP_NAME = "SQLMap~"
    const val APP_VERSION = "1.0.0"
    const val PACKAGE_NAME = "com.sqlmap.app"
    
    // Network
    const val DEFAULT_TIMEOUT = 30000L // 30 seconds
    const val MAX_RETRIES = 3
    const val RETRY_DELAY = 1000L
    
    // SQLMap
    const val MIN_PAYLOAD_LENGTH = 3
    const val MAX_PAYLOAD_LENGTH = 10000
    
    // WAF Detection
    const val WAF_DETECTION_TIMEOUT = 15000L
    const val WAF_CONFIDENCE_THRESHOLD = 60
    
    // Database
    const val DB_NAME = "sqlmap_app.db"
    const val DB_VERSION = 1
    
    // File paths
    const val LOG_DIR = "logs"
    const val RESULTS_DIR = "results"
    const val CONFIG_DIR = "config"
    const val DOWNLOADS_DIR = "downloads"
    
    // Preferences
    const val PREF_FIRST_LAUNCH = "first_launch"
    const val PREF_LANGUAGE = "language"
    const val PREF_DISCLAIMER_ACCEPTED = "disclaimer_accepted"
    const val PREF_LAST_TARGET = "last_target"
    const val PREF_PROXY_ENABLED = "proxy_enabled"
    const val PREF_PROXY_HOST = "proxy_host"
    const val PREF_PROXY_PORT = "proxy_port"
    
    // UI
    const val SPLASH_DURATION = 3000L
    const val ANIMATION_DURATION = 300
    
    // User Agent
    const val DEFAULT_USER_AGENT = "Mozilla/5.0 (Linux; Android 14; SQLMap~) AppleWebKit/537.36"
    
    // HTTP Methods
    const val HTTP_GET = "GET"
    const val HTTP_POST = "POST"
    const val HTTP_PUT = "PUT"
    const val HTTP_DELETE = "DELETE"
    const val HTTP_HEAD = "HEAD"
    
    // Content Types
    const val CONTENT_TYPE_JSON = "application/json"
    const val CONTENT_TYPE_FORM = "application/x-www-form-urlencoded"
    const val CONTENT_TYPE_MULTIPART = "multipart/form-data"
    
    // Injection Techniques
    const val TECHNIQUE_UNION = "UNION"
    const val TECHNIQUE_BOOLEAN_BLIND = "BOOLEAN_BLIND"
    const val TECHNIQUE_TIME_BLIND = "TIME_BLIND"
    const val TECHNIQUE_ERROR_BASED = "ERROR_BASED"
    const val TECHNIQUE_DNS = "DNS"
    
    // DBMS Types
    const val DBMS_MYSQL = "MySQL"
    const val DBMS_POSTGRESQL = "PostgreSQL"
    const val DBMS_MSSQL = "MS-SQL"
    const val DBMS_ORACLE = "Oracle"
    const val DBMS_SQLITE = "SQLite"
    const val DBMS_MONGODB = "MongoDB"
    
    // Notifications
    const val NOTIFICATION_CHANNEL_ID = "sqlmap_notifications"
    const val NOTIFICATION_ID_WAF = 1001
    const val NOTIFICATION_ID_INJECTION = 1002
    const val NOTIFICATION_ID_DUMP = 1003
    
    // Error codes
    const val ERROR_NETWORK = 1001
    const val ERROR_TIMEOUT = 1002
    const val ERROR_INVALID_URL = 1003
    const val ERROR_NO_PARAMETER = 1004
    const val ERROR_WAF_BLOCKED = 1005
}

object UIConstants {
    const val SPACING_EXTRA_SMALL = 4
    const val SPACING_SMALL = 8
    const val SPACING_MEDIUM = 12
    const val SPACING_LARGE = 16
    const val SPACING_EXTRA_LARGE = 24
    
    const val CORNER_RADIUS_SMALL = 4
    const val CORNER_RADIUS_MEDIUM = 8
    const val CORNER_RADIUS_LARGE = 16
    
    const val ICON_SIZE_SMALL = 16
    const val ICON_SIZE_MEDIUM = 24
    const val ICON_SIZE_LARGE = 32
    
    const val FONT_SIZE_SMALL = 10
    const val FONT_SIZE_MEDIUM = 14
    const val FONT_SIZE_LARGE = 18
    const val FONT_SIZE_XLARGE = 24
}

object LanguageConstants {
    const val LANGUAGE_ENGLISH = "en"
    const val LANGUAGE_TURKISH = "tr"
    const val LANGUAGE_ARABIC = "ar"
    const val LANGUAGE_SPANISH = "es"
    const val LANGUAGE_FRENCH = "fr"
    const val LANGUAGE_GERMAN = "de"
    const val LANGUAGE_CHINESE = "zh"
    const val LANGUAGE_RUSSIAN = "ru"
    const val LANGUAGE_JAPANESE = "ja"
    const val LANGUAGE_KOREAN = "ko"
}

object RegexPatterns {
    const val URL_PATTERN = "^https?://[a-zA-Z0-9-.]+(:[0-9]+)?(/[^?]*)?(?:\\?[^#]*)?(#.*)?$"
    const val EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    const val IP_PATTERN = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"
    const val DOMAIN_PATTERN = "^([a-zA-Z0-9]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,}$"
}

object SQLErrorPatterns {
    const val MYSQL_ERROR = "SQL.*Error|MySQL.*Error|syntax.*error"
    const val POSTGRESQL_ERROR = "PostgreSQL.*Error|SQLSTATE"
    const val MSSQL_ERROR = "Msg [0-9]+|SQL Server"
    const val ORACLE_ERROR = "ORA-[0-9]+"
    const val SQLITE_ERROR = "SQLite.*error"
}

object ByteSize {
    const val KB = 1024L
    const val MB = 1024 * KB
    const val GB = 1024 * MB
}
