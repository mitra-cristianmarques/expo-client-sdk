import { ExpoConfig } from '@expo/config-types'
import { AndroidConfig } from 'expo/config-plugins'

export function withAndroidPlugin(config: ExpoConfig) {
  config = AndroidConfig.Permissions.withPermissions(config, [
    'android.permissions.CAMERA',
  ])
  return config
}
