// Welcome to the FaceTec Sample App
// This sample demonstrates Initialization, Liveness Check, Enrollment, Verification, Photo ID Match, Customizing the UX, and Getting Audit Trail Images.
// Please use our technical support form to submit questions and issue reports:  https://dev.facetec.com/
package br.com.mitra.biometricsdk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

// import androidx.databinding.DataBindingUtil;

// import br.com.mitra.biometricsdk.databinding.ActivityMainBinding;
import com.facetec.sdk.FaceTecIDScanResult;
import com.facetec.sdk.FaceTecSDK;
import com.facetec.sdk.FaceTecSessionResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import br.com.mitra.biometricsdk.processors.VerificationProcessor;
import br.com.mitra.biometricsdk.processors.Config;
import br.com.mitra.biometricsdk.processors.EnrollmentProcessor;
import br.com.mitra.biometricsdk.processors.LivenessCheckProcessor;
import br.com.mitra.biometricsdk.processors.NetworkingHelpers;
import br.com.mitra.biometricsdk.processors.PhotoIDMatchProcessor;
import br.com.mitra.biometricsdk.processors.PhotoIDScanProcessor;
import br.com.mitra.biometricsdk.processors.Processor;
import br.com.mitra.biometricsdk.processors.ThemeHelpers;
import okhttp3.Call;
import okhttp3.Callback;

public class SampleAppActivity extends Activity {
    // public ActivityMainBinding activityMainBinding;
    public SampleAppUtilities utils = new SampleAppUtilities(this);
    public FaceTecSessionResult latestSessionResult;
    public FaceTecIDScanResult latestIDScanResult;
    public static Processor latestProcessor;
    private boolean isSessionPreparingToLaunch = false;

    public String latestExternalDatabaseRefID = "";
    String externalID = "";

    // Parametros retornados para o RN
    static String externalDatabaseRefID = "";
    public static String documentData = "";
    public static String faceScan = "";
    public static String documentFrontScan = "";
    public static String documentBackScan = "";
    public static String requestEnroll = "";
    public static String responseEnroll = "";
    public static int httpCode = 0;
    public static String resquestFrontDocument = "";
    public static String responseFrontDocument = "";
    public static String resquestBacktDocument = "";
    public static String responseBackDocument = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Optional - Preload resources related to the FaceTec SDK so that it can be run as soon as possible.
        //            Run this as soon as you think you might use the SDK for optimal start up performance.
        FaceTecSDK.preload(this);

        configureInitialSampleAppUI();

        utils.displayStatus("Initializing...");

        Config.getConfigFacetec(this);

        // Set your FaceTec Device SDK Customizations.
        ThemeHelpers.setAppTheme(this, utils.currentTheme);

        // Set the strings to be used for group names, field names, and placeholder texts for the FaceTec ID Scan User OCR Confirmation Screen.
        SampleAppUtilities.setOCRLocalization(this);

