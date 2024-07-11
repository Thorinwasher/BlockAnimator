plugins {
    id("java")
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
    implementation(project(":paper:api"))
    implementation(project(":api"))
    implementation("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
    implementation("org.apache.commons:commons-math3:3.6.1")
}

tasks.test {
    useJUnitPlatform()
}