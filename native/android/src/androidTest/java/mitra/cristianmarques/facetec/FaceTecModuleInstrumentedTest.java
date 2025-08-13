package androidTest.java.mitra.cristianmarques.facetec;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.runner.AndroidJUnitRunner;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import mitra.cristianmarques.facetec.FaceTecModule;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Instrumented tests for FaceTecModule
 * These tests run on an actual Android device or emulator
 * 
 * To run these tests:
 * 1. In Android Studio: Right-click on the test file and select "Run"
 * 2. From command line: ./gradlew connectedAndroidTest
 * 3. From project root: cd native/android && ./gradlew connectedAndroidTest
 */
@RunWith(AndroidJUnit4.class)
public class FaceTecModuleInstrumentedTest {

    @Mock
    private Promise mockPromise;
    
    private FaceTecModule faceTecModule;
    private ReactApplicationContext reactContext;
    
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Get the target context from the instrumentation
        Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        
        // Create a mock React context for testing
        reactContext = mock(ReactApplicationContext.class);
        when(reactContext.getApplicationContext()).thenReturn(targetContext);
        
        faceTecModule = new FaceTecModule(reactContext);
    }
    
    @Test
    public void testModuleName() {
        // Test that the module name is correct
        String moduleName = faceTecModule.getName();
        assertEquals("FaceTecModule", moduleName);
    }
    
    @Test
    public void testModuleConstruction() {
        // Test that the module is properly constructed
        assertNotNull(faceTecModule);
        assertNotNull(faceTecModule.getReactApplicationContext());
    }
    
    @Test
    public void testInitializeSDK_Success() {
        // Test successful SDK initialization
        faceTecModule.initializeSDK(mockPromise);
        
        // Verify that promise.resolve was called with success message
        verify(mockPromise).resolve("FaceTec SDK initialized successfully");
        verify(mockPromise, never()).reject(anyString(), anyString());
    }
    
    @Test
    public void testContextInjection() {
        // Test that the context is properly injected
        ReactApplicationContext context = faceTecModule.getReactApplicationContext();
        assertSame(reactContext, context);
    }
}
