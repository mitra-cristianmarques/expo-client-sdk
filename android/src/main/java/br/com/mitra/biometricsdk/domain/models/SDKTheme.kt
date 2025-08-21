package br.com.mitra.biometricsdk.domain.models

data class SDKTheme(
    var outerBackgroundColor: String = "#ffffff",
    var frameColor: String = "#ffffff",
    var borderColor: String = "#417FB2",
    var ovalColor: String = "#417FB2",
    var dualSpinnerColor: String = "#417FB2",
    var textColor: String = "#417FB2",
    var buttonAndFeedbackBarColor: String = "#417FB2",
    var buttonAndFeedbackBarTextColor: String = "#ffffff",
    var buttonColorHighlight: String = "#396E99",
    var buttonColorDisabled: String = "#B9CCDE"
) {
    fun fromMap(themeMap: Map<String, Any>): SDKTheme {
        this.outerBackgroundColor = themeMap["outerBackgroundColor"] as? String ?: "#ffffff"
        this.frameColor = themeMap["frameColor"] as? String ?: "#ffffff"
        this.borderColor = themeMap["borderColor"] as? String ?: "#417FB2"
        this.ovalColor = themeMap["ovalColor"] as? String ?: "#417FB2"
        this.dualSpinnerColor = themeMap["dualSpinnerColor"] as? String ?: "#417FB2"
        this.textColor = themeMap["textColor"] as? String ?: "#417FB2"
        this.buttonAndFeedbackBarColor = themeMap["buttonAndFeedbackBarTextColor"] as? String ?: "#417FB2"
        this.buttonAndFeedbackBarTextColor = themeMap["buttonAndFeedbackBarTextColor"] as? String ?: "#ffffff"
        this.buttonColorHighlight = themeMap["buttonColorHighlight"] as? String ?: "#396E99"
        this.buttonColorDisabled = themeMap["buttonColorDisabled"] as? String ?: "#B9CCDE"
        return this
    }
}
