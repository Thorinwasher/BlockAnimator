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
include("paper:v_1_21")
include("paper:v_1_18_2")
include("paper:api")
include("minestomtest")
