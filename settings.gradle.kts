pluginManagement {
    repositories {
        // These repositories are only for Gradle plugins, put any other repositories in the repository block further below
        maven(url = "https://maven.minecraftforge.net")
        maven(url = "https://maven.parchmentmc.org")
        gradlePluginPortal()
        mavenCentral()
    }
}
rootProject.name = "mcef-arm64-builder"
