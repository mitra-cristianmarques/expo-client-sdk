// Welcome to the FaceTec Sample App
// This sample demonstrates Initialization, Liveness Check, Enrollment, Verification, Photo ID Match, Customizing the UX, and Getting Audit Trail Images.
// Please use our technical support form to submit questions and issue reports:  https://dev.facetec.com/
package br.com.mitra.biometricsdk.facetec

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import br.com.mitra.biometricsdk.processors.Config
import br.com.mitra.biometricsdk.processors.EnrollmentProcessor
import br.com.mitra.biometricsdk.processors.LivenessCheckProcessor
import br.com.mitra.biometricsdk.processors.NetworkingHelpers
import br.com.mitra.biometricsdk.processors.PhotoIDMatchProcessor
import br.com.mitra.biometricsdk.processors.PhotoIDScanProcessor
import br.com.mitra.biometricsdk.processors.Processor
import br.com.mitra.biometricsdk.processors.ThemeHelpers
import br.com.mitra.biometricsdk.processors.VerificationProcessor
import com.facetec.sdk.FaceTecIDScanResult
import com.facetec.sdk.FaceTecSDK
import com.facetec.sdk.FaceTecSessionResult
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class SampleAppActivity : Activity() {
    // public ActivityMainBinding activityMainBinding;
    var utils: SampleAppUtilities = SampleAppUtilities(this)
    var latestSessionResult: FaceTecSessionResult? = null
    var latestIDScanResult: FaceTecIDScanResult? = null
    private var isSessionPreparingToLaunch = false

    var latestExternalDatabaseRefID: String? = ""
    var externalID: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Optional - Preload resources related to the FaceTec SDK so that it can be run as soon as possible.
        //            Run this as soon as you think you might use the SDK for optimal start up performance.
        try {
            FaceTecSDK.preload(this)
        } catch (e: Exception) {
            Log.e("DEBUG_BIOMETRICS", "❌ Error in FaceTecSDK.preload(): " + e.message, e)
            e.printStackTrace()
        }
        configureInitialSampleAppUI()

        utils.displayStatus("Initializing...")

        Config.getConfigFacetec(this)

        // Set your FaceTec Device SDK Customizations.
        ThemeHelpers.setAppTheme(this, utils.currentTheme)

        // Set the strings to be used for group names, field names, and placeholder texts for the FaceTec ID Scan User OCR Confirmation Screen.
        SampleAppUtilities.Companion.setOCRLocalization(this)

        // Set the FaceTec Customization defined in the Config File.
        SampleAppUtilities.Companion.setVocalGuidanceSoundFiles()
        utils.setUpVocalGuidancePlayers()
    }

    override fun onBackPressed() {
        // If the activity is in the process of launching FaceTec sessions, don't exit the app.
        if (isSessionPreparingToLaunch) {
            return
        }

        super.onBackPressed()
    }

    // Perform Liveness Check.
    fun onLivenessCheckPressed(v: View?) {
        isSessionPreparingToLaunch = true

        utils.fadeOutMainUIAndPrepareForFaceTecSDK(Runnable {
            getSessionToken(SessionTokenCallback { sessionToken: String? ->
                resetLatestResults()
                isSessionPreparingToLaunch = false
                latestProcessor = LivenessCheckProcessor(sessionToken, this)
            })
        })
    }

    // Perform Enrollment, generating a username each time to guarantee uniqueness.
    fun onEnrollUserPressed(v: View?) {
        isSessionPreparingToLaunch = true

        utils.fadeOutMainUIAndPrepareForFaceTecSDK(Runnable {
            getSessionToken(SessionTokenCallback { sessionToken: String? ->
                resetLatestResults()
                isSessionPreparingToLaunch = false
                if (latestExternalDatabaseRefID!!.length <= 0) {
                    latestExternalDatabaseRefID = externalID
                }
                latestProcessor = EnrollmentProcessor(sessionToken, this)
            })
        })
    }

    // Perform Verification, using the username from Enrollment.
    fun onVerifyUserPressed(v: View?) {
        isSessionPreparingToLaunch = true

        if (latestExternalDatabaseRefID == null || latestExternalDatabaseRefID!!.length == 0) {
            utils.displayStatus("Please enroll first before trying verification.")
            return
        }

        utils.fadeOutMainUIAndPrepareForFaceTecSDK(Runnable {
            getSessionToken(SessionTokenCallback { sessionToken: String? ->
                resetLatestResults()
                isSessionPreparingToLaunch = false
                latestProcessor = VerificationProcessor(sessionToken, this)
            })
        })
    }

    // Perform Photo ID Match, generating a username each time to guarantee uniqueness.
    fun onPhotoIDMatchPressed(view: View?) {
        isSessionPreparingToLaunch = true

        utils.fadeOutMainUIAndPrepareForFaceTecSDK(Runnable {
            getSessionToken(SessionTokenCallback { sessionToken: String? ->
                resetLatestResults()
                isSessionPreparingToLaunch = false
                if (latestExternalDatabaseRefID!!.length <= 0) {
                    latestExternalDatabaseRefID = externalID
                }
                latestProcessor = PhotoIDMatchProcessor(sessionToken, this)
            })
        })
    }

    // Perform Photo ID Scan, generating a username each time to guarantee uniqueness.
    fun onPhotoIDScanOnlyPressed(view: View?) {
        isSessionPreparingToLaunch = true

        utils.fadeOutMainUIAndPrepareForFaceTecSDK(Runnable {
            getSessionToken(SessionTokenCallback { sessionToken: String? ->
                resetLatestResults()
                isSessionPreparingToLaunch = false
                latestProcessor = PhotoIDScanProcessor(sessionToken, this)
            })
        })
    }

    fun onVocalGuidanceSettingsButtonPressed(v: View?) {
        utils.setVocalGuidanceMode()
    }

    // When the FaceTec SDK is completely done, you receive control back here.
    // Since you have already handled all results in your Processor code, how you proceed here is up to you and how your App works.
    // In general, there was either a Success, or there was some other case where you cancelled out.
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (latestProcessor == null) {
            return
        }

        if (latestSessionResult != null && latestSessionResult!!.getStatus() != null) {
            Log.d(
                "FaceTecSDKSampleApp",
                "Session Status: " + latestSessionResult!!.getStatus().toString()
            )
        }

        if (latestIDScanResult != null && latestIDScanResult!!.getStatus() != null) {
            Log.d(
                "FaceTecSDKSampleApp",
                "ID Scan Status: " + latestIDScanResult!!.getStatus().toString()
            )
        }

        utils.displayStatus("See logs for more details.")
        utils.fadeInMainUI()

        // At this point, you have already handled all results in your Processor code.
        if (!latestProcessor!!.isSuccess()) {
            // Reset the enrollment identifier.
            latestExternalDatabaseRefID = ""
        } else {
            externalDatabaseRefID = latestExternalDatabaseRefID!!
        }

        // MainActivity reference removed for library compatibility
        // Intent intent = new Intent(SampleAppActivity.this, MainActivity.class);
        // startActivity(intent);
        finish()
    }

    //
    // DEVELOPER NOTE:  This is a convenience function for demonstration purposes only so the Sample App can have access to the latest session results.
    // In your code, you may not even want or need to do this.
    //
    fun setLatestSessionResult(sessionResult: FaceTecSessionResult?) {
        this.latestSessionResult = sessionResult
    }

    //
    // DEVELOPER NOTE:  This is a convenience function for demonstration purposes only so the Sample App can have access to the latest id scan results.
    // In your code, you may not even want or need to do this.
    //
    fun setLatestIDScanResult(idScanResult: FaceTecIDScanResult?) {
        this.latestIDScanResult = idScanResult
    }

    private fun resetLatestResults() {
        this.latestSessionResult = null
        this.latestIDScanResult = null
    }

    fun configureInitialSampleAppUI() {
        // Data binding removed for library compatibility
        // activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        //utils.setupAllButtons();

        //Esconde os botões para o usuário não ter acesso

        utils.hiddenAllButtons()

        // If the screen size is small, reduce FaceTec Logo
        // if(getResources().getConfiguration().screenHeightDp < 500) {
        //     activityMainBinding.facetecLogo.setScaleX(0.6f);
        //     activityMainBinding.facetecLogo.setScaleY(0.6f);
        //     ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) activityMainBinding.facetecLogo.getLayoutParams();
        //     params.setMargins(0, 0, 0, 0);
        // }

        // runOnUiThread(() -> activityMainBinding.vocalGuidanceSettingButton.setEnabled(false));
    }

    // Display audit trail images captured from user's last session
    fun onViewAuditTrailPressed(view: View?) {
        utils.showAuditTrailImages()
    }

    // Present settings action sheet, allowing user to select a new app theme (pre-made FaceTecCustomization configuration).
    fun onThemeSelectionPressed(view: View?) {
        utils.showThemeSelectionMenu()
    }

    internal interface SessionTokenCallback {
        fun onSessionTokenReceived(sessionToken: String?)
    }

    fun getSessionToken(sessionTokenCallback: SessionTokenCallback) {
        utils.showSessionTokenConnectionText()

        // Do the network call and handle result
        val request = Request.Builder()
            .header("X-Device-Key", Config.DeviceKeyIdentifier)
            .header("User-Agent", FaceTecSDK.createFaceTecAPIUserAgentString(""))
            .header("X-User-Agent", FaceTecSDK.createFaceTecAPIUserAgentString(""))
            .header("Authorization", "Bearer " + Config.accessToken)
            .url(Config.BaseURL + "/session-token")
            .get()
            .build()

        NetworkingHelpers.getApiClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException) {
                e.printStackTrace()
                Log.d("FaceTecSDKSampleApp", "Exception raised while attempting HTTPS call.")

                // If this comes from HTTPS cancel call, don't set the sub code to NETWORK_ERROR.
                if (e.message != NetworkingHelpers.OK_HTTP_RESPONSE_CANCELED) {
                    utils.handleErrorGettingServerSessionToken()
                }
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call?, response: Response) {
                val responseString = response.body()!!.string()
                response.body()!!.close()
                try {
                    val responseJSON = JSONObject(responseString)
                    if (responseJSON.has("sessionToken")) {
                        utils.hideSessionTokenConnectionText()
                        sessionTokenCallback.onSessionTokenReceived(responseJSON.getString("sessionToken"))
                    } else {
                        utils.handleErrorGettingServerSessionToken()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.d(
                        "FaceTecSDKSampleApp",
                        "Exception raised while attempting to parse JSON result."
                    )
                    utils.handleErrorGettingServerSessionToken()
                }
            }
        })
    }

    companion object {
        var latestProcessor: Processor? = null

        // Parametros retornados para o RN
        var externalDatabaseRefID: String = ""
        var documentData: String = ""
        var faceScan: String = ""
        var documentFrontScan: String = ""
        var documentBackScan: String = ""
        var requestEnroll: String = ""
        var responseEnroll: String = ""
        var httpCode: Int = 0
        var resquestFrontDocument: String = ""
        var responseFrontDocument: String = ""
        var resquestBacktDocument: String = ""
        var responseBackDocument: String = ""
    }
}