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

        maven{
            url = uri("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven/")
        }

        google()
        mavenCentral()

        // --- 아래 maven 블록을 추가하세요 ---
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/unchil/ComposeDataGrid")
            credentials {
                username = System.getenv("GPR_USER")
                password = System.getenv("GPR_KEY")

            }
        }
        // ------------------------------------

    }
}

include(":shared")
include(":composeApp")
include(":server")
include(":client")
