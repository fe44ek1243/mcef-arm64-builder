package com.mcef.arm64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * JNI Library Loader for MCEF ARM64
 * Handles loading native libraries compiled for ARM64 architecture
 */
public class McefJniLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger("MCEF JNI Loader");
    private static boolean loaded = false;
    
    public static void loadLibraries() {
        if (loaded) {
            LOGGER.debug("Native libraries already loaded");
            return;
        }
        
        try {
            String os = System.getProperty("os.name").toLowerCase();
            String arch = System.getProperty("os.arch").toLowerCase();
            
            LOGGER.info("Loading MCEF native libraries for: {}-{}", os, arch);
            
            if (arch.contains("aarch64") || arch.contains("arm64")) {
                loadArm64Libraries();
                loaded = true;
                LOGGER.info("MCEF native libraries loaded successfully");
            } else {
                LOGGER.warn("MCEF ARM64 requires ARM64 architecture, but running on: {}", arch);
                // Try to load anyway for compatibility
                loadFallbackLibraries();
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load MCEF native libraries", e);
            throw new RuntimeException("MCEF initialization failed", e);
        }
    }
    
    private static void loadArm64Libraries() throws Exception {
        // Load in dependency order
        loadLibrary("jcef_jni");      // Custom JNI wrapper
        loadLibrary("jcef");           // JCEF wrapper
        loadLibrary("cef");            // Chromium Embedded Framework
        
        LOGGER.info("All ARM64 libraries loaded");
    }
    
    private static void loadFallbackLibraries() throws Exception {
        LOGGER.warn("Loading fallback libraries (may not work properly)");
        loadLibrary("jcef_jni");
        loadLibrary("jcef");
    }
    
    private static void loadLibrary(String libName) throws Exception {
        try {
            // Try standard system library first
            System.loadLibrary(libName);
            LOGGER.debug("Loaded system library: {}", libName);
        } catch (UnsatisfiedLinkError e1) {
            LOGGER.debug("System library not found: {}, trying embedded", libName);
            
            // Try loading from JAR natives
            try {
                loadFromJar(libName);
                LOGGER.debug("Loaded embedded library: {}", libName);
            } catch (Exception e2) {
                LOGGER.error("Failed to load library: {}", libName, e2);
                throw new RuntimeException("Cannot load required library: " + libName, e2);
            }
        }
    }
    
    private static void loadFromJar(String libName) throws IOException {
        String osName = System.getProperty("os.name").toLowerCase();
        String nativeDir;
        String libFileName;
        
        if (osName.contains("win")) {
            nativeDir = "natives/arm64-v8a/windows";
            libFileName = libName + ".dll";
        } else if (osName.contains("mac")) {
            nativeDir = "natives/arm64-v8a/macos";
            libFileName = "lib" + libName + ".dylib";
        } else if (osName.contains("linux") || osName.contains("android")) {
            nativeDir = "natives/arm64-v8a/linux";
            libFileName = "lib" + libName + ".so";
        } else {
            throw new UnsupportedOperationException("Unsupported OS: " + osName);
        }
        
        String resourcePath = "/" + nativeDir + "/" + libFileName;
        LOGGER.info("Attempting to load from JAR: {}", resourcePath);
        
        try (InputStream is = McefJniLoader.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            
            // Create temporary file
            Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"), "mcef_arm64");
            Files.createDirectories(tempDir);
            
            Path tempLib = tempDir.resolve(libFileName);
            Files.copy(is, tempLib, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            
            // Make executable if on Unix
            if (!osName.contains("win")) {
                tempLib.toFile().setExecutable(true);
            }
            
            System.load(tempLib.toAbsolutePath().toString());
            LOGGER.info("Loaded native library from: {}", tempLib);
        }
    }
    
    public static boolean isLoaded() {
        return loaded;
    }
}
