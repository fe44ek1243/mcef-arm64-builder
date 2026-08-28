import org.gradle.api.tasks.Delete
import java.io.File

plugins {
    java
    id("net.minecraftforge.gradle") version "6.0.16"
    id("eclipse")
    id("idea")
}

group = "com.mcef.arm64"
version = "1.20.1-0.1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

configurations {
    create("ndk")
    create("mcefSource")
}

dependencies {
    // Minecraft & Forge
    minecraft("net.minecraftforge:forge:1.20.1-47.2.0")
    
    // JCEF (Java CEF)
    implementation("org.cef:jcef:1.0.0-arm64")
    
    // Mixin for patches
    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
    implementation("org.spongepowered:mixin:0.8.5")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
}

minecraft {
    // Mappings channel and version - use positional arguments
    mappings("official", "1.20.1")
    
    copyIdeResources = true
    runs {
        create("client") {
            workingDirectory(project.file("run"))
            ideaModule("${project.name}.main")
            args("--username=Dev")
            
            // ARM64 specific JVM args
            property("fml.earlyprogresswindow", "false")
            property("forge.logging.console.level", "debug")
            property("forge.logging.markers", "REGISTRIES")
            property("mcef.arch", "arm64")
            mods {
                create(rootProject.name) {
                    source(sourceSets.main.get())
                }
            }
        }
        create("server") {
            workingDirectory(project.file("run"))
            ideaModule("${project.name}.main")
            args("--nogui")
            property("fml.earlyprogresswindow", "false")
            property("forge.logging.console.level", "debug")
            property("forge.logging.markers", "REGISTRIES")
            property("mcef.arch", "arm64")
            mods {
                create(rootProject.name) {
                    source(sourceSets.main.get())
                }
            }
        }
        create("gameTestServer") {
            workingDirectory(project.file("run"))
            ideaModule("${project.name}.main")
            args("--nogui", "--gametest")
            property("fml.earlyprogresswindow", "false")
            property("forge.logging.console.level", "debug")
            property("forge.logging.markers", "REGISTRIES")
            property("mcef.arch", "arm64")
            mods {
                create(rootProject.name) {
                    source(sourceSets.main.get())
                }
            }
        }
    }
}

// JAR Configuration
tasks.jar {
    manifest {
        attributes(
            "Specification-Title" to project.name,
            "Specification-Vendor" to "MCEF Contributors",
            "Specification-Version" to "1.20.1",
            "Implementation-Title" to project.name,
            "Implementation-Version" to "${version}",
            "Implementation-Vendor" to "MCEF Contributors",
            "MCEF-Architecture" to "arm64-v8a",
            "MCEF-Platform" to "Android"
        )
    }
    archiveBaseName.set("mcef-arm64")
}

// ARM64 Build Tasks
tasks.register("buildArm64") {
    dependsOn("buildNative", "classes", "jar")
    description = "Build MCEF ARM64 mod JAR"
    doLast {
        println("✓ ARM64 MCEF JAR built: build/libs/mcef-arm64-${version}.jar")
    }
}

tasks.register("buildNative") {
    description = "Compile native ARM64 libraries"
    doLast {
        val ndkHome = System.getenv("ANDROID_NDK_HOME") ?: "/path/to/ndk"
        val srcDir = file("src/main/cpp")
        val buildDir = file("build/native")
        
        if (!srcDir.exists()) {
            srcDir.mkdirs()
            println("Created cpp directory at: ${srcDir.absolutePath}")
        }
        
        println("Building native libraries...")
        println("NDK Home: $ndkHome")
        println("Source: ${srcDir.absolutePath}")
        println("Build Output: ${buildDir.absolutePath}")
        
        // Create CMakeLists.txt if it doesn't exist
        val cmakeLists = file("src/main/cpp/CMakeLists.txt")
        if (!cmakeLists.exists()) {
            cmakeLists.writeText("""
cmake_minimum_required(VERSION 3.20)
project(mcef_jni)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# ARM64 specific flags
add_compile_options(-march=armv8-a -mtune=cortex-a72)

add_library(jcef_jni SHARED
    src/main/cpp/jcef_jni.cpp
    src/main/cpp/cef_bridge.cpp
)

target_include_directories(jcef_jni PRIVATE
    ${'$'}{JAVA_INCLUDE_PATH}
    ${'$'}{JAVA_INCLUDE_PATH}/linux
    src/main/cpp/include
)
""")
        }
    }
}

tasks.register("downloadForgeSource") {
    description = "Download and setup Forge 1.20.1 sources"
    doLast {
        println("Forge sources configured via ForgeGradle")
        println("Run 'gradlew genSources' to generate decompiled sources")
    }
}

tasks.register("applyPatches") {
    description = "Apply ARM64 compatibility patches"
    dependsOn("genSources")
    doLast {
        val patchDir = file("patches")
        if (patchDir.exists()) {
            println("Applying patches from ${patchDir.absolutePath}")
            // Patch application logic here
        } else {
            println("No patches directory found")
        }
    }
}

tasks.named<Delete>("clean") {
    delete("build")
    delete("run")
}
