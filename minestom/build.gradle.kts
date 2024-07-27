import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("java")
    id("com.vanniktech.maven.publish") version "0.29.0"
}

group = properties["groupId"]!!
version = System.getenv("VERSION") ?: "development"
repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.apache.commons:commons-math3:3.6.1")
    implementation(project(":api"))
    implementation("net.minestom:minestom-snapshots:2be6f9c507")
    implementation("dev.hollowcube:schem:1.2.0")
}

tasks {
    test {
        useJUnitPlatform()
    }

    java {
        withSourcesJar()
        withJavadocJar()
    }
}


mavenPublishing {
    publishToMavenCentral(SonatypeHost.DEFAULT)
    signAllPublications()

    pom {
        description.set("An interface between block animator api and minestom")
        name.set("blockanimator-minestom")
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