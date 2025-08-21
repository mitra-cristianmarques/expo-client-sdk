package br.com.mitra.biometricsdk.domain.enums.configuration

enum class ConfigurationKeys(val key: String) {
    DEVICE_KEY_IDENTIFIER("device_key_identifier"),
    BASE_URL("base_url"),
    PUBLIC_FACE_SCAN_ENCRYPTION_KEY("public_face_scan_encryption_key"),
    CLIENT_ID("client_id"),
    CLIENT_SECRET("client_secret"),
    API_BASE_URL("api_base_url"),
    ENVIRONMENT("environment")
}