package mitra.biometricsdk.facetec;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for FaceTecModule
 * 
 * To run these tests:
 * 1. In Android Studio: Right-click on the test file and select "Run"
 * 2. From command line: ./gradlew test
 * 3. From project root: cd native/android && ./gradlew test
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class FaceTecModuleTest {

    @Mock
    private ReactApplicationContext mockReactContext;
    
    @Mock
    private Promise mockPromise;
    
    @Mock
    private ReadableMap mockConfig;
    
    @Mock
    private ReadableMap mockOptions;
    
    private FaceTecModule faceTecModule;
    
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        faceTecModule = new FaceTecModule(mockReactContext);
    }
    
    // ============================================================================
    // BASIC MODULE TESTS
    // ============================================================================
    
    @Test
    public void testGetName() {
        String moduleName = faceTecModule.getName();
        assertEquals("FaceTecModule", moduleName);
    }
    
    @Test
    public void testModuleConstruction() {
        assertNotNull(faceTecModule);
    }
    
    // ============================================================================
    // SDK INITIALIZATION TESTS
    // ============================================================================
    
    @Test
    public void testInitializeSDK_Success() {
        // Setup mock config
        when(mockConfig.getString("licenseKey")).thenReturn("test-license-key");
        when(mockConfig.getString("serverUrl")).thenReturn("https://test.facetec.com");
        when(mockConfig.getString("deviceKeyIdentifier")).thenReturn("test-device-key");
        
        faceTecModule.initializeSDK(mockConfig, mockPromise);
        
        verify(mockPromise).resolve(any(WritableMap.class));
        verify(mockPromise, never()).reject(anyString(), anyString());
    }
    
    @Test
    public void testInitializeSDK_MissingLicenseKey() {
        // Setup mock config without license key
        when(mockConfig.getString("licenseKey")).thenReturn(null);
        
        faceTecModule.initializeSDK(mockConfig, mockPromise);
        
        verify(mockPromise).reject("INVALID_CONFIG", "License key is required");
        verify(mockPromise, never()).resolve(any());
    }
    
    @Test
    public void testInitializeSDK_EmptyLicenseKey() {
        // Setup mock config with empty license key
        when(mockConfig.getString("licenseKey")).thenReturn("");
        
        faceTecModule.initializeSDK(mockConfig, mockPromise);
        
        verify(mockPromise).reject("INVALID_CONFIG", "License key is required");
        verify(mockPromise, never()).resolve(any());
    }
    
    @Test
    public void testIsSDKInitialized_BeforeInit() {
        faceTecModule.isSDKInitialized(mockPromise);
        verify(mockPromise).resolve(false);
    }
    
    @Test
    public void testIsSDKInitialized_AfterInit() {
        // First initialize
        when(mockConfig.getString("licenseKey")).thenReturn("test-key");
        faceTecModule.initializeSDK(mockConfig, mockPromise);
        
        // Reset mock
        reset(mockPromise);
        
        // Then check if initialized
        faceTecModule.isSDKInitialized(mockPromise);
        verify(mockPromise).resolve(true);
    }
    
    // ============================================================================
    // VERIFICATION SESSION TESTS
    // ============================================================================
    
    @Test
    public void testStartVerification_SDKNotInitialized() {
        // Setup mock options
        when(mockOptions.getString("sessionToken")).thenReturn("test-token");
        
        faceTecModule.startVerification(mockOptions, mockPromise);
        
        verify(mockPromise).reject("SDK_NOT_INITIALIZED", "Please initialize the SDK first");
    }
    
    @Test
    public void testStartVerification_Success() {
        // First initialize SDK
        when(mockConfig.getString("licenseKey")).thenReturn("test-key");
        faceTecModule.initializeSDK(mockConfig, mockPromise);
        reset(mockPromise);
        
        // Setup mock options
        when(mockOptions.getString("sessionToken")).thenReturn("test-session-token");
        when(mockOptions.getString("serverUrl")).thenReturn("https://test.facetec.com");
        when(mockOptions.getString("theme")).thenReturn("dark");
        when(mockOptions.getString("language")).thenReturn("es");
        
        faceTecModule.startVerification(mockOptions, mockPromise);
        
        verify(mockPromise).resolve(any(WritableMap.class));
        verify(mockPromise, never()).reject(anyString(), anyString());
    }
    
    @Test
    public void testStartVerification_MissingSessionToken() {
        // First initialize SDK
        when(mockConfig.getString("licenseKey")).thenReturn("test-key");
        faceTecModule.initializeSDK(mockConfig, mockPromise);
        reset(mockPromise);
        
        // Setup mock options without session token
        when(mockOptions.getString("sessionToken")).thenReturn(null);
        
        faceTecModule.startVerification(mockOptions, mockPromise);
        
        verify(mockPromise).reject("INVALID_SESSION", "Session token is required");
    }
    
    @Test
    public void testGetSessionStatus_NoSession() {
        faceTecModule.getSessionStatus(mockPromise);
        verify(mockPromise).reject("NO_SESSION", "No active session");
    }
    
    @Test
    public void testGetSessionStatus_WithSession() {
        // First initialize SDK and start session
        when(mockConfig.getString("licenseKey")).thenReturn("test-key");
        faceTecModule.initializeSDK(mockConfig, mockPromise);
        reset(mockPromise);
        
        when(mockOptions.getString("sessionToken")).thenReturn("test-token");
        faceTecModule.startVerification(mockOptions, mockPromise);
        reset(mockPromise);
        
        // Get session status
        faceTecModule.getSessionStatus(mockPromise);
        verify(mockPromise).resolve(any(WritableMap.class));
    }
    
    @Test
    public void testCancelSession_NoSession() {
        faceTecModule.cancelSession(mockPromise);
        verify(mockPromise).reject("NO_SESSION", "No active session to cancel");
    }
    
    @Test
    public void testCancelSession_WithSession() {
        // First initialize SDK and start session
        when(mockConfig.getString("licenseKey")).thenReturn("test-key");
        faceTecModule.initializeSDK(mockConfig, mockPromise);
        reset(mockPromise);
        
        when(mockOptions.getString("sessionToken")).thenReturn("test-token");
        faceTecModule.startVerification(mockOptions, mockPromise);
        reset(mockPromise);
        
        // Cancel session
        faceTecModule.cancelSession(mockPromise);
        verify(mockPromise).resolve(any(WritableMap.class));
    }
    
    // ============================================================================
    // DEVICE COMPATIBILITY TESTS
    // ============================================================================
    
    @Test
    public void testIsDeviceSupported() {
        faceTecModule.isDeviceSupported(mockPromise);
        verify(mockPromise).resolve(any(WritableMap.class));
    }
    
    @Test
    public void testGetDeviceInfo() {
        faceTecModule.getDeviceInfo(mockPromise);
        verify(mockPromise).resolve(any(WritableMap.class));
    }
    
    // ============================================================================
    // CONFIGURATION TESTS
    // ============================================================================
    
    @Test
    public void testUpdateConfig_SDKNotInitialized() {
        when(mockConfig.getString("serverUrl")).thenReturn("https://new.facetec.com");
        
        faceTecModule.updateConfig(mockConfig, mockPromise);
        verify(mockPromise).reject("SDK_NOT_INITIALIZED", "Please initialize the SDK first");
    }
    
    @Test
    public void testUpdateConfig_Success() {
        // First initialize SDK
        when(mockConfig.getString("licenseKey")).thenReturn("test-key");
        faceTecModule.initializeSDK(mockConfig, mockPromise);
        reset(mockPromise);
        
        // Update config
        when(mockConfig.getString("serverUrl")).thenReturn("https://new.facetec.com");
        when(mockConfig.getString("theme")).thenReturn("light");
        when(mockConfig.getString("language")).thenReturn("fr");
        
        faceTecModule.updateConfig(mockConfig, mockPromise);
        verify(mockPromise).resolve(any(WritableMap.class));
    }
    
    @Test
    public void testGetConfig() {
        faceTecModule.getConfig(mockPromise);
        verify(mockPromise).resolve(any(WritableMap.class));
    }
    
    // ============================================================================
    // UTILITY METHOD TESTS
    // ============================================================================
    
    @Test
    public void testGetSDKVersion() {
        faceTecModule.getSDKVersion(mockPromise);
        verify(mockPromise).resolve("9.0.0");
    }
    
    @Test
    public void testHasActiveSession_NoSession() {
        faceTecModule.hasActiveSession(mockPromise);
        verify(mockPromise).resolve(false);
    }
    
    @Test
    public void testHasActiveSession_WithSession() {
        // First initialize SDK and start session
        when(mockConfig.getString("licenseKey")).thenReturn("test-key");
        faceTecModule.initializeSDK(mockConfig, mockPromise);
        reset(mockPromise);
        
        when(mockOptions.getString("sessionToken")).thenReturn("test-token");
        faceTecModule.startVerification(mockOptions, mockPromise);
        reset(mockPromise);
        
        // Check if has active session
        faceTecModule.hasActiveSession(mockPromise);
        verify(mockPromise).resolve(true);
    }
    
    @Test
    public void testGetCurrentSessionId_NoSession() {
        faceTecModule.getCurrentSessionId(mockPromise);
        verify(mockPromise).resolve(null);
    }
    
    @Test
    public void testGetCurrentSessionId_WithSession() {
        // First initialize SDK and start session
        when(mockConfig.getString("licenseKey")).thenReturn("test-key");
        faceTecModule.initializeSDK(mockConfig, mockPromise);
        reset(mockPromise);
        
        when(mockOptions.getString("sessionToken")).thenReturn("test-token");
        faceTecModule.startVerification(mockOptions, mockPromise);
        reset(mockPromise);
        
        // Get current session ID
        faceTecModule.getCurrentSessionId(mockPromise);
        verify(mockPromise).resolve(any(String.class));
    }
    
    @Test
    public void testClearSessionData() {
        faceTecModule.clearSessionData(mockPromise);
        verify(mockPromise).resolve(any(WritableMap.class));
    }
    
    // ============================================================================
    // INTEGRATION TESTS
    // ============================================================================
    
    @Test
    public void testFullWorkflow() {
        // 1. Initialize SDK
        when(mockConfig.getString("licenseKey")).thenReturn("test-key");
        faceTecModule.initializeSDK(mockConfig, mockPromise);
        reset(mockPromise);
        
        // 2. Check if initialized
        faceTecModule.isSDKInitialized(mockPromise);
        verify(mockPromise).resolve(true);
        reset(mockPromise);
        
        // 3. Start verification
        when(mockOptions.getString("sessionToken")).thenReturn("test-token");
        faceTecModule.startVerification(mockOptions, mockPromise);
        verify(mockPromise).resolve(any(WritableMap.class));
        reset(mockPromise);
        
        // 4. Check session status
        faceTecModule.getSessionStatus(mockPromise);
        verify(mockPromise).resolve(any(WritableMap.class));
        reset(mockPromise);
        
        // 5. Cancel session
        faceTecModule.cancelSession(mockPromise);
        verify(mockPromise).resolve(any(WritableMap.class));
        reset(mockPromise);
        
        // 6. Verify no active session
        faceTecModule.hasActiveSession(mockPromise);
        verify(mockPromise).resolve(false);
    }
}
