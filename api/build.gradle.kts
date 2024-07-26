
plugins {
    id("java")
    id("maven-publish")
}

group = properties["groupId"]!!
version = System.getenv("VERSION")?: "development"

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.apache.commons:commons-math3:3.6.1")
    implementation("org.jetbrains:annotations:24.1.0")
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

publishing {

    repositories {
        publications {
            create<MavenPublication>("maven") {
                artifactId = "blockanimator-api"
                from(components["java"])
            }
        }
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/octocat/hello-world")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}