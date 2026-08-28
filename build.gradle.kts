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
    minecraft("net.minecraftforge:forge:1.20.1-47.2.0")
    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
    implementation("org.spongepowered:mixin:0.8.5")
    testImplementation("junit:junit:4.13.2")
}

minecraft {
    mappings("official", "1.20.1")
    copyIdeResources = true
    runs {
        create("client") {
            workingDirectory(project.file("run"))
            ideaModule("${project.name}.main")
            args("--username=Dev")
            property("fml.earlyprogresswindow", "false")
            property("forge.logging.console.level", "debug")
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
            property("mcef.arch", "arm64")
            mods {
                create(rootProject.name) {
                    source(sourceSets.main.get())
                }
            }
        }
    }
}

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

tasks.register("buildArm64") {
    dependsOn("classes", "jar")
    description = "Build MCEF ARM64 mod JAR"
    doLast {
        println("✓ ARM64 MCEF JAR built: build/libs/mcef-arm64-${version}.jar")
    }
}

tasks.named<Delete>("clean") {
    delete("build")
    delete("run")
}
