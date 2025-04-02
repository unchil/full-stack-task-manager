rootProject.name = "full-stack-task-manager"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {

    repositories {
        maven{
            url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        }


        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()



    }
}
dependencyResolutionManagement {
    repositories {
        maven{
            url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        }


        google()
        mavenCentral()
    }
}

include(":shared")
include(":composeApp")
include(":server")
include(":client")
