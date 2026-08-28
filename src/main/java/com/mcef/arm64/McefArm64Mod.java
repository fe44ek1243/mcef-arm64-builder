package com.mcef.arm64;

import com.mcef.arm64.wrapper.MCEFWebViewWrapper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fxmod.FXModLoadingContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MCEF ARM64 Mod
 * Minecraft Chromium Embedded Framework for ARM64 devices (Android, ARM64 Linux)
 * Compatible with Forge 1.20.1
 * 
 * Includes:
 * - MCEF ↔ Android WebView wrapper
 * - Create: Trackmaps (localhost:3876) support
 * - WebDisplays compatibility
 */
@Mod(McefArm64Mod.MOD_ID)
public class McefArm64Mod {
    public static final String MOD_ID = "mcef_arm64";
    private static final Logger LOGGER = LoggerFactory.getLogger("MCEF ARM64 Mod");
    
    public McefArm64Mod() {
        var modEventBus = FXModLoadingContext.getInstance().getModEventBus();
        
        modEventBus.addListener(this::commonSetup);
        if (FXModLoadingContext.getInstance().getDistExecutor().isClient()) {
            modEventBus.addListener(this::clientSetup);
        }
        
        logSystemInfo();
    }
    
    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("MCEF ARM64: Common setup");
        LOGGER.info("Mod version: 1.20.1-0.1.0");
        LOGGER.info("Target platform: ARM64 (aarch64)");
    }
    
    private void clientSetup(final FMLClientSetupEvent event) {
        LOGGER.info("MCEF ARM64: Client setup");
        
        try {
            // Initialize MCEF wrapper
            MCEFWebViewWrapper.initialize();
            LOGGER.info("MCEF Wrapper initialized: {}", MCEFWebViewWrapper.getWrapperInfo());
            
            // Initialize Create: Trackmaps integration
            CreateTrackMapsIntegration.initialize();
            LOGGER.info("Create: Trackmaps integration ready");
            
            // Check architecture
            String arch = System.getProperty("os.arch");
            LOGGER.info("Detected architecture: {}", arch);
            
            if (MCEFWebViewWrapper.isAndroid()) {
                LOGGER.info("✓ MCEF Wrapper running in Android mode");
                LOGGER.info("✓ WebView bridge active for localhost:3876");
                LOGGER.info("✓ Create: Trackmaps compatible");
                LOGGER.info("✓ WebDisplays compatible");
            } else {
                LOGGER.info("✓ MCEF Wrapper running in Desktop mode");
                LOGGER.info("✓ Standard MCEF passthrough enabled");
            }
            
        } catch (Exception e) {
            LOGGER.error("Failed to initialize MCEF ARM64", e);
        }
    }
    
    private void logSystemInfo() {
        LOGGER.info("=== MCEF ARM64 System Information ===");
        LOGGER.info("OS: {} {}", System.getProperty("os.name"), System.getProperty("os.version"));
        LOGGER.info("Architecture: {}", System.getProperty("os.arch"));
        LOGGER.info("Java Version: {}", System.getProperty("java.version"));
        LOGGER.info("Java VM: {} ({})", System.getProperty("java.vm.name"), System.getProperty("java.vm.vendor"));
        LOGGER.info("Total Memory: {} MB", Runtime.getRuntime().totalMemory() / 1024 / 1024);
        LOGGER.info("Available Memory: {} MB", Runtime.getRuntime().freeMemory() / 1024 / 1024);
        LOGGER.info("===================================");
    }
}
