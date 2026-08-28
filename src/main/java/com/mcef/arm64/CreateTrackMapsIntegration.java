package com.mcef.arm64;

import com.mcef.arm64.wrapper.MCEFWebViewWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Create: Trackmaps Integration
 * Ensures Create: Trackmaps localhost:3876 works with MCEF wrapper
 */
public class CreateTrackMapsIntegration {
    private static final Logger LOGGER = LoggerFactory.getLogger("CreateTrackMapsIntegration");
    
    private static final String TRACKMAPS_URL = "http://localhost:3876/";
    private static final int TRACKMAPS_PORT = 3876;
    
    /**
     * Initialize Trackmaps integration
     */
    public static void initialize() {
        LOGGER.info("CreateTrackMapsIntegration: Initializing");
        
        try {
            // Initialize MCEF wrapper first
            MCEFWebViewWrapper.initialize();
            
            // Log integration info
            LOGGER.info("CreateTrackMapsIntegration: Ready");
            LOGGER.info("Trackmaps localhost: {}", TRACKMAPS_URL);
            LOGGER.info("Wrapper info: {}", MCEFWebViewWrapper.getWrapperInfo());
            
            if (MCEFWebViewWrapper.isAndroid()) {
                LOGGER.info("CreateTrackMapsIntegration: Android mode - localhost bridge active");
            } else {
                LOGGER.info("CreateTrackMapsIntegration: Desktop mode - standard MCEF");
            }
            
        } catch (Exception e) {
            LOGGER.error("CreateTrackMapsIntegration: Failed to initialize", e);
        }
    }
    
    /**
     * Open Trackmaps in WebDisplays
     */
    public static void openTrackMaps() {
        LOGGER.info("CreateTrackMapsIntegration: Opening Trackmaps");
        
        try {
            MCEFWebViewWrapper.createBrowser(TRACKMAPS_URL);
            LOGGER.info("CreateTrackMapsIntegration: Trackmaps opened successfully");
        } catch (Exception e) {
            LOGGER.error("CreateTrackMapsIntegration: Failed to open Trackmaps", e);
        }
    }
    
    /**
     * Check if Trackmaps server is running
     */
    public static boolean isTrackMapsRunning() {
        try {
            java.net.Socket socket = new java.net.Socket("127.0.0.1", TRACKMAPS_PORT);
            socket.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
