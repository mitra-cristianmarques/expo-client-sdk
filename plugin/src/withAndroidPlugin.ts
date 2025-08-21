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

  // Configure autolinking by adding to settings.gradle
  config = withDangerousMod(config, [
    'android',
    (config) => {
      const settingsGradlePath = path.join(config.modRequest.platformProjectRoot, 'settings.gradle')
      
      if (fs.existsSync(settingsGradlePath)) {
        let settingsGradleContent = fs.readFileSync(settingsGradlePath, 'utf8')
        
        // Check if our module is already included
        if (!settingsGradleContent.includes('expo-biometrics-sdk')) {
          // Try to find the includeProject section first
          let insertIndex = -1
          let newIncludeLine = ''
          
          if (settingsGradleContent.includes('includeProject')) {
            // Find the includeProject section and add our module
            const includeProjectIndex = settingsGradleContent.indexOf('includeProject')
            insertIndex = settingsGradleContent.indexOf('\n', includeProjectIndex) + 1
            newIncludeLine = "includeProject(':expo-biometrics-sdk')\n"
          } else if (settingsGradleContent.includes('include ')) {
            // Find the include section and add our module
            const includeIndex = settingsGradleContent.indexOf('include ')
            insertIndex = settingsGradleContent.indexOf('\n', includeIndex) + 1
            newIncludeLine = "include ':expo-biometrics-sdk'\n"
          } else {
            // If no include statements found, add at the end before the last line
            const lines = settingsGradleContent.split('\n')
            insertIndex = settingsGradleContent.length - lines[lines.length - 1].length - 1
            newIncludeLine = "\ninclude ':expo-biometrics-sdk'\n"
          }
          
          if (insertIndex !== -1) {
            settingsGradleContent = 
              settingsGradleContent.slice(0, insertIndex) + 
              newIncludeLine + 
              settingsGradleContent.slice(insertIndex)
            
            // Also add the project configuration
            const projectConfigIndex = settingsGradleContent.indexOf('project(')
            if (projectConfigIndex !== -1) {
              const projectConfigEndIndex = settingsGradleContent.indexOf('\n', projectConfigIndex)
              const insertProjectIndex = projectConfigEndIndex + 1
              
              const projectConfigLine = "project(':expo-biometrics-sdk').projectDir = new File(rootProject.projectDir, 'node_modules/expo-biometrics-sdk/android')\n"
              
              settingsGradleContent = 
                settingsGradleContent.slice(0, insertProjectIndex) + 
                projectConfigLine + 
                settingsGradleContent.slice(insertProjectIndex)
            }
            
            fs.writeFileSync(settingsGradlePath, settingsGradleContent)
            console.log('✅ Added expo-biometrics-sdk to settings.gradle for autolinking')
          } else {
            console.warn('⚠️ Could not find suitable location to add expo-biometrics-sdk to settings.gradle')
          }
        } else {
          console.log('✅ expo-biometrics-sdk already configured in settings.gradle')
        }
      } else {
        console.error('❌ settings.gradle not found at:', settingsGradlePath)
      }
      
      return config
    }
  ])

  // Copy FaceTec SDK AAR to client app's libs directory
  config = withDangerousMod(config, [
    'android',
    (config) => {
      const clientLibsDir = path.join(config.modRequest.platformProjectRoot, 'app', 'libs')
      
      // Try multiple possible paths for the FaceTec SDK AAR
      const possiblePaths = [
        // Path 1: From the source SDK directory (when developing locally)
        path.join(config.modRequest.projectRoot, 'node_modules', 'expo-biometrics-sdk', 'android', 'libs', 'facetec', 'facetec-sdk-full.aar'),
        // Path 2: From the installed package (when published)
        path.join(config.modRequest.projectRoot, 'node_modules', 'expo-biometrics-sdk', 'android', 'libs', 'facetec', 'facetec-sdk-full.aar'),
        // Path 3: Alternative path structure
        path.join(config.modRequest.projectRoot, 'node_modules', 'expo-biometrics-sdk', 'android', 'libs', 'facetec-sdk-full.aar'),
        // Path 4: From the plugin's own directory
        path.join(__dirname, '..', '..', 'android', 'libs', 'facetec', 'facetec-sdk-full.aar'),
        // Path 5: From the plugin's build directory
        path.join(__dirname, '..', '..', 'android', 'libs', 'facetec', 'facetec-sdk-full.aar')
      ]
      
      // Create libs directory if it doesn't exist
      if (!fs.existsSync(clientLibsDir)) {
        fs.mkdirSync(clientLibsDir, { recursive: true })
      }
      
      // Try to find and copy the FaceTec SDK AAR
      let aarCopied = false
      for (const aarPath of possiblePaths) {
        if (fs.existsSync(aarPath)) {
          const destAar = path.join(clientLibsDir, 'facetec-sdk-full.aar')
          fs.copyFileSync(aarPath, destAar)
          console.log('✅ Copied FaceTec SDK AAR to client app from:', aarPath)
          aarCopied = true
          break
        }
      }
      
      if (!aarCopied) {
        console.error('❌ FaceTec SDK AAR not found in any of the expected paths:')
        possiblePaths.forEach((path, index) => {
          console.error(`   Path ${index + 1}:`, path)
        })
        throw new Error('FaceTec SDK AAR file not found. Please ensure the SDK is properly built and contains the AAR file.')
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
        if (!buildGradleContent.includes('facetec-sdk-full.aar')) {
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
      
      // Also try to add to the new architecture packages if available
      if (config.modResults.contents.includes('getReactPackageList()')) {
        // This is the new architecture method
        if (!config.modResults.contents.includes('MitraBiometricsSdkPackage')) {
          const methodStartIndex = config.modResults.contents.indexOf('getReactPackageList()')
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
    
    return config
  })

  return config
}
