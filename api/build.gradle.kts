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
                pom {
                    description.set("A library for animating the generation or destruction of structures")
                    name.set(artifactId)
                    url.set(rootProject.properties["website"]!!.toString())
                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("${rootProject.properties["websiteRaw"]!!}/v$version/LICENSE")
                        }
                    }
                    developers {
                        developer {
                            id.set("thorinwasher")
                            name.set("Hjalmar Gunnarsson")
                            email.set("officialhjalmar.gunnarsson@outlook.com")
                        }
                    }
                    scm {
                        connection.set("scm:git:git://github.com/Thorinwasher/BlockAnimator.git")
                        developerConnection.set("scm:git:ssh://github.com:Thorinwasher/BlockAnimator.git")
                        url.set("${rootProject.properties["website"]!!}/tree/v$version")
                    }
                }
            }
        }
        mavenCentral {
            credentials {
                username = System.getenv("CENTRAL_USER_TOKEN")
                password = System.getenv("CENTRAL_KEY_TOKEN")
            }
        }
    }
}