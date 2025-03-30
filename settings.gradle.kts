rootProject.name = "full-stack-task-manager"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {

        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }


        mavenCentral()

        gradlePluginPortal()
//  mavenLocal()


    }
}
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        //  mavenLocal()
    }
}

include(":shared")
include(":composeApp")
include(":server")
include(":client")
