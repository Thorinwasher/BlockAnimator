plugins {
    id("java")
    id("xyz.jpenilla.run-paper") version "2.3.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "dev.thorinwasher.blockanimator"
version = System.getenv("VERSION") ?: "development"

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation(project(":papertest:shared"))
    compileOnly("io.papermc.paper:paper-api:1.19.4-R0.1-SNAPSHOT")
    compileOnly("com.sk89q.worldedit:worldedit-core:7.3.0-SNAPSHOT")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.3.0-SNAPSHOT")
    implementation("org.joml:joml:1.10.8")
}

tasks {

    test {
        useJUnitPlatform()
    }


    runServer {
        minecraftVersion("1.18.2")
        downloadPlugins {
            url("https://cdn.modrinth.com/data/z4HZZnLr/versions/YSQCH9EW/FastAsyncWorldEdit-Bukkit-2.9.2.jar")
        }
    }

    shadowJar {
        dependencies {
            include(project(":api:"))
            include(project(":paper"))
            include(project(":worldedit"))
            include(project(":papertest:shared"))
            include(dependency("org.joml:joml:.*"))
        }
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
}