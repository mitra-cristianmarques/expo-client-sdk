package br.com.mitra.biometricsdk.facetec

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.facebook.react.bridge.*
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.modules.core.ActivityEventListener
import com.facebook.react.modules.core.BaseActivityEventListener
import br.com.mitra.biometricsdk.common.config.BiometricSDKConfiguration
import br.com.mitra.biometricsdk.domain.models.SDKConfiguration
import br.com.mitra.biometricsdk.domain.models.SDKTheme

class FaceTecModule internal constructor(context: ReactApplicationContext) :
    ReactContextBaseJavaModule(context) {
    private var myPromise: Promise? = null

    private val mActivityEventListener: ActivityEventListener =
        object : BaseActivityEventListener() {
            public override fun onActivityResult(
                activity: Activity?,
                requestCode: Int,
                resultCode: Int,
                intent: Intent?
            ) {
                try {
                    val map: WritableMap = WritableNativeMap()
                    map.putBoolean(
                        "isSuccess",
                        SampleAppActivity.Companion.latestProcessor.isSuccess()
                    )
                    map.putString(
                        "externalDatabaseRefID",
                        SampleAppActivity.Companion.externalDatabaseRefID
                    )
                    map.putString("documentData", SampleAppActivity.Companion.documentData)
                    map.putString("faceScan", SampleAppActivity.Companion.faceScan)
                    map.putString(
                        "documentFrontScan",
                        SampleAppActivity.Companion.documentFrontScan
                    )
                    map.putString("documentBackScan", SampleAppActivity.Companion.documentBackScan)
                    map.putString("requestEnroll", SampleAppActivity.Companion.requestEnroll)
                    map.putString("responseEnroll", SampleAppActivity.Companion.responseEnroll)
                    map.putInt("httpCode", SampleAppActivity.Companion.httpCode)
                    map.putString(
                        "resquestFrontDocument",
                        SampleAppActivity.Companion.resquestFrontDocument
                    )
                    map.putString(
                        "responseFrontDocument",
                        SampleAppActivity.Companion.responseFrontDocument
                    )
                    map.putString(
                        "resquestBacktDocument",
                        SampleAppActivity.Companion.resquestBacktDocument
                    )
                    map.putString(
                        "responseBackDocument",
                        SampleAppActivity.Companion.responseBackDocument
                    )

                    //Depois de tudo terminado na SDK retorna para o React Native true ou false
                    myPromise.resolve(map)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    init {
        //Cria um listener para quando terminar as ações na SDK
        context.addActivityEventListener(mActivityEventListener)
    }

    override fun getName(): String {
        return "MitraBiometricsSdk"
    }

    @ReactMethod
    fun Facetec(params: ReadableMap, promise: Promise?) {
        try {
            Log.d("FaceTecModule", "Facetec method called with params: " + params.toString())
            val intent: Intent = Intent(getCurrentActivity(), SampleAppActivity::class.java)
            Log.i("FaceTecModule", "Facetec")
            val inicialModule: String? = params.getString("actionFacetec")
            val externalID: String? = params.getString("externalDatabaseRefID")
            val cpf: String? = params.getString("cpf")

            Log.d(
                "FaceTecModule",
                "Params - actionFacetec: " + inicialModule + ", externalID: " + externalID + ", cpf: " + cpf
            )

            //Seta variavel que será buscada na SampleAppActivity
            intent.putExtra("inicialModule", inicialModule)
            intent.putExtra("externalID", externalID)
            intent.putExtra("cpf", cpf)

            myPromise = promise

            getCurrentActivity().startActivityForResult(intent, 1)
        } catch (e: Exception) {
            myPromise.reject("Erro ao iniciar SDK", e)
            myPromise = null
        }
    }

    @ReactMethod
    fun testConfiguration(promise: Promise) {
        try {
            Log.d("FaceTecModule", "testConfiguration method called")
            val configuration: SDKConfiguration? = BiometricSDKConfiguration.getConfiguration()
            if (configuration == null) {
                promise.resolve(null)
                return
            }
            promise.resolve(configuration.serializeForReactNative())
        } catch (e: Exception) {
            promise.reject("Fetch SDK configuration error", e)
        }
    }

    @ReactMethod
    fun configure(theme: ReadableMap?, options: ReadableMap?, promise: Promise) {
        try {
            Log.d("FaceTecModule", "configure method called")


            // Convert ReadableMap to Map using built-in method
            val themeMap: MutableMap<String, Any> =
                if (theme != null) theme.toHashMap() else HashMap<String, Any>()
            val optionsMap: MutableMap<String, Any> =
                if (options != null) options.toHashMap() else HashMap<String, Any>()

            val configurationTheme: SDKTheme = SDKTheme().fromMap(themeMap)
            val configuration: SDKConfiguration = SDKConfiguration().fromMap(optionsMap)
            BiometricSDKConfiguration.initializeConfiguration(configurationTheme, configuration)

            promise.resolve(true)
        } catch (e: Exception) {
            Log.e("FaceTecModule", "Error in configure: " + e.message)
            promise.reject("Error", e)
        }
    }
}
