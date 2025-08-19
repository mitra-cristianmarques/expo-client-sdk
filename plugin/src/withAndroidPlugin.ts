import { ExpoConfig } from '@expo/config-types'
import { AndroidConfig, withAndroidManifest, withMainApplication, withDangerousMod } from '@expo/config-plugins'
import * as fs from 'fs'
import * as path from 'path'

export function withAndroidPlugin(config: ExpoConfig) {
  config = AndroidConfig.Permissions.withPermissions(config, [
    'android.permission.CAMERA',
  ])

  // Add the SampleAppActivity to AndroidManifest.xml
  config = withAndroidManifest(config, (config) => {
    if (!config.modResults.manifest.application) {
      config.modResults.manifest.application = [];
    }
    
    const application = config.modResults.manifest.application[0];
    if (application && !application.activity) {
      application.activity = [];
    }
    
    if (application && application.activity) {
      // Check if SampleAppActivity is already declared
      const hasSampleAppActivity = application.activity.some(
        (activity: any) => activity.$ && activity.$.name === 'br.com.mitra.biometricsdk.SampleAppActivity'
      );
      
      if (!hasSampleAppActivity) {
        application.activity.push({
          $: {
            'android:name': 'br.com.mitra.biometricsdk.SampleAppActivity',
            'android:exported': 'false',
            'android:theme': '@android:style/Theme.NoTitleBar.Fullscreen'
          }
        });
      }
    }
    
    return config;
  });

  // Copy FaceTec SDK AAR to client app's libs directory
  config = withDangerousMod(config, [
    'android',
    (config) => {
      const clientLibsDir = path.join(config.modRequest.platformProjectRoot, 'app', 'libs')
      const sdkLibsDir = path.join(config.modRequest.projectRoot, 'node_modules', 'expo-biometrics-sdk', 'android', 'libs', 'facetec')
      
      // Create libs directory if it doesn't exist
      if (!fs.existsSync(clientLibsDir)) {
        fs.mkdirSync(clientLibsDir, { recursive: true })
      }
      
      // Copy FaceTec SDK AAR
      const facetecAar = path.join(sdkLibsDir, 'facetec-sdk-9.7.80-minimal.aar')
      if (fs.existsSync(facetecAar)) {
        const destAar = path.join(clientLibsDir, 'facetec-sdk-9.7.80-minimal.aar')
        fs.copyFileSync(facetecAar, destAar)
        console.log('✅ Copied FaceTec SDK AAR to client app')
      } else {
        console.warn('⚠️ FaceTec SDK AAR not found in:', facetecAar)
      }
      
      return config
    }
  ])

  // Add FaceTec SDK dependency to build.gradle
  config = withDangerousMod(config, [
    'android',
    (config) => {
      const buildGradlePath = path.join(config.modRequest.platformProjectRoot, 'app', 'build.gradle')
      
      if (fs.existsSync(buildGradlePath)) {
        let buildGradleContent = fs.readFileSync(buildGradlePath, 'utf8')
        
        // Check if FaceTec SDK dependency is already added
        if (!buildGradleContent.includes('facetec-sdk-9.7.80-minimal.aar')) {
          // Find the dependencies block and add the FaceTec SDK
          const dependenciesIndex = buildGradleContent.indexOf('dependencies {')
          if (dependenciesIndex !== -1) {
            const insertIndex = dependenciesIndex + 'dependencies {'.length
            
            const facetecDependency = '\n    implementation fileTree(dir: "libs", include: ["*.aar"])\n'
            
            buildGradleContent = 
              buildGradleContent.slice(0, insertIndex) + 
              facetecDependency + 
              buildGradleContent.slice(insertIndex)
            
            fs.writeFileSync(buildGradlePath, buildGradleContent)
            console.log('✅ Added FaceTec SDK dependency to build.gradle')
          }
        }
      }
      
      return config
    }
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
