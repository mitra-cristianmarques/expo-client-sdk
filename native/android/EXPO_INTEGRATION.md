# FaceTec Plugin - Expo Integration Guide

This guide shows you how to use your FaceTec plugin from Expo/React Native applications. The plugin provides a complete bridge between your JavaScript code and the native FaceTec SDK.

## 🚀 **Quick Start Example**

Here's a complete example of how to use the plugin in your Expo app:

```typescript
import { NativeModules } from 'react-native';

const { FaceTecModule } = NativeModules;

// Initialize the SDK
const initializeFaceTec = async () => {
  try {
    const config = {
      licenseKey: 'your-facetec-license-key',
      serverUrl: 'https://your-server.com/facetec',
      deviceKeyIdentifier: 'your-device-key'
    };
    
    const result = await FaceTecModule.initializeSDK(config);
    console.log('SDK initialized:', result);
    
    // Check if initialized
    const isInitialized = await FaceTecModule.isSDKInitialized();
    console.log('SDK initialized:', isInitialized);
    
  } catch (error) {
    console.error('Failed to initialize SDK:', error);
  }
};

// Start a verification session
const startVerification = async () => {
  try {
    const options = {
      sessionToken: 'your-session-token-from-server',
      serverUrl: 'https://your-server.com/facetec',
      theme: 'dark', // 'light', 'dark', or 'auto'
      language: 'en'  // 'en', 'es', 'fr', etc.
    };
    
    const result = await FaceTecModule.startVerification(options);
    console.log('Verification started:', result);
    
    // Store session ID for later use
    const sessionId = result.sessionId;
    
  } catch (error) {
    console.error('Failed to start verification:', error);
  }
};
```

## 📱 **Complete Usage Examples**

### **1. Basic Setup and Initialization**

```typescript
import React, { useEffect, useState } from 'react';
import { View, Text, Button, Alert } from 'react-native';
import { NativeModules } from 'react-native';

const { FaceTecModule } = NativeModules;

const FaceTecScreen = () => {
  const [isInitialized, setIsInitialized] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    checkSDKStatus();
  }, []);

  const checkSDKStatus = async () => {
    try {
      const status = await FaceTecModule.isSDKInitialized();
      setIsInitialized(status);
    } catch (error) {
      console.error('Error checking SDK status:', error);
    }
  };

  const initializeSDK = async () => {
    setIsLoading(true);
    try {
      const config = {
        licenseKey: 'your-license-key-here',
        serverUrl: 'https://your-server.com/facetec',
        deviceKeyIdentifier: 'your-device-key'
      };
      
      const result = await FaceTecModule.initializeSDK(config);
      console.log('SDK initialized successfully:', result);
      
      setIsInitialized(true);
      Alert.alert('Success', 'FaceTec SDK initialized successfully!');
      
    } catch (error) {
      console.error('SDK initialization failed:', error);
      Alert.alert('Error', `Failed to initialize SDK: ${error.message}`);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <View style={{ flex: 1, padding: 20, justifyContent: 'center' }}>
      <Text style={{ fontSize: 24, marginBottom: 20, textAlign: 'center' }}>
        FaceTec Integration
      </Text>
      
      <Text style={{ marginBottom: 20, textAlign: 'center' }}>
        SDK Status: {isInitialized ? '✅ Initialized' : '❌ Not Initialized'}
      </Text>
      
      {!isInitialized && (
        <Button
          title={isLoading ? 'Initializing...' : 'Initialize SDK'}
          onPress={initializeSDK}
          disabled={isLoading}
        />
      )}
      
      {isInitialized && (
        <Text style={{ color: 'green', textAlign: 'center', marginTop: 20 }}>
          Ready to use FaceTec!
        </Text>
      )}
    </View>
  );
};
```

### **2. Complete Verification Flow**

```typescript
import React, { useState } from 'react';
import { View, Text, Button, TextInput, Alert, ScrollView } from 'react-native';
import { NativeModules } from 'react-native';

const { FaceTecModule } = NativeModules;

const VerificationScreen = () => {
  const [sessionToken, setSessionToken] = useState('');
  const [isVerifying, setIsVerifying] = useState(false);
  const [currentSession, setCurrentSession] = useState(null);
  const [sessionStatus, setSessionStatus] = useState('');

  const startVerification = async () => {
    if (!sessionToken.trim()) {
      Alert.alert('Error', 'Please enter a session token');
      return;
    }

    setIsVerifying(true);
    try {
      const options = {
        sessionToken: sessionToken.trim(),
        serverUrl: 'https://your-server.com/facetec',
        theme: 'auto',
        language: 'en'
      };
      
      const result = await FaceTecModule.startVerification(options);
      console.log('Verification started:', result);
      
      setCurrentSession(result);
      setSessionStatus('Verification in progress...');
      
      Alert.alert('Success', `Verification started! Session ID: ${result.sessionId}`);
      
    } catch (error) {
      console.error('Verification failed:', error);
      Alert.alert('Error', `Verification failed: ${error.message}`);
    } finally {
      setIsVerifying(false);
    }
  };

  const checkSessionStatus = async () => {
    try {
      const status = await FaceTecModule.getSessionStatus();
      setSessionStatus(JSON.stringify(status, null, 2));
    } catch (error) {
      setSessionStatus(`Error: ${error.message}`);
    }
  };

  const cancelSession = async () => {
    try {
      const result = await FaceTecModule.cancelSession();
      console.log('Session cancelled:', result);
      
      setCurrentSession(null);
      setSessionStatus('');
      Alert.alert('Success', 'Session cancelled successfully');
      
    } catch (error) {
      console.error('Failed to cancel session:', error);
      Alert.alert('Error', `Failed to cancel session: ${error.message}`);
    }
  };

  const checkDeviceSupport = async () => {
    try {
      const support = await FaceTecModule.isDeviceSupported();
      Alert.alert('Device Support', 
        `Supported: ${support.supported}\nReason: ${support.reason}`
      );
    } catch (error) {
      Alert.alert('Error', `Failed to check device support: ${error.message}`);
    }
  };

  const getDeviceInfo = async () => {
    try {
      const info = await FaceTecModule.getDeviceInfo();
      Alert.alert('Device Info', 
        `Manufacturer: ${info.manufacturer}\nModel: ${info.model}\nAndroid: ${info.androidVersion}`
      );
    } catch (error) {
      Alert.alert('Error', `Failed to get device info: ${error.message}`);
    }
  };

  return (
    <ScrollView style={{ flex: 1, padding: 20 }}>
      <Text style={{ fontSize: 24, marginBottom: 20, textAlign: 'center' }}>
        Face Verification
      </Text>
      
      {/* Session Token Input */}
      <Text style={{ fontSize: 16, marginBottom: 10 }}>Session Token:</Text>
      <TextInput
        style={{
          borderWidth: 1,
          borderColor: '#ccc',
          padding: 10,
          marginBottom: 20,
          borderRadius: 5
        }}
        value={sessionToken}
        onChangeText={setSessionToken}
        placeholder="Enter your session token"
        multiline
      />
      
      {/* Action Buttons */}
      <Button
        title={isVerifying ? 'Starting...' : 'Start Verification'}
        onPress={startVerification}
        disabled={isVerifying || !sessionToken.trim()}
      />
      
      <View style={{ height: 20 }} />
      
      <Button
        title="Check Session Status"
        onPress={checkSessionStatus}
        disabled={!currentSession}
      />
      
      <View style={{ height: 20 }} />
      
      <Button
        title="Cancel Session"
        onPress={cancelSession}
        disabled={!currentSession}
        color="red"
      />
      
      <View style={{ height: 20 }} />
      
      <Button
        title="Check Device Support"
        onPress={checkDeviceSupport}
      />
      
      <View style={{ height: 20 }} />
      
      <Button
        title="Get Device Info"
        onPress={getDeviceInfo}
      />
      
      {/* Session Status Display */}
      {sessionStatus && (
        <View style={{ marginTop: 20 }}>
          <Text style={{ fontSize: 16, marginBottom: 10 }}>Session Status:</Text>
          <Text style={{ 
            backgroundColor: '#f0f0f0', 
            padding: 10, 
            borderRadius: 5,
            fontFamily: 'monospace'
          }}>
            {sessionStatus}
          </Text>
        </View>
      )}
      
      {/* Current Session Info */}
      {currentSession && (
        <View style={{ marginTop: 20 }}>
          <Text style={{ fontSize: 16, marginBottom: 10 }}>Current Session:</Text>
          <Text style={{ 
            backgroundColor: '#e8f5e8', 
            padding: 10, 
            borderRadius: 5,
            fontFamily: 'monospace'
          }}>
            {JSON.stringify(currentSession, null, 2)}
          </Text>
        </View>
      )}
    </ScrollView>
  );
};
```

### **3. Advanced Configuration Management**

```typescript
import React, { useState, useEffect } from 'react';
import { View, Text, Button, TextInput, Alert, Switch } from 'react-native';
import { NativeModules } from 'react-native';

const { FaceTecModule } = NativeModules;

const ConfigurationScreen = () => {
  const [config, setConfig] = useState({
    serverUrl: 'https://your-server.com/facetec',
    theme: 'auto',
    language: 'en'
  });
  const [currentConfig, setCurrentConfig] = useState(null);

  useEffect(() => {
    loadCurrentConfig();
  }, []);

  const loadCurrentConfig = async () => {
    try {
      const config = await FaceTecModule.getConfig();
      setCurrentConfig(config);
    } catch (error) {
      console.error('Failed to load config:', error);
    }
  };

  const updateConfiguration = async () => {
    try {
      const result = await FaceTecModule.updateConfig(config);
      console.log('Configuration updated:', result);
      
      Alert.alert('Success', 'Configuration updated successfully!');
      loadCurrentConfig(); // Reload current config
      
    } catch (error) {
      console.error('Failed to update config:', error);
      Alert.alert('Error', `Failed to update configuration: ${error.message}`);
    }
  };

  const clearSessionData = async () => {
    try {
      const result = await FaceTecModule.clearSessionData();
      console.log('Session data cleared:', result);
      
      Alert.alert('Success', 'Session data cleared successfully!');
      
    } catch (error) {
      console.error('Failed to clear session data:', error);
      Alert.alert('Error', `Failed to clear session data: ${error.message}`);
    }
  };

  return (
    <View style={{ flex: 1, padding: 20 }}>
      <Text style={{ fontSize: 24, marginBottom: 20, textAlign: 'center' }}>
        Configuration
      </Text>
      
      {/* Server URL */}
      <Text style={{ fontSize: 16, marginBottom: 10 }}>Server URL:</Text>
      <TextInput
        style={{
          borderWidth: 1,
          borderColor: '#ccc',
          padding: 10,
          marginBottom: 20,
          borderRadius: 5
        }}
        value={config.serverUrl}
        onChangeText={(text) => setConfig({ ...config, serverUrl: text })}
        placeholder="https://your-server.com/facetec"
      />
      
      {/* Theme Selection */}
      <Text style={{ fontSize: 16, marginBottom: 10 }}>Theme:</Text>
      <View style={{ flexDirection: 'row', marginBottom: 20 }}>
        {['light', 'dark', 'auto'].map((theme) => (
          <Button
            key={theme}
            title={theme.charAt(0).toUpperCase() + theme.slice(1)}
            onPress={() => setConfig({ ...config, theme })}
            color={config.theme === theme ? '#007AFF' : '#ccc'}
          />
        ))}
      </View>
      
      {/* Language Selection */}
      <Text style={{ fontSize: 16, marginBottom: 10 }}>Language:</Text>
      <View style={{ flexDirection: 'row', marginBottom: 20, flexWrap: 'wrap' }}>
        {['en', 'es', 'fr', 'de', 'it', 'pt'].map((lang) => (
          <Button
            key={lang}
            title={lang.toUpperCase()}
            onPress={() => setConfig({ ...config, language: lang })}
            color={config.language === lang ? '#007AFF' : '#ccc'}
          />
        ))}
      </View>
      
      {/* Update Configuration */}
      <Button
        title="Update Configuration"
        onPress={updateConfiguration}
      />
      
      <View style={{ height: 20 }} />
      
      {/* Clear Session Data */}
      <Button
        title="Clear Session Data"
        onPress={clearSessionData}
        color="orange"
      />
      
      {/* Current Configuration Display */}
      {currentConfig && (
        <View style={{ marginTop: 30 }}>
          <Text style={{ fontSize: 16, marginBottom: 10 }}>Current Configuration:</Text>
          <Text style={{ 
            backgroundColor: '#f0f0f0', 
            padding: 10, 
            borderRadius: 5,
            fontFamily: 'monospace'
          }}>
            {JSON.stringify(currentConfig, null, 2)}
          </Text>
        </View>
      )}
    </View>
  );
};
```

## 🔧 **Available Methods Reference**

### **Core SDK Methods**
- `initializeSDK(config)` - Initialize the FaceTec SDK
- `isSDKInitialized()` - Check if SDK is initialized

### **Verification Methods**
- `startVerification(options)` - Start a verification session
- `getSessionStatus()` - Get current session status
- `cancelSession()` - Cancel current session

### **Device & Compatibility**
- `isDeviceSupported()` - Check device compatibility
- `getDeviceInfo()` - Get device information

### **Configuration**
- `updateConfig(config)` - Update SDK configuration
- `getConfig()` - Get current configuration

### **Utility Methods**
- `getSDKVersion()` - Get SDK version
- `hasActiveSession()` - Check for active session
- `getCurrentSessionId()` - Get current session ID
- `clearSessionData()` - Clear all session data

## 📱 **Error Handling Best Practices**

```typescript
const handleFaceTecError = (error: any) => {
  switch (error.code) {
    case 'SDK_NOT_INITIALIZED':
      // Re-initialize the SDK
      initializeFaceTec();
      break;
      
    case 'INVALID_CONFIG':
      // Show configuration error
      Alert.alert('Configuration Error', error.message);
      break;
      
    case 'VERIFICATION_ERROR':
      // Handle verification failure
      Alert.alert('Verification Failed', error.message);
      break;
      
    case 'NO_SESSION':
      // No active session
      console.log('No active session');
      break;
      
    default:
      // Handle unknown errors
      Alert.alert('Error', error.message || 'An unknown error occurred');
  }
};

// Usage in try-catch blocks
try {
  const result = await FaceTecModule.startVerification(options);
  // Handle success
} catch (error) {
  handleFaceTecError(error);
}
```

## 🎯 **Next Steps for Production**

1. **Replace TODO comments** with actual FaceTec SDK calls
2. **Add proper error handling** for network failures
3. **Implement session persistence** for app restarts
4. **Add analytics and logging** for user behavior
5. **Test on multiple devices** to ensure compatibility
6. **Implement proper security measures** for production use

## 🚀 **Testing Your Integration**

Use the test runner script to verify your plugin works correctly:

```bash
cd native/android
./run-tests.sh all
```

This will run all tests and ensure your plugin methods work as expected before integrating with Expo!
