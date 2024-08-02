plugins {
    id("java")
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
    implementation("io.papermc.paper:paper-api:1.19.4-R0.1-SNAPSHOT")
    implementation("com.sk89q.worldedit:worldedit-core:7.3.0-SNAPSHOT")
    implementation("com.sk89q.worldedit:worldedit-bukkit:7.3.0-SNAPSHOT")
    implementation("org.apache.commons:commons-math3:3.6.1")
    implementation(project(":api"))
    implementation(project(":paper"))
    implementation(project(":worldedit"))
}

tasks {
    test {
        useJUnitPlatform()
    }

    processResources {
        filesMatching("**/plugin.yml") { expand(project.properties) }
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
}
