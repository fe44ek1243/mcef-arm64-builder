package com.mcef.arm64.wrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WebDisplays Compatibility Module
 * Intercepts WebDisplays mod calls and routes to appropriate browser implementation
 */
public class WebDisplaysCompat {
    private static final Logger LOGGER = LoggerFactory.getLogger("WebDisplaysCompat");
    
    /**
     * Create a web display screen
     * Called by WebDisplays mod when opening a browser
     */
    public static WebViewBrowser createWebDisplay(String url) throws Exception {
        LOGGER.info("WebDisplays: Creating display for {}", url);
        
        // Normalize URL
        if (url == null || url.isEmpty()) {
            url = "http://localhost:3876/";
        }
        
        // Ensure wrapper is initialized
        MCEFWebViewWrapper.initialize();
        
        // Create browser via wrapper
        return MCEFWebViewWrapper.createBrowser(url);
    }
    
    /**
     * Create a web display with custom size
     */
    public static WebViewBrowser createWebDisplay(String url, int width, int height) throws Exception {
        LOGGER.info("WebDisplays: Creating display {} x {} for {}", width, height, url);
        
        WebViewBrowser browser = createWebDisplay(url);
        browser.setSize(width, height);
        return browser;
    }
    
    /**
     * Handle WebDisplays block interaction
     */
    public static void handleWebDisplayInteraction(WebViewBrowser browser, int x, int y) throws Exception {
        if (browser == null) {
            LOGGER.warn("WebDisplays: Browser is null");
            return;
        }
        
        LOGGER.debug("WebDisplays: Interaction at ({}, {})", x, y);
        browser.click(x, y);
    }
    
    /**
     * Handle scroll on web display
     */
    public static void handleWebDisplayScroll(WebViewBrowser browser, int direction, int amount) throws Exception {
        if (browser == null) {
            LOGGER.warn("WebDisplays: Browser is null");
            return;
        }
        
        int dy = direction > 0 ? amount : -amount;
        LOGGER.debug("WebDisplays: Scroll dy={}", dy);
        browser.scroll(0, dy);
    }
    
    /**
     * Get system info for compatibility check
     */
    public static String getSystemInfo() {
        return String.format(
            "MCEF Wrapper: Platform=%s, Active=%s, Info=%s",
            MCEFWebViewWrapper.isAndroid() ? "Android" : "Desktop",
            MCEFWebViewWrapper.isWrapperActive(),
            MCEFWebViewWrapper.getWrapperInfo()
        );
    }
}
