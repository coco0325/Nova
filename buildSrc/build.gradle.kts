plugins {
    `kotlin-dsl`
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation("org.spigotmc:spigot-api:1.19.4-R0.1-SNAPSHOT")
    implementation("net.lingala.zip4j:zip4j:2.11.1")
}

gradlePlugin {
    plugins {
        create("jar-loader-gradle-plugin") {
            id = "xyz.xenondevs.jar-loader-gradle-plugin"
            implementationClass = "JarLoaderPlugin"
        }
    }
}