package com.mcef.arm64;

import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MCEF ARM64 Mod
 * Minecraft Chromium Embedded Framework for ARM64 devices
 * Compatible with Forge 1.20.1
 */
@Mod(McefArm64Mod.MOD_ID)
public class McefArm64Mod {
    public static final String MOD_ID = "mcef_arm64";
    private static final Logger LOGGER = LoggerFactory.getLogger("MCEF ARM64");

    public McefArm64Mod() {
        LOGGER.info("MCEF ARM64 Mod loaded!");
        LOGGER.info("OS: {}", System.getProperty("os.name"));
        LOGGER.info("Architecture: {}", System.getProperty("os.arch"));
    }
}
