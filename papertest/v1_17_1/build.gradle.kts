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
    implementation("io.papermc.paper:paper-api:1.19.4-R0.1-SNAPSHOT")
    implementation("com.sk89q.worldedit:worldedit-core:7.3.0-SNAPSHOT")
    implementation("com.sk89q.worldedit:worldedit-bukkit:7.3.0-SNAPSHOT")
    implementation("org.apache.commons:commons-math3:3.6.1")
}

tasks {

    test {
        useJUnitPlatform()
    }


    runServer {
        minecraftVersion("1.17.1")
        downloadPlugins {
            url("https://cdn.modrinth.com/data/1u6JkXh5/versions/DfjpjdP5/worldedit-bukkit-7.2.20.jar")
        }
    }

    shadowJar {
        dependencies {
            include(project(":api:"))
            include(project(":paper"))
            include(project(":worldedit"))
            include(project(":papertest:shared"))
            include(dependency("org.apache.commons:commons-math3:.*"))
        }
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
}