import { Platform } from 'react-native';

let FaceTecModule;

if (Platform.OS === 'android') {
  try {
    FaceTecModule = require('@mitra-cristianmarques/expo-client-sdk-native');
  } catch (error) {
    console.warn('FaceTec native module not available:', error.message);
  }
} else if (Platform.OS === 'ios') {
  // iOS implementation will go here
  FaceTecModule = null;
} else {
  // Web implementation
  FaceTecModule = require('./web/core-sdk/FaceTecSDK.js/FaceTecSDK.js');
}

export default FaceTecModule;
export { FaceTecModule };
