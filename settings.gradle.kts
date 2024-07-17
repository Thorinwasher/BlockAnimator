pluginManagement{
    repositories{
        maven("https://repo.papermc.io/repository/maven-public/")
        gradlePluginPortal()
    }
}

rootProject.name = "blockanimator"
include("api")
include("minestom")
include("papertest")
include("paper")
include("minestomtest")
