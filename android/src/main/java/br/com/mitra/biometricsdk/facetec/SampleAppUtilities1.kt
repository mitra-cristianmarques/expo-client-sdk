package br.com.mitra.biometricsdk

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Handler
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.RelativeLayout
import br.com.mitra.biometricsdk.processors.Config
import br.com.mitra.biometricsdk.processors.ThemeHelpers
import com.facetec.sdk.FaceTecSDK
import com.facetec.sdk.FaceTecVocalGuidanceCustomization
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class SampleAppUtilities(private val sampleAppActivity: SampleAppActivity) {
    internal enum class VocalGuidanceMode {
        OFF,
        MINIMAL,
        FULL
    }

    private var vocalGuidanceOnPlayer: MediaPlayer? = null
    private var vocalGuidanceOffPlayer: MediaPlayer? = null
    var currentTheme: String = "Mitra"
    private val themeTransitionTextHandler: Handler? = null

    fun setupAllButtons() {
        // sampleAppActivity.runOnUiThread(new Runnable() {
        //     @Override
        //     public void run() {
        //         sampleAppActivity.activityMainBinding.enrollButton.setupButton(sampleAppActivity);
        //         sampleAppActivity.activityMainBinding.authButton.setupButton(sampleAppActivity);
        //         sampleAppActivity.activityMainBinding.livenessCheckButton.setupButton(sampleAppActivity);
        //         sampleAppActivity.activityMainBinding.identityCheckButton.setupButton(sampleAppActivity);
        //         sampleAppActivity.activityMainBinding.identityScanOnlyButton.setupButton(sampleAppActivity);
        //         sampleAppActivity.activityMainBinding.auditTrailImagesButton.setupButton(sampleAppActivity);
        //         sampleAppActivity.activityMainBinding.settingsButton.setupButton(sampleAppActivity);
        //     }
        // });
    }

    /** NEW  */
    fun hiddenAllButtons() {
        // sampleAppActivity.runOnUiThread(new Runnable() {
        //     @Override
        //     public void run() {
        //         sampleAppActivity.activityMainBinding.contentLayout.setVisibility(View.INVISIBLE);
        //     }
        // });
    }

    /** NEW  */
    fun showAllButtons() {
        // sampleAppActivity.runOnUiThread(new Runnable() {
        //     @Override
        //     public void run() {
        //         sampleAppActivity.activityMainBinding.contentLayout.setVisibility(View.VISIBLE);
        //     }
        // });
    }

    fun disableAllButtons() {
        // sampleAppActivity.runOnUiThread(new Runnable() {
        //     @Override
        //     public void run() {
        //         sampleAppActivity.activityMainBinding.enrollButton.setEnabled(false, true);
        //         sampleAppActivity.activityMainBinding.authButton.setEnabled(false, true);
        //         sampleAppActivity.activityMainBinding.livenessCheckButton.setEnabled(false, true);
        //         sampleAppActivity.activityMainBinding.identityCheckButton.setEnabled(false, true);
        //         sampleAppActivity.activityMainBinding.identityScanOnlyButton.setEnabled(false, true);
        //         sampleAppActivity.activityMainBinding.auditTrailImagesButton.setEnabled(false, true);
        //         sampleAppActivity.activityMainBinding.settingsButton.setEnabled(false, true);
        //     }
        // });
    }

    fun enableAllButtons() {
        // sampleAppActivity.runOnUiThread(new Runnable() {
        //     @Override
        //     public void run() {
        //         sampleAppActivity.activityMainBinding.enrollButton.setEnabled(true, true);
        //         sampleAppActivity.activityMainBinding.authButton.setEnabled(true, true);
        //         sampleAppActivity.activityMainBinding.livenessCheckButton.setEnabled(true, true);
        //         sampleAppActivity.activityMainBinding.identityCheckButton.setEnabled(true, true);
        //         sampleAppActivity.activityMainBinding.identityScanOnlyButton.setEnabled(true, true);
        //         sampleAppActivity.activityMainBinding.auditTrailImagesButton.setEnabled(true, true);
        //         sampleAppActivity.activityMainBinding.settingsButton.setEnabled(true, true);
        //     }
        // });
    }

    fun showSessionTokenConnectionText() {
        // themeTransitionTextHandler = new Handler();
        // themeTransitionTextHandler.postDelayed(new Runnable() {
        //     @Override
        //     public void run() {
        //         sampleAppActivity.activityMainBinding.themeTransitionText.animate().alpha(1f).setDuration(600);
        //     }
        // }, 3000);
    }

    fun hideSessionTokenConnectionText() {
        // themeTransitionTextHandler.removeCallbacksAndMessages(null);
        // themeTransitionTextHandler = null;
        // sampleAppActivity.runOnUiThread(new Runnable() {
        //     @Override
        //     public void run() {
        //         sampleAppActivity.activityMainBinding.themeTransitionText.animate().alpha(0f).setDuration(600);
        //     }
        // });
    }

    // Disable buttons to prevent hammering, fade out main interface elements, and shuffle the guidance images.
    fun fadeOutMainUIAndPrepareForFaceTecSDK(callback: Runnable?) {
        // disableAllButtons();
        // sampleAppActivity.runOnUiThread(new Runnable() {
        //     @Override
        //     public void run() {
        //         sampleAppActivity.activityMainBinding.vocalGuidanceSettingButton.animate().alpha(0f).setDuration(600).start();
        //         sampleAppActivity.activityMainBinding.themeTransitionImageView.animate().alpha(1f).setDuration(600).start();
        //         sampleAppActivity.activityMainBinding.contentLayout.animate().alpha(0f).setDuration(600).withEndAction(callback).start();
        //     }
        // });
    }

    fun fadeInMainUI() {
        // enableAllButtons();
        // sampleAppActivity.runOnUiThread(new Runnable() {
        //       @Override
        //       public void run() {
        //           sampleAppActivity.activityMainBinding.vocalGuidanceSettingButton.animate().alpha(1f).setDuration(600);
        //           sampleAppActivity.activityMainBinding.contentLayout.animate().alpha(1f).setDuration(600);
        //           sampleAppActivity.activityMainBinding.themeTransitionImageView.animate().alpha(0f).setDuration(600);
        //       }
        //     }
        // );
    }

    fun displayStatus(statusString: String) {
        Log.d("FaceTecSDKSampleApp", statusString)
        // sampleAppActivity.runOnUiThread(new Runnable() {
        //     @Override
        //     public void run() {
        //         sampleAppActivity.activityMainBinding.statusLabel.setText(statusString);
        //     }
        // });
    }

    fun showAuditTrailImages() {
        // Store audit trail images from latest session result for inspection
        val auditTrailAndIDScanImages = ArrayList<Bitmap?>()
        if (sampleAppActivity.latestSessionResult != null) {
            // convert the compressed base64 encoded audit trail images into bitmaps
            for (compressedBase64EncodedAuditTrailImage in sampleAppActivity.latestSessionResult.getAuditTrailCompressedBase64()) {
                val decodedString =
                    Base64.decode(compressedBase64EncodedAuditTrailImage, Base64.DEFAULT)
                val auditTrailImage =
                    BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                auditTrailAndIDScanImages.add(auditTrailImage)
            }
        }

        if (sampleAppActivity.latestIDScanResult != null && !sampleAppActivity.latestIDScanResult.getFrontImagesCompressedBase64()
                .isEmpty()
        ) {
            val decodedString = Base64.decode(
                sampleAppActivity.latestIDScanResult.getFrontImagesCompressedBase64().get(0),
                Base64.DEFAULT
            )
            val frontImage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            auditTrailAndIDScanImages.add(frontImage)
        }

        if (sampleAppActivity.latestIDScanResult != null && !sampleAppActivity.latestIDScanResult.getBackImagesCompressedBase64()
                .isEmpty()
        ) {
            val decodedString = Base64.decode(
                sampleAppActivity.latestIDScanResult.getBackImagesCompressedBase64().get(0),
                Base64.DEFAULT
            )
            val backImage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            auditTrailAndIDScanImages.add(backImage)
        }

        if (auditTrailAndIDScanImages.size <= 0) {
            displayStatus("No Audit Trail Images")
            return
        }

        for (i in auditTrailAndIDScanImages.indices.reversed()) {
            addDismissableImageToInterface(auditTrailAndIDScanImages.get(i))
        }
    }

    fun addDismissableImageToInterface(imageBitmap: Bitmap?) {
        val imageDialog = Dialog(sampleAppActivity)
        imageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        imageDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val imageView = ImageView(sampleAppActivity)
        imageView.setImageBitmap(imageBitmap)
        imageView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                imageDialog.dismiss()
            }
        })

        // Scale image to better fit device's display.
        val dm = DisplayMetrics()
        sampleAppActivity.getWindowManager().getDefaultDisplay().getMetrics(dm)
        val layout =
            RelativeLayout.LayoutParams(dm.widthPixels * 0.5.toInt(), dm.heightPixels * 0.5.toInt())
        imageDialog.addContentView(imageView, layout)
        imageDialog.show()
    }

    fun handleErrorGettingServerSessionToken() {
        hideSessionTokenConnectionText()
        displayStatus("Session could not be started due to an unexpected issue during the network request.")
        fadeInMainUI()
    }

    fun showThemeSelectionMenu() {
        val themes: Array<String>
        if (Config.wasSDKConfiguredWithConfigWizard == true) {
            themes = arrayOf<String>(
                "Config Wizard Theme",
                "FaceTec Theme",
                "Pseudo-Fullscreen",
                "Well-Rounded",
                "Bitcoin Exchange",
                "eKYC",
                "Sample Bank"
            )
        } else {
            themes = arrayOf<String>(
                "FaceTec Theme",
                "Pseudo-Fullscreen",
                "Well-Rounded",
                "Bitcoin Exchange",
                "eKYC",
                "Sample Bank"
            )
        }

        val builder = AlertDialog.Builder(
            ContextThemeWrapper(
                sampleAppActivity,
                android.R.style.Theme_Holo_Light
            )
        )
        builder.setTitle("Select a Theme:")
        builder.setItems(themes, object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, index: Int) {
                currentTheme = themes[index]
                ThemeHelpers.setAppTheme(sampleAppActivity, currentTheme)
                updateThemeTransitionView()
            }
        })
        builder.show()
    }

    fun updateThemeTransitionView() {
        var transitionViewImage = 0
        var transitionViewTextColor =
            Config.currentCustomization.getGuidanceCustomization().foregroundColor
        when (currentTheme) {
            "FaceTec Theme" -> {}
            "Config Wizard Theme" -> {}
            "Pseudo-Fullscreen" -> {}
            "Well-Rounded" -> {
                transitionViewImage = R.drawable.well_rounded_bg
                transitionViewTextColor =
                    Config.currentCustomization.getFrameCustomization().backgroundColor
            }

            "Bitcoin Exchange" -> {
                transitionViewImage = R.drawable.bitcoin_exchange_bg
                transitionViewTextColor =
                    Config.currentCustomization.getFrameCustomization().backgroundColor
            }

            "eKYC" -> transitionViewImage = R.drawable.ekyc_bg
            "Sample Bank" -> {
                transitionViewImage = R.drawable.sample_bank_bg
                transitionViewTextColor =
                    Config.currentCustomization.getFrameCustomization().backgroundColor
            }

            else -> {}
        }

        // sampleAppActivity.activityMainBinding.themeTransitionImageView.setImageResource(transitionViewImage);
        // sampleAppActivity.activityMainBinding.themeTransitionText.setTextColor(transitionViewTextColor);
    }

    fun setUpVocalGuidancePlayers() {
        vocalGuidanceOnPlayer = MediaPlayer.create(sampleAppActivity, R.raw.vocal_guidance_on)
        vocalGuidanceOffPlayer = MediaPlayer.create(sampleAppActivity, R.raw.vocal_guidance_off)
        vocalGuidanceMode = VocalGuidanceMode.OFF
    }

    fun setVocalGuidanceMode() {
        if (this.isDeviceMuted) {
            val alertDialog = AlertDialog.Builder(
                ContextThemeWrapper(
                    sampleAppActivity,
                    android.R.style.Theme_Holo_Light
                )
            ).create()
            alertDialog.setMessage("Vocal Guidance is disabled when the device is muted")
            alertDialog.setButton(
                AlertDialog.BUTTON_NEUTRAL, "OK",
                object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which: Int) {
                        dialog.dismiss()
                    }
                })
            alertDialog.show()
            return
        }

        if (vocalGuidanceOnPlayer!!.isPlaying() || vocalGuidanceOffPlayer!!.isPlaying()) {
            return
        }

        sampleAppActivity.runOnUiThread(object : Runnable {
            override fun run() {
                when (vocalGuidanceMode) {
                    VocalGuidanceMode.OFF -> {
                        vocalGuidanceMode = VocalGuidanceMode.MINIMAL
                        // sampleAppActivity.activityMainBinding.vocalGuidanceSettingButton.setImageResource(R.drawable.vocal_minimal);
                        vocalGuidanceOnPlayer!!.start()
                        Config.currentCustomization.vocalGuidanceCustomization.mode =
                            FaceTecVocalGuidanceCustomization.VocalGuidanceMode.MINIMAL_VOCAL_GUIDANCE
                    }

                    VocalGuidanceMode.MINIMAL -> {
                        vocalGuidanceMode = VocalGuidanceMode.FULL
                        // sampleAppActivity.activityMainBinding.vocalGuidanceSettingButton.setImageResource(R.drawable.vocal_full);
                        vocalGuidanceOnPlayer!!.start()
                        Config.currentCustomization.vocalGuidanceCustomization.mode =
                            FaceTecVocalGuidanceCustomization.VocalGuidanceMode.FULL_VOCAL_GUIDANCE
                    }

                    VocalGuidanceMode.FULL -> {
                        vocalGuidanceMode = VocalGuidanceMode.OFF
                        // sampleAppActivity.activityMainBinding.vocalGuidanceSettingButton.setImageResource(R.drawable.vocal_off);
                        vocalGuidanceOffPlayer!!.start()
                        Config.currentCustomization.vocalGuidanceCustomization.mode =
                            FaceTecVocalGuidanceCustomization.VocalGuidanceMode.NO_VOCAL_GUIDANCE
                    }
                }

                setVocalGuidanceSoundFiles()
                FaceTecSDK.setCustomization(Config.currentCustomization)
            }
        })
    }

    val isDeviceMuted: Boolean
        get() {
            val audio =
                (sampleAppActivity.getSystemService(Context.AUDIO_SERVICE)) as AudioManager
            if (audio.getStreamVolume(AudioManager.STREAM_MUSIC) == 0) {
                return true
            } else {
                return false
            }
        }

    companion object {
        var vocalGuidanceMode: VocalGuidanceMode = VocalGuidanceMode.OFF

        fun setVocalGuidanceSoundFiles() {
            Config.currentCustomization.vocalGuidanceCustomization.pleaseFrameYourFaceInTheOvalSoundFile =
                R.raw.please_frame_your_face_sound_file
            Config.currentCustomization.vocalGuidanceCustomization.pleaseMoveCloserSoundFile =
                R.raw.please_move_closer_sound_file
            Config.currentCustomization.vocalGuidanceCustomization.pleaseRetrySoundFile =
                R.raw.please_retry_sound_file
            Config.currentCustomization.vocalGuidanceCustomization.uploadingSoundFile =
                R.raw.uploading_sound_file
            Config.currentCustomization.vocalGuidanceCustomization.facescanSuccessfulSoundFile =
                R.raw.facescan_successful_sound_file
            Config.currentCustomization.vocalGuidanceCustomization.pleasePressTheButtonToStartSoundFile =
                R.raw.please_press_button_sound_file

            when (vocalGuidanceMode) {
                VocalGuidanceMode.OFF -> Config.currentCustomization.vocalGuidanceCustomization.mode =
                    FaceTecVocalGuidanceCustomization.VocalGuidanceMode.NO_VOCAL_GUIDANCE

                VocalGuidanceMode.MINIMAL -> Config.currentCustomization.vocalGuidanceCustomization.mode =
                    FaceTecVocalGuidanceCustomization.VocalGuidanceMode.MINIMAL_VOCAL_GUIDANCE

                VocalGuidanceMode.FULL -> Config.currentCustomization.vocalGuidanceCustomization.mode =
                    FaceTecVocalGuidanceCustomization.VocalGuidanceMode.FULL_VOCAL_GUIDANCE
            }
        }

        fun setOCRLocalization(context: Context) {
            // Set the strings to be used for group names, field names, and placeholder texts for the FaceTec ID Scan User OCR Confirmation Screen.
            // DEVELOPER NOTE: For this demo, we are using the template json file, 'FaceTec_OCR_Customization.json,' as the parameter in calling this API.
            // For the configureOCRLocalization API parameter, you may use any object that follows the same structure and key naming as the template json file, 'FaceTec_OCR_Customization.json'.
            try {
                val `is` = context.getAssets().open("FaceTec_OCR_Customization.json")
                val size = `is`.available()
                val buffer = ByteArray(size)
                `is`.read(buffer)
                `is`.close()
                val ocrLocalizationJSONString = String(buffer, charset("UTF-8"))
                val ocrLocalizationJSON = JSONObject(ocrLocalizationJSONString)

                FaceTecSDK.configureOCRLocalization(ocrLocalizationJSON)
            } catch (ex: IOException) {
                ex.printStackTrace()
            } catch (ex: JSONException) {
                ex.printStackTrace()
            }
        }
    }
}
