import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("java")
    id("com.vanniktech.maven.publish") version "0.29.0"
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
    compileOnly("com.sk89q.worldedit:worldedit-core:7.3.0-SNAPSHOT")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.3.0-SNAPSHOT")
    compileOnly("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")
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

mavenPublishing {
    coordinates(project.group.toString(), "blockanimator-worldedit", project.version.toString())
    pom {
        description.set("An interface to world edit for block animator api")
        name.set("blockanimator-worldedit")
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