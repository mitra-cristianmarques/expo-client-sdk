import { NativeModules } from 'react-native'
import { IFacetec } from './domain'


type biometricAction = 'authenticateUser' | 'enrollUser'

class FactecSDK {
  private static instance: FactecSDK
  private facetec: IFacetec

  private constructor() {
    this.facetec = NativeModules.FaceTecModule
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
    return this.facetec.Facetec({
      actionFacetec: action,
      externalDatabaseRefID: externalRefId,
      cpf: document,
    })
  }
}

export const MitraFacetecSDK = FactecSDK.getInstance()
