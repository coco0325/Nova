plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.5.4"
    `kotlin-dsl`
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.xenondevs.xyz/releases/")
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
        metadataSources {
            mavenPom()
        }
    }
}

dependencies {
    //implementation("org.spigotmc:spigot-api:1.19.4-R0.1-SNAPSHOT")
    implementation("net.lingala.zip4j:zip4j:2.11.1")
    implementation("net.md-5:SpecialSource:1.11.0")
    implementation("xyz.xenondevs.string-remapper:string-remapper-core:1.1")
    //implementation("dev.folia:folia-api:1.19.4-R0.1-SNAPSHOT")
    paperweight.foliaDevBundle("1.19.4-R0.1-SNAPSHOT")
}

gradlePlugin {
    plugins {
        create("loader-jar-plugin") {
            id = "xyz.xenondevs.loader-jar-plugin"
            implementationClass = "LoaderJarPlugin"
        }
        create("library-loader-plugin") {
            id = "xyz.xenondevs.library-loader-plugin"
            implementationClass = "LibraryLoaderPlugin"
        }
    }
}