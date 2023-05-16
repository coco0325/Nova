import org.jetbrains.dokka.gradle.AbstractDokkaLeafTask

plugins {
    java
    `maven-publish`
    alias(libs.plugins.dokka)
}

dependencies {
    implementation("org.jetbrains:annotations:24.0.1")
    compileOnly(project(":nova-loader"))
    compileOnly(libs.spigot.api)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<AbstractDokkaLeafTask> {
    dokkaSourceSets {
        register("main") {
            sourceRoots.from("src/main/java")
        }
    }
}

publishing {
    repositories {
        maven {
            credentials {
                name = "xenondevs"
                url = uri { "https://repo.xenondevs.xyz/releases/" }
                credentials(PasswordCredentials::class)
            }
        }
    }
    
    publications {
        create<MavenPublication>("novaAPI") {
            from(components.getByName("java"))
            artifact(tasks.getByName("sources"))
        }
    }
}