// File: android/app/src/main/java/br/com/mitra/multibeneficios/vinhedo/FaceTecPackage.java
package br.com.mitra.biometricsdk

import com.facebook.react.ReactPackage

class FaceTecPackage : ReactPackage {
    public override fun createNativeModules(reactContext: ReactApplicationContext?): MutableList<NativeModule?> {
        val modules: MutableList<NativeModule?> = ArrayList<NativeModule?>()
        modules.add(FaceTecModule(reactContext))
        return modules
    }

    public override fun createViewManagers(reactContext: ReactApplicationContext?): MutableList<ViewManager?> {
        return mutableListOf<ViewManager?>()
    }
}
