plugins {
    id("java")
}

group = "org.example"
version = System.getenv("VERSION")?: "development"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation(project(":minestom"))
    implementation(project(":api"))
    implementation("org.joml:joml:1.10.8")
    implementation("net.minestom:minestom-snapshots:2be6f9c507")
    implementation("dev.hollowcube:schem:1.2.0")
}

tasks.test {
    useJUnitPlatform()
}