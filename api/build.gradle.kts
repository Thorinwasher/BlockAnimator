import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("java")
    id("com.vanniktech.maven.publish") version "0.29.0"
}

group = properties["groupId"]!!
version = System.getenv("VERSION") ?: "development"

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.joml:joml:1.10.8")
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

mavenPublishing {
    coordinates(project.group.toString(), "blockanimator-api", project.version.toString())
    pom {
        description.set("A library for animating the generation or destruction of structures in minecraft")
        name.set("blockanimator-api")
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
            developerConnection.set("scm:git:ssh://git@github.com/:Thorinwasher/BlockAnimator.git")
            url.set("${rootProject.properties["website"]!!}/tree/v$version")
        }
    }
    signAllPublications()
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
}