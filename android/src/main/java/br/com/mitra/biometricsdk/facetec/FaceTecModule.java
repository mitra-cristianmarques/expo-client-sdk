package br.com.mitra.biometricsdk;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import br.com.mitra.biometricsdk.common.config.BiometricSDKConfiguration;
import br.com.mitra.biometricsdk.domain.models.SDKConfiguration;
import br.com.mitra.biometricsdk.domain.models.SDKTheme;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableMap;
import java.util.HashMap;
import java.util.Map;

public class FaceTecModule extends ReactContextBaseJavaModule {

    private Promise myPromise;

    private final ActivityEventListener mActivityEventListener = new BaseActivityEventListener() {

        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent intent) {

            try {
                WritableMap map = new WritableNativeMap();
                map.putBoolean("isSuccess", SampleAppActivity.latestProcessor.isSuccess());
                map.putString("externalDatabaseRefID", SampleAppActivity.externalDatabaseRefID);
                map.putString("documentData", SampleAppActivity.documentData);
                map.putString("faceScan", SampleAppActivity.faceScan);
                map.putString("documentFrontScan", SampleAppActivity.documentFrontScan);
                map.putString("documentBackScan", SampleAppActivity.documentBackScan);
                map.putString("requestEnroll", SampleAppActivity.requestEnroll);
                map.putString("responseEnroll", SampleAppActivity.responseEnroll);
                map.putInt("httpCode", SampleAppActivity.httpCode);
                map.putString("resquestFrontDocument", SampleAppActivity.resquestFrontDocument);
                map.putString("responseFrontDocument", SampleAppActivity.responseFrontDocument);
                map.putString("resquestBacktDocument", SampleAppActivity.resquestBacktDocument);
                map.putString("responseBackDocument", SampleAppActivity.responseBackDocument);

                //Depois de tudo terminado na SDK retorna para o React Native true ou false
                myPromise.resolve(map);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    FaceTecModule(ReactApplicationContext context) {
        super(context);

        //Cria um listener para quando terminar as ações na SDK
        context.addActivityEventListener(mActivityEventListener);
    }

    @Override
    public String getName() {
        return "MitraBiometricsSdk";
    }

    @ReactMethod
    public void Facetec(ReadableMap params, final Promise promise) {
        try {
            Log.d("FaceTecModule", "Facetec method called with params: " + params.toString());
            Intent intent = new Intent(getCurrentActivity(), SampleAppActivity.class);
            Log.i("FaceTecModule", "Facetec");
            String inicialModule = params.getString("actionFacetec");
            String externalID = params.getString("externalDatabaseRefID");
            String cpf = params.getString("cpf");

            Log.d("FaceTecModule", "Params - actionFacetec: " + inicialModule + ", externalID: " + externalID + ", cpf: " + cpf);

            //Seta variavel que será buscada na SampleAppActivity
            intent.putExtra("inicialModule", inicialModule);
            intent.putExtra("externalID", externalID);
            intent.putExtra("cpf", cpf);

            myPromise = promise;

            getCurrentActivity().startActivityForResult(intent, 1);
        } catch (Exception e) {
            myPromise.reject("Erro ao iniciar SDK", e);
            myPromise = null;
        }

    }
    @ReactMethod
    public void testConfiguration(final Promise promise) {
        try {
            Log.d("FaceTecModule", "testConfiguration method called");
            SDKConfiguration configuration = BiometricSDKConfiguration.INSTANCE.getConfiguration();
            if(configuration == null) {
                promise.resolve(null);
                return;
            }
            promise.resolve(configuration.serializeForReactNative());
        } catch (Exception e) {
            promise.reject("Fetch SDK configuration error", e);
        }
    }

    @ReactMethod
    public void configure(ReadableMap theme, ReadableMap options, final Promise promise) {
        try {
            Log.d("FaceTecModule", "configure method called");
            
            // Convert ReadableMap to Map using built-in method
            Map<String, Object> themeMap = theme != null ? theme.toHashMap() : new HashMap<>();
            Map<String, Object> optionsMap = options != null ? options.toHashMap() : new HashMap<>();
            
            SDKTheme configurationTheme = new SDKTheme().fromMap(themeMap);
            SDKConfiguration configuration = new SDKConfiguration().fromMap(optionsMap);
            BiometricSDKConfiguration.INSTANCE.initializeConfguration(configurationTheme, configuration);
            
            promise.resolve(true);
        } catch (Exception e) {
            Log.e("FaceTecModule", "Error in configure: " + e.getMessage());
            promise.reject("Error", e);
        }
    }


}
