package br.com.mitra.biometricsdk.domain.models

import br.com.mitra.biometricsdk.domain.enums.configuration.SdkEnvironment

data class SDKConfiguration(
    var deviceKeyIdentifier: String = "",
    var baseUrl: String = "",
    var publicFaceScanEncryptionKey: String = "",
    var clientId: String = "",
    var clientSecret: String = "",
    var apiBaseUrl: String = "",
    var environment: SdkEnvironment = SdkEnvironment.DEVELOPMENT
) {
    fun fromMap(map: Map<String, Any>): SDKConfiguration {
        this.deviceKeyIdentifier = map["deviceKeyIdentifier"] as? String ?: ""
        this.baseUrl = map["baseUrl"] as? String ?: ""
        this.publicFaceScanEncryptionKey = map["publicFaceScanEncryptionKey"] as? String ?: ""
        this.clientId = map["clientId"] as? String ?: ""
        this.clientSecret = map["clientSecret"] as? String ?: ""
        this.apiBaseUrl = map["apiBaseUrl"] as? String ?: ""
        this.environment = (map["environment"] as? String)?.let { SdkEnvironment.valueOf(it) } ?: SdkEnvironment.DEVELOPMENT
        return this
    }

    fun serializeForReactNative(): Map<String, String> {
        val configMap = mutableMapOf<String, String>()
        configMap["deviceKeyIdentifier"] = this.deviceKeyIdentifier
        configMap["baseUrl"] = this.baseUrl
        configMap["publicFaceScanEncryptionKey"] = this.publicFaceScanEncryptionKey
        configMap["clientId"] = this.clientId
        configMap["clientSecret"] = this.clientSecret
        configMap["apiBaseUrl"] = this.apiBaseUrl
        configMap["environment"] = this.environment.toString()
        return configMap
    }
}
