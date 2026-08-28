# MCEF ARM64 Builder for Forge 1.20.1

**ARM64-compatible MCEF (Minecraft Chromium Embedded Framework) mod for Forge 1.20.1 with ZalithLauncher2 Android support**

## Overview

This project provides:
- ✅ ARM64 (aarch64) native library compilation
- ✅ Forge 1.20.1 compatibility patch
- ✅ ZalithLauncher2 integration
- ✅ Gradle build automation
- ✅ Native JNI bindings for ARM64 devices

## Prerequisites

- Java 17+
- Gradle 8.0+
- Android NDK r25c (for ARM64 compilation)
- Git

## Quick Start

### 1. Clone and Setup

```bash
git clone https://github.com/fe44ek1243/mcef-arm64-builder.git
cd mcef-arm64-builder
./gradlew build
```

### 2. Build for ARM64

```bash
./gradlew buildArm64
```

The compiled JAR will be in `build/libs/mcef-arm64-1.20.1.jar`

### 3. Install to ZalithLauncher2

Copy the JAR to your Minecraft mods folder:
```
~/.zalith/instances/[instance-name]/mods/mcef-arm64-1.20.1.jar
```

## Architecture

### Build Pipeline

```
Forge 1.20.1 Source
        ↓
  [Gradle Patch]
        ↓
  ARM64 Binaries (JNI)
        ↓
  Minecraft Mod JAR
        ↓
  ZalithLauncher2 Integration
```

## Files

- `build.gradle.kts` - Main Gradle build configuration
- `gradle/wrapper/` - Gradle wrapper for consistent builds
- `src/main/java/` - Java source code (mod logic)
- `src/main/cpp/` - Native C/C++ code (ARM64 compilation)
- `patches/` - Forge 1.20.1 compatibility patches

## Key Features

### ARM64 Support
- Native ARM64 (aarch64) JNI bindings
- Android NDK build integration
- Runtime architecture detection

### Forge 1.20.1 Compatibility
- Gradle ForgeGradle plugin v6.0.x
- NeoForge compatibility layer
- Mixin support for version-agnostic patches

### ZalithLauncher2 Integration
- Auto-detection of launcher paths
- Native library loading via `NativeLibraryLoader`
- Environment variable configuration for ARM64

## Compilation Stages

### Stage 1: Download Forge Sources
Automatically downloads Forge 1.20.1 sources and decompiles client/server.

### Stage 2: Apply Patches
Patches are applied for:
- ARM64 JNI compatibility
- CEF library linking (arm64 variant)
- Memory management optimizations

### Stage 3: Compile Java
Compiles patched source to bytecode targeting Java 17+.

### Stage 4: Compile Native Code
- Uses Android NDK ndk-build or CMake
- Produces `libjcef.so` (ARM64)
- Produces `libcef_jni.so` (ARM64 JNI wrapper)

### Stage 5: Package JAR
- Bundles Java classes
- Includes ARM64 `.so` files in `natives/arm64-v8a/`
- Generates mod metadata

## Building ARM64 Binaries

### Option A: Using Android NDK (Recommended)

```bash
export ANDROID_NDK_HOME=/path/to/android-ndk-r25c
./gradlew buildNdk
```

### Option B: Cross-compile from Linux

```bash
./gradlew buildArm64Linux
```

### Option C: Build on ARM64 Device

```bash
./gradlew buildLocal
```

## Gradle Tasks

```bash
# Full build
./gradlew build

# ARM64 specific
./gradlew buildArm64

# Download sources
./gradlew downloadForgeSource

# Apply patches
./gradlew applyPatches

# Compile native
./gradlew buildNative

# Package mod
./gradlew jar

# Clean build
./gradlew clean
```

## Testing

### 1. Unit Tests
```bash
./gradlew test
```

### 2. On ZalithLauncher2
1. Place JAR in mods folder
2. Launch Forge 1.20.1 instance
3. Check logs for native library loading
4. Verify CEF initialization

## Environment Variables

Set when launching Minecraft with MCEF:

```bash
# Enable CEF debugging
export MCEF_DEBUG=1

# Set CEF cache directory
export MCEF_CACHE_DIR=/path/to/cache

# Force software rendering (if GPU issues)
export MCEF_DISABLE_GPU=1

# ARM64 specific
export MCEF_ARCH=arm64
```

## Troubleshooting

### Native Library Not Loading
```
Error: libjcef.so not found
```
- Check `build/libs/mcef-arm64-1.20.1.jar` contains `natives/arm64-v8a/libjcef.so`
- Verify Android NDK path is correct
- Run `./gradlew buildNative --stacktrace`

### CEF Initialization Fails
```
Exception: CEF initialization failed on ARM64
```
- Ensure Forge 1.20.1 is installed
- Check mod load order (MCEF should load early)
- Verify JVM architecture matches device (aarch64)

### Performance Issues on Mobile
- Reduce CEF window size
- Enable `MCEF_DISABLE_GPU` for stability over performance
- Use software rendering if hardware rendering fails

## Source References

- **Base MCEF**: [FarestR06/forked-mcef](https://github.com/FarestR06/forked-mcef) (1.20.1 branch)
- **Forge Setup**: [MinecraftForge/ForgeGradle](https://github.com/MinecraftForge/ForgeGradle)
- **Android NDK**: [Google NDK Documentation](https://developer.android.com/ndk/downloads)

## Contributing

Contributions welcome! Please:
1. Fork the repository
2. Create a feature branch
3. Submit a pull request

## License

GNU General Public License v3.0 - See [LICENSE](LICENSE) file

Based on MCEF by [montoyo](https://github.com/montoyo/mcef)

## Support

For issues and questions:
- GitHub Issues: [mcef-arm64-builder/issues](https://github.com/fe44ek1243/mcef-arm64-builder/issues)
- ZalithLauncher Discord: [Official Server](https://discord.gg/rNrh5kW8Ty)
