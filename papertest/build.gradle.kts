plugins {
    id("java")
    id("xyz.jpenilla.run-paper") version "2.3.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "dev.thorinwasher"
version = properties["projectVersion"]!!

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")
    implementation(project(":api"))
    implementation(project(":paper:api"))
    implementation(project(":paper:v_1_18_2"))
    implementation("org.apache.commons:commons-math3:3.6.1")
}

tasks {
    test {
        useJUnitPlatform()
    }

    runServer {
        minecraftVersion("1.18.2")
    }

    processResources {
        filesMatching("**/plugin.yml") { expand(project.properties) }
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    shadowJar {
        dependencies {
            include(project(":api:"))
            include(project(":paper:api"))
            include(project(":paper:v_1_18_2"))
            include(dependency("org.apache.commons:commons-math3:.*"))
        }
    }
}
