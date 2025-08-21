package br.com.mitra.biometricsdk.common.config

import br.com.mitra.biometricsdk.domain.models.SDKConfiguration
import br.com.mitra.biometricsdk.domain.models.SDKTheme

object BiometricSDKConfiguration {
    private var configuration: SDKConfiguration? = null
    private var theme: SDKTheme? = null

    fun initializeConfiguration(theme: SDKTheme, configuration: SDKConfiguration) {
        this.theme = theme
        this.configuration = configuration
    }

    fun SetTheme(newTheme: SDKTheme) {
        this.theme = newTheme
    }

    fun SetConfiguration(newConfiguration: SDKConfiguration) {
        this.configuration = newConfiguration
    }

    fun getTheme(): SDKTheme? {
        return this.theme
    }

    fun getConfiguration(): SDKConfiguration? {
        return this.configuration
    }
}