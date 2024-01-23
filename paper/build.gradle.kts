plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "1.3.6"
}

group = "dev.thorinwasher"
version = properties["projectVersion"]!!

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation(project(":api"))
    implementation("org.apache.commons:commons-math3:3.6.1")
    paperDevBundle("1.18.2-R0.1-SNAPSHOT")
}

tasks {
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    test {
        useJUnitPlatform()
    }

    assemble {
        dependsOn(reobfJar)
    }
}