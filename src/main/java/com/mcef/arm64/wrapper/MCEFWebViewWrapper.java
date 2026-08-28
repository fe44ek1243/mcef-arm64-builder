package com.mcef.arm64.wrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * MCEF to Android WebView Wrapper
 * Provides compatibility layer for MCEF-dependent mods on ARM64 Android
 */
public class MCEFWebViewWrapper {
    private static final Logger LOGGER = LoggerFactory.getLogger("MCEF WebView Wrapper");
    
    private static final String LOCALHOST_HOST = "127.0.0.1";
    private static final int TRACKMAPS_PORT = 3876;
    private static final String TRACKMAPS_URL = "http://localhost:3876/";
    
    private static boolean isAndroid = false;
    private static boolean wrapperActive = false;
    private static LocalhostBridgeRelay bridgeRelay;
    private static Map<String, WebViewBrowser> openBrowsers = new HashMap<>();
    
    static {
        detectPlatform();
    }
    
    /**
     * Detect if running on Android
     */
    private static void detectPlatform() {
        String osName = System.getProperty("os.name").toLowerCase();
        String osArch = System.getProperty("os.arch").toLowerCase();
        
        LOGGER.info("MCEF Wrapper: Detecting platform...");
        LOGGER.info("OS: {} | Architecture: {}", osName, osArch);
        
        // Check for Android indicators
        if (osName.contains("linux") && (osArch.contains("aarch64") || osArch.contains("arm64"))) {
            // Could be Android or ARM64 Linux
            // Check for Android-specific properties
            if (System.getProperty("java.vendor").toLowerCase().contains("android") ||
                System.getenv("ANDROID_DATA") != null) {
                isAndroid = true;
                LOGGER.info("MCEF Wrapper: Detected ANDROID platform (ARM64)");
            } else {
                LOGGER.info("MCEF Wrapper: Detected ARM64 Linux (not Android)");
            }
        } else {
            LOGGER.info("MCEF Wrapper: Detected DESKTOP platform (using standard MCEF)");
        }
    }
    
    /**
     * Initialize wrapper
     */
    public static void initialize() {
        if (wrapperActive) {
            LOGGER.debug("MCEF Wrapper already initialized");
            return;
        }
        
        try {
            if (isAndroid) {
                LOGGER.info("MCEF Wrapper: Initializing WebView mode for Android ARM64");
                initializeWebViewMode();
            } else {
                LOGGER.info("MCEF Wrapper: Running in passthrough mode (standard MCEF)");
            }
            wrapperActive = true;
        } catch (Exception e) {
            LOGGER.error("MCEF Wrapper: Failed to initialize", e);
            throw new RuntimeException("MCEF Wrapper initialization failed", e);
        }
    }
    
    /**
     * Initialize WebView mode for Android
     */
    private static void initializeWebViewMode() throws IOException {
        LOGGER.info("MCEF Wrapper: Starting localhost bridge relay for port {}", TRACKMAPS_PORT);
        
        // Create and start localhost bridge
        bridgeRelay = new LocalhostBridgeRelay(LOCALHOST_HOST, TRACKMAPS_PORT);
        bridgeRelay.start();
        
        LOGGER.info("MCEF Wrapper: Localhost bridge relay started");
        LOGGER.info("MCEF Wrapper: Ready to intercept MCEF calls for localhost:{}", TRACKMAPS_PORT);
    }
    
    /**
     * Create a browser instance
     * This is the key interception point for WebDisplays
     */
    public static WebViewBrowser createBrowser(String url) throws Exception {
        LOGGER.info("MCEF Wrapper: Creating browser for URL: {}", url);
        
        if (!wrapperActive) {
            initialize();
        }
        
        WebViewBrowser browser;
        
        if (isAndroid) {
            LOGGER.debug("MCEF Wrapper: Creating Android WebView browser");
            browser = new AndroidWebViewBrowser(url, bridgeRelay);
        } else {
            LOGGER.debug("MCEF Wrapper: Creating desktop MCEF browser");
            browser = new DesktopMCEFBrowser(url);
        }
        
        // Register browser
        String browserId = "browser_" + System.nanoTime();
        openBrowsers.put(browserId, browser);
        browser.setId(browserId);
        
        LOGGER.info("MCEF Wrapper: Browser created with ID: {}", browserId);
        return browser;
    }
    
    /**
     * Get browser by ID
     */
    public static WebViewBrowser getBrowser(String browserId) {
        return openBrowsers.get(browserId);
    }
    
    /**
     * Close browser
     */
    public static void closeBrowser(String browserId) {
        WebViewBrowser browser = openBrowsers.remove(browserId);
        if (browser != null) {
            try {
                browser.close();
                LOGGER.info("MCEF Wrapper: Browser closed: {}", browserId);
            } catch (Exception e) {
                LOGGER.error("MCEF Wrapper: Error closing browser", e);
            }
        }
    }
    
    /**
     * Check if running on Android
     */
    public static boolean isAndroid() {
        return isAndroid;
    }
    
    /**
     * Check if wrapper is active
     */
    public static boolean isWrapperActive() {
        return wrapperActive;
    }
    
    /**
     * Get wrapper info
     */
    public static String getWrapperInfo() {
        return String.format(
            "MCEF WebView Wrapper v1.0 | Platform: %s | Active: %s | Browsers: %d",
            isAndroid ? "Android ARM64" : "Desktop",
            wrapperActive,
            openBrowsers.size()
        );
    }
    
    /**
     * Shutdown wrapper
     */
    public static void shutdown() {
        LOGGER.info("MCEF Wrapper: Shutting down...");
        
        // Close all browsers
        openBrowsers.values().forEach(browser -> {
            try {
                browser.close();
            } catch (Exception e) {
                LOGGER.error("Error closing browser during shutdown", e);
            }
        });
        openBrowsers.clear();
        
        // Stop bridge relay
        if (bridgeRelay != null) {
            try {
                bridgeRelay.stop();
            } catch (Exception e) {
                LOGGER.error("Error stopping bridge relay", e);
            }
        }
        
        wrapperActive = false;
        LOGGER.info("MCEF Wrapper: Shutdown complete");
    }
}
