package br.com.mitra.biometricsdk.common.config

import br.com.mitra.biometricsdk.domain.enums.configuration.ConfigurationKeys
import br.com.mitra.biometricsdk.domain.models.SdkConfiguration
import br.com.mitra.biometricsdk.domain.models.SdkTheme

object FacetecOptionsConfiguration {
    private var configuration: SdkConfiguration? = null
    private var theme: SdkTheme? = null

    fun initializeConfguration(theme: SdkTheme, configuration: SdkConfiguration) {
        this.theme = theme
        this.configuration = configuration
    }

    fun SetTheme(newTheme: SdkTheme) {
        this.theme = newTheme
    }

    fun SetConfiguration(newConfiguration: SdkConfiguration) {
        this.configuration = newConfiguration
    }

    fun getTheme(): SdkTheme? {
        return this.theme
    }

    fun getConfiguration(): SdkConfiguration? {
        return this.configuration
    }
}