import fs from 'fs'
import path from 'path'
import {
  withAndroidManifest,
  withAppBuildGradle,
  withInfoPlist,
  withXcodeProject,
  withDangerousMod,
  createRunOncePlugin,
} from '@expo/config-plugins'
import { ExpoConfig } from '@expo/config-types'

// Helper to copy files
function copyFile(src: string, dest: string) {
  if (!fs.existsSync(dest)) {
    fs.mkdirSync(path.dirname(dest), { recursive: true })
  }
  fs.copyFileSync(src, dest)
}

function withFaceTecAndroid(config: ExpoConfig) {
  // Copy .aar to android/app/libs
  config = withDangerousMod(config, [
    'android',
    (config) => {
      const libsDir = path.join(
        config.modRequest.platformProjectRoot,
        'app/libs'
      )
      const srcAar = path.resolve(
        config.modRequest.projectRoot,
        'node_modules/@mitra-cristianmarques/expo-client-sdk/lib/android/facetec-sdk-9.7.80.aar'
      )
      copyFile(srcAar, path.join(libsDir, 'facetec-sdk-9.7.80.aar'))
      return config
    },
  ])

  // Copy localization files to android/app/src/main/res/values*
  config = withDangerousMod(config, [
    'android',
    (config) => {
      const resDirBase = path.join(
        config.modRequest.platformProjectRoot,
        'app/src/main/res'
      )
      const stringsSrcDir = path.resolve(
        config.modRequest.projectRoot,
        'node_modules/@mitra-cristianmarques/expo-client-sdk/lib/android'
      )
      const stringFiles = [
        'strings-af.xml',
        'strings-ar.xml',
        'strings-de.xml',
        'strings-el.xml',
        'strings-es.xml',
        'strings-fr.xml',
        'strings-kk.xml',
        'strings-nb.xml',
        'strings-pt-rBR.xml',
        'strings-ru.xml',
        'strings.xml',
      ]
      stringFiles.forEach((file) => {
        const langCode =
          file === 'strings.xml'
            ? 'values'
            : `values-${file.replace('strings-', '').replace('.xml', '')}`
        const resDir = path.join(resDirBase, langCode)
        copyFile(path.join(stringsSrcDir, file), path.join(resDir, file))
      })
      return config
    },
  ])

  // Add .aar dependency to app/build.gradle
  config = withAppBuildGradle(config, (config) => {
    if (config.modResults.language === 'groovy') {
      config.modResults.contents += `\ndependencies {\n  implementation files('libs/facetec-sdk-9.7.80.aar')\n}`
    } else {
      throw new Error('Unsupported build.gradle language')
    }
    return config
  })

  // Add camera permission
  config = withAndroidManifest(config, (config) => {
    if (config.modResults.manifest.application) {
      const mainApplication: any = config.modResults.manifest.application[0]
      mainApplication['uses-permission'] = [
        ...(mainApplication['uses-permission'] || []),
        { $: { 'android:name': 'android.permission.CAMERA' } },
      ]
    }
    return config
  })

  return config
}

function withFaceTecIos(config: ExpoConfig) {
  // Copy FaceTec.framework (if available)
  config = withDangerousMod(config, [
    'ios',
    (config) => {
      const frameworkDir = path.join(
        config.modRequest.platformProjectRoot,
        'FaceTec.framework'
      )
      const srcFramework = path.resolve(
        config.modRequest.projectRoot,
        'node_modules/@mitra-cristianmarques/expo-client-sdk/lib/ios/FaceTec.framework'
      )
      if (fs.existsSync(srcFramework)) {
        fs.cpSync(srcFramework, frameworkDir, { recursive: true })
      } else {
        console.warn(
          'FaceTec.framework not found in lib/ios. Ensure it is included.'
        )
      }
      return config
    },
  ])

  // Embed framework in Xcode project
  config = withXcodeProject(config, (config) => {
    const xcodeProject = config.modResults
    if (
      fs.existsSync(
        path.join(config.modRequest.platformProjectRoot, 'FaceTec.framework')
      )
    ) {
      xcodeProject.addFramework('FaceTec.framework', { link: true })
    }
    return config
  })

  // Add camera permission
  config = withInfoPlist(config, (config) => {
    config.modResults.NSCameraUsageDescription =
      'This app requires camera access for facial verification.'
    return config
  })

  return config
}

export default createRunOncePlugin(
  (config) => {
    config = withFaceTecAndroid(config)
    config = withFaceTecIos(config)
    return config
  },
  'withFaceTec',
  '1.0.0'
)
