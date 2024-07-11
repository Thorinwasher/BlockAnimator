plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "1.6.3"
}

group = "dev.thorinwasher"
version = properties["projectVersion"]!!

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation(project(":paper:api"))
    implementation(project(":api"))
    paperweight.paperDevBundle("1.18.2-R0.1-SNAPSHOT")
    implementation("org.apache.commons:commons-math3:3.6.1")
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