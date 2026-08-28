package com.mcef.arm64;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafxmod.FXModLanguageProvider;
import net.minecraftforge.fxmod.FXModLoadingContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MCEF ARM64 Mod
 * Minecraft Chromium Embedded Framework for ARM64 devices (Android, ARM64 Linux)
 * Compatible with Forge 1.20.1
 */
@Mod(McefArm64Mod.MOD_ID)
public class McefArm64Mod {
    public static final String MOD_ID = "mcef_arm64";
    private static final Logger LOGGER = LoggerFactory.getLogger("MCEF ARM64");
    
    public McefArm64Mod() {
        IEventBus modEventBus = FXModLoadingContext.getInstance().getModEventBus();
        
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
            // Load native JNI libraries
            String arch = System.getProperty("os.arch");
            LOGGER.info("Detected architecture: {}", arch);
            
            if (arch.equals("aarch64") || arch.equals("arm64")) {
                LOGGER.info("Loading ARM64 native libraries...");
                // Native library loading handled by McefJniLoader
                McefJniLoader.loadLibraries();
                LOGGER.info("Native libraries loaded successfully");
            } else {
                LOGGER.warn("MCEF ARM64 is designed for ARM64, but running on: {}", arch);
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
