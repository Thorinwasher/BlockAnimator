import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("java")
}

group = properties["groupId"]!!
version = System.getenv("VERSION") ?: "development"

repositories {
    mavenCentral()
    maven("https://maven.enginehub.org/repo/")
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.apache.commons:commons-math3:3.6.1")
    implementation("com.sk89q.worldedit:worldedit-core:7.2.20-SNAPSHOT")
    implementation("com.sk89q.worldedit:worldedit-bukkit:7.2.20-SNAPSHOT")
    implementation("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")
    implementation(project(":api"))
    implementation(project(":paper"))
}

tasks {
    test {
        useJUnitPlatform()
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
}