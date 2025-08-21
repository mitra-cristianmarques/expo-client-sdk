package br.com.mitra.biometricsdk

import com.facebook.react.ReactPackage

class MitraBiometricsSdkPackage : ReactPackage {
    public override fun createViewManagers(reactContext: ReactApplicationContext?): MutableList<ViewManager?> {
        return mutableListOf<ViewManager?>()
    }

    public override fun createNativeModules(reactContext: ReactApplicationContext?): MutableList<NativeModule?> {
        val modules: MutableList<NativeModule?> = ArrayList<NativeModule?>()
        modules.add(FaceTecModule(reactContext))
        return modules
    }
}