        // Set the FaceTec Customization defined in the Config File.
        SampleAppUtilities.setVocalGuidanceSoundFiles();
        utils.setUpVocalGuidancePlayers();
    }

    @Override
    public void onBackPressed() {
        // If the activity is in the process of launching FaceTec sessions, don't exit the app.
        if(isSessionPreparingToLaunch) {
            return;
        }

        super.onBackPressed();
    }

    // Perform Liveness Check.
    public void onLivenessCheckPressed(View v) {
        isSessionPreparingToLaunch = true;

        utils.fadeOutMainUIAndPrepareForFaceTecSDK(() -> getSessionToken(sessionToken -> {
            resetLatestResults();
            isSessionPreparingToLaunch = false;
            latestProcessor = new LivenessCheckProcessor(sessionToken, this);
        }));
    }

    // Perform Enrollment, generating a username each time to guarantee uniqueness.
    public void onEnrollUserPressed(View v) {
        isSessionPreparingToLaunch = true;

        utils.fadeOutMainUIAndPrepareForFaceTecSDK(() -> getSessionToken(sessionToken -> {
            resetLatestResults();
            isSessionPreparingToLaunch = false;
            if(latestExternalDatabaseRefID.length() <= 0){
                latestExternalDatabaseRefID = externalID;
            }
            latestProcessor = new EnrollmentProcessor(sessionToken, this);
        }));
    }

    // Perform Verification, using the username from Enrollment.
    public void onVerifyUserPressed(View v) {
        isSessionPreparingToLaunch = true;

        if(latestExternalDatabaseRefID == null || latestExternalDatabaseRefID.length() == 0){
            utils.displayStatus("Please enroll first before trying verification.");
            return;
        }

        utils.fadeOutMainUIAndPrepareForFaceTecSDK(() -> getSessionToken(sessionToken -> {
            resetLatestResults();
            isSessionPreparingToLaunch = false;
            latestProcessor = new VerificationProcessor(sessionToken, this);
        }));
    }

    // Perform Photo ID Match, generating a username each time to guarantee uniqueness.
    public void onPhotoIDMatchPressed(View view) {
        isSessionPreparingToLaunch = true;

        utils.fadeOutMainUIAndPrepareForFaceTecSDK(() -> getSessionToken(sessionToken -> {
            resetLatestResults();
            isSessionPreparingToLaunch = false;
            if(latestExternalDatabaseRefID.length() <= 0){
                latestExternalDatabaseRefID = externalID;
            }
            latestProcessor = new PhotoIDMatchProcessor(sessionToken, this);
        }));
    }

    // Perform Photo ID Scan, generating a username each time to guarantee uniqueness.
    public void onPhotoIDScanOnlyPressed(View view) {
        isSessionPreparingToLaunch = true;

        utils.fadeOutMainUIAndPrepareForFaceTecSDK(() -> getSessionToken(sessionToken -> {
            resetLatestResults();
            isSessionPreparingToLaunch = false;
            latestProcessor = new PhotoIDScanProcessor(sessionToken, this);
        }));
    }

    public void onVocalGuidanceSettingsButtonPressed(View v) {
        utils.setVocalGuidanceMode();
    }

    // When the FaceTec SDK is completely done, you receive control back here.
    // Since you have already handled all results in your Processor code, how you proceed here is up to you and how your App works.
    // In general, there was either a Success, or there was some other case where you cancelled out.
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(latestProcessor == null) {
            return;
        }

        if(latestSessionResult != null && latestSessionResult.getStatus() != null) {
            Log.d("FaceTecSDKSampleApp", "Session Status: " + latestSessionResult.getStatus().toString());
        }

        if(latestIDScanResult != null && latestIDScanResult.getStatus() != null) {
            Log.d("FaceTecSDKSampleApp", "ID Scan Status: " + latestIDScanResult.getStatus().toString());
        }

        utils.displayStatus("See logs for more details.");
        utils.fadeInMainUI();

        // At this point, you have already handled all results in your Processor code.
        if(!this.latestProcessor.isSuccess()) {
            // Reset the enrollment identifier.
            latestExternalDatabaseRefID = "";
        }else{
            externalDatabaseRefID = latestExternalDatabaseRefID;
        }

        // MainActivity reference removed for library compatibility
        // Intent intent = new Intent(SampleAppActivity.this, MainActivity.class);
        // startActivity(intent);
        finish();
    }

    //
    // DEVELOPER NOTE:  This is a convenience function for demonstration purposes only so the Sample App can have access to the latest session results.
    // In your code, you may not even want or need to do this.
    //
    public void setLatestSessionResult(FaceTecSessionResult sessionResult) {
        this.latestSessionResult = sessionResult;
    }
    //
    // DEVELOPER NOTE:  This is a convenience function for demonstration purposes only so the Sample App can have access to the latest id scan results.
    // In your code, you may not even want or need to do this.
    //
    public void setLatestIDScanResult(FaceTecIDScanResult idScanResult) {
        this.latestIDScanResult = idScanResult;
    }

    private void resetLatestResults() {
        this.latestSessionResult = null;
        this.latestIDScanResult = null;
    }

    public String getLatestExternalDatabaseRefID() {
        return latestExternalDatabaseRefID;
    }

    public void configureInitialSampleAppUI() {
        // Data binding removed for library compatibility
        // activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        //utils.setupAllButtons();

        //Esconde os botões para o usuário não ter acesso
        utils.hiddenAllButtons();

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
    public void onViewAuditTrailPressed(View view) {
        utils.showAuditTrailImages();
    }

    // Present settings action sheet, allowing user to select a new app theme (pre-made FaceTecCustomization configuration).
    public void onThemeSelectionPressed(View view) {
        utils.showThemeSelectionMenu();
    }

    interface SessionTokenCallback {
        void onSessionTokenReceived(String sessionToken);
    }

    public void getSessionToken(final SessionTokenCallback sessionTokenCallback) {
        utils.showSessionTokenConnectionText();

        // Do the network call and handle result
        okhttp3.Request request = new okhttp3.Request.Builder()
                .header("X-Device-Key", Config.DeviceKeyIdentifier)
                .header("User-Agent", FaceTecSDK.createFaceTecAPIUserAgentString(""))
                .header("X-User-Agent", FaceTecSDK.createFaceTecAPIUserAgentString(""))
                .header("Authorization", "Bearer "+Config.accessToken)
                .url(Config.BaseURL + "/session-token")
                .get()
                .build();

        NetworkingHelpers.getApiClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.d("FaceTecSDKSampleApp", "Exception raised while attempting HTTPS call.");

                // If this comes from HTTPS cancel call, don't set the sub code to NETWORK_ERROR.
                if(!e.getMessage().equals(NetworkingHelpers.OK_HTTP_RESPONSE_CANCELED)) {
                    utils.handleErrorGettingServerSessionToken();
                }
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                String responseString = response.body().string();
                response.body().close();
                try {
                    JSONObject responseJSON = new JSONObject(responseString);
                    if(responseJSON.has("sessionToken")) {
                        utils.hideSessionTokenConnectionText();
                        sessionTokenCallback.onSessionTokenReceived(responseJSON.getString("sessionToken"));
                    }
                    else {
                        utils.handleErrorGettingServerSessionToken();
                    }
                }
                catch(JSONException e) {
                    e.printStackTrace();
                    Log.d("FaceTecSDKSampleApp", "Exception raised while attempting to parse JSON result.");
                    utils.handleErrorGettingServerSessionToken();
                }
            }
        });
    }
}