plugins {
    id("java")
}

group = "org.example"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation(project(":minestom"))
    implementation(project(":api"))
    implementation("org.apache.commons:commons-math3:3.6.1")
    implementation("net.minestom:minestom-snapshots:2be6f9c507")
}

tasks.test {
    useJUnitPlatform()
}