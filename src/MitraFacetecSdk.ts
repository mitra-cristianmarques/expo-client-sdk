import { NativeModules } from 'react-native'
import { IFacetec } from './domain'


type biometricAction = 'authenticateUser' | 'enrollUser'

class FactecSDK {
  private static instance: FactecSDK
  private facetec: IFacetec | null = null

  private constructor() {
    // Don't initialize facetec here - do it lazily when needed
  }

  private getFacetec(): IFacetec {
    if (!this.facetec) {
      this.facetec = NativeModules.MitraBiometricsSdk
      if (!this.facetec) {
        throw new Error('MitraBiometricsSdk native module not found. Make sure the plugin is properly configured.')
      }
    }
    return this.facetec
  }

  public static getInstance(): FactecSDK {
    if (!FactecSDK.instance) {
      FactecSDK.instance = new FactecSDK()
    }
    return FactecSDK.instance
  }

  public requestBiometricScreen(
    action: biometricAction,
    externalRefId: string,
    document?: string
  ) {
    const facetec = this.getFacetec()
    return facetec.Facetec({
      actionFacetec: action,
      externalDatabaseRefID: externalRefId,
      cpf: document,
    })
  }
}

// Export the class instead of an instance
export { FactecSDK }

// Create a lazy getter that only creates the instance when accessed
let _mitraFacetecSDK: FactecSDK | null = null

export const MitraFacetecSDK = {
  get requestBiometricScreen() {
    if (!_mitraFacetecSDK) {
      _mitraFacetecSDK = FactecSDK.getInstance()
    }
    return _mitraFacetecSDK.requestBiometricScreen.bind(_mitraFacetecSDK)
  }
}
