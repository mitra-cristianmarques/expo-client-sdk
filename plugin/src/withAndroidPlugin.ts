import { ExpoConfig } from '@expo/config-types'
import { AndroidConfig, withAndroidManifest, withMainApplication } from '@expo/config-plugins'

export function withAndroidPlugin(config: ExpoConfig) {
  config = AndroidConfig.Permissions.withPermissions(config, [
    'android.permission.CAMERA',
  ])

  // Add the native module to MainApplication.java/kt
  config = withMainApplication(config, (config) => {
    if (!config.modResults.contents.includes('MitraBiometricsSdkPackage')) {
      // Add import statement if not present
      if (!config.modResults.contents.includes('import br.com.mitra.biometricsdk.MitraBiometricsSdkPackage;')) {
        // Find the last import statement and add ours after it
        const lastImportIndex = config.modResults.contents.lastIndexOf('import ')
        if (lastImportIndex !== -1) {
          const nextLineIndex = config.modResults.contents.indexOf('\n', lastImportIndex)
          const insertIndex = nextLineIndex + 1
          
          const newImportLine = 'import br.com.mitra.biometricsdk.MitraBiometricsSdkPackage;\n'
          
          config.modResults.contents = 
            config.modResults.contents.slice(0, insertIndex) + 
            newImportLine + 
            config.modResults.contents.slice(insertIndex)
        }
      }
      
      // Handle both Java and Kotlin files
      const isKotlin = config.modResults.contents.includes('fun getPackages()')
      
      if (isKotlin) {
        // For Kotlin files, add the package to the packages list
        if (config.modResults.contents.includes('val packages = PackageList(this).packages')) {
          // Find the packages line and add our package after it
          const packagesLineIndex = config.modResults.contents.indexOf('val packages = PackageList(this).packages')
          const nextLineIndex = config.modResults.contents.indexOf('\n', packagesLineIndex)
          const insertIndex = nextLineIndex + 1
          
          const newPackageLine = '            packages.add(MitraBiometricsSdkPackage())\n'
          
          config.modResults.contents = 
            config.modResults.contents.slice(0, insertIndex) + 
            newPackageLine + 
            config.modResults.contents.slice(insertIndex)
        }
      } else {
        // For Java files, look for the getPackages method
        const getPackagesMethodRegex = /public\s+List<ReactPackage>\s+getPackages\(\)\s*\{[\s\S]*?\}/g
        const getPackagesMatch = config.modResults.contents.match(getPackagesMethodRegex)
        
        if (getPackagesMatch) {
          // Find where to insert our package
          const methodContent = getPackagesMatch[0]
          const packagesListRegex = /packages\.add\(new\s+([^)]+)\)/g
          const packagesMatches = methodContent.match(packagesListRegex)
          
          if (packagesMatches && packagesMatches.length > 0) {
            // Find the last package.add in the method
            const lastPackageInMethod = packagesMatches[packagesMatches.length - 1]
            const lastPackageIndex = config.modResults.contents.lastIndexOf(lastPackageInMethod)
            const insertIndex = config.modResults.contents.indexOf(')', lastPackageIndex) + 1
            
            const newPackageLine = '\n        packages.add(new MitraBiometricsSdkPackage());'
            
            config.modResults.contents = 
              config.modResults.contents.slice(0, insertIndex) + 
              newPackageLine + 
              config.modResults.contents.slice(insertIndex)
          } else {
            // If no packages found, add after the method opening
            const methodStartIndex = config.modResults.contents.indexOf('public List<ReactPackage> getPackages()')
            if (methodStartIndex !== -1) {
              const braceIndex = config.modResults.contents.indexOf('{', methodStartIndex)
              if (braceIndex !== -1) {
                const insertIndex = braceIndex + 1
                
                const newPackageLine = '\n        packages.add(new MitraBiometricsSdkPackage());'
                
                config.modResults.contents = 
                  config.modResults.contents.slice(0, insertIndex) + 
                  newPackageLine + 
                  config.modResults.contents.slice(insertIndex)
              }
            }
          }
        }
      }
    }
    
    return config
  })

  return config
}
