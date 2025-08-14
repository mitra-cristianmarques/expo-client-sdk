import { ExpoConfig } from '@expo/config-types'

export function withIosPlugin(config: ExpoConfig) {
  // // Copy FaceTec.framework (if available)
  // config = withDangerousMod(config, [
  //   'ios',
  //   (config) => {
  //     const frameworkDir = path.join(
  //       config.modRequest.platformProjectRoot,
  //       'FaceTec.framework',
  //     )
  //     const srcFramework = path.resolve(
  //       config.modRequest.projectRoot,
  //       'node_modules/@mitra-cristianmarques/expo-biometrics-sdk/lib/ios/FaceTec.framework',
  //     )
  //     if (fs.existsSync(srcFramework)) {
  //       fs.cpSync(srcFramework, frameworkDir, { recursive: true })
  //     } else {
  //       console.warn(
  //         'FaceTec.framework not found in lib/ios. Ensure it is included.',
  //       )
  //     }
  //     return config
  //   },
  // ])

  // // Embed framework in Xcode project
  // config = withXcodeProject(config, (config) => {
  //   const xcodeProject = config.modResults
  //   if (
  //     fs.existsSync(
  //       path.join(config.modRequest.platformProjectRoot, 'FaceTec.framework'),
  //     )
  //   ) {
  //     xcodeProject.addFramework('FaceTec.framework', { link: true })
  //   }
  //   return config
  // })

  // // Add camera permission
  // config = withInfoPlist(config, (config) => {
  //   config.modResults.NSCameraUsageDescription =
  //     'This app requires camera access for facial verification.'
  //   return config
  // })

  return config
}
