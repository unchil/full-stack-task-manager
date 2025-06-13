import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import com.android.build.gradle.tasks.MergeSourceSetFolders
import org.gradle.kotlin.dsl.implementation

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.google.devtools.ksp)
    alias(libs.plugins.secretsGradle)
}


kotlin {

    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    jvm("desktop")

    js(IR) {
        browser{
            commonWebpackConfig {
                outputFileName = "compose-app.js"
            }
        }
        binaries.executable()
        useEsModules()
    }


    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName = "composeApp"
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "composeApp.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(rootDirPath)
                        add(projectDirPath)
                    }
                }
            }
        }
        binaries.executable()
    }



    sourceSets {
        val desktopMain by getting
        
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            implementation(libs.ktor.client.cio)

            implementation(libs.google.maps.compose)
            implementation(libs.google.maps.compose.widgets)
            implementation(libs.google.maps.compose.utils)

            implementation(libs.let.plot.kernel)
            implementation(libs.let.plot.common)

            implementation(libs.skiko.android)
            implementation(libs.let.plot.compose.android)


        }
        
        commonMain.dependencies {
            implementation(projects.shared)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.kotlinx.datetime.old)
        }

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.logback)
            implementation(libs.ktor.client.cio)

            implementation(libs.let.plot.kernel)
            implementation(libs.let.plot.common)

            implementation(libs.let.plot.platf.awt)
            implementation(libs.let.plot.compose)
        }


        iosMain.dependencies {
            implementation(libs.ktor.client.cio)
        }

        wasmJsMain.dependencies {

        }

        jsMain.dependencies {
            implementation(libs.kotlinx.serialization)
            implementation(libs.ktor.serialization.json)
            implementation(libs.lets.plot.kotlin.js)
            implementation(libs.kotlinx.html.js)
        }


    }
}





android {
    namespace = "org.example.ktor"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "org.example.ktor"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        buildFeatures {
            buildConfig = true
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

}



compose.desktop {
    application {
        mainClass = "org.example.ktor.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.example.ktor"
            packageVersion = "1.0.0"
        }
    }
}


////////////////////////////////////////////////////////
// Include the following code in your Gradle build script
// to ensure that compatible Skiko binaries are
// downloaded and included in your project.
//
// Without this, you won't be able to run your app
// in the IDE on a device emulator.
// //////////////////////////////////////////////////////

val skikoJniLibsReleaseAssetName = "skiko-jni-libs.zip"
val skikoJniLibsDestDir = file("${project.projectDir}/src/androidMain/jniLibs/")

tasks.register("downloadSkikoJniLibsReleaseAsset") {
    val repoUrl = "https://github.com/JetBrains/lets-plot-skia"
    val releaseTag = "v2.0.0"

    doLast {
        val downloadUrl = "$repoUrl/releases/download/$releaseTag/$skikoJniLibsReleaseAssetName"
        val outputFile = layout.buildDirectory.file("downloads/$skikoJniLibsReleaseAssetName").get().asFile

        if (outputFile.exists()) {
            println("File already exists: ${outputFile.absolutePath}")
            println("Skipping download.")
        } else {
            outputFile.parentFile?.mkdirs()

            println("Downloading $skikoJniLibsReleaseAssetName from $downloadUrl")
            uri(downloadUrl).toURL().openStream().use { input ->
                outputFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            println("Download completed: ${outputFile.absolutePath}")
        }
    }
}

tasks.register<Copy>("unzipSkikoJniLibsReleaseAsset") {
    dependsOn("downloadSkikoJniLibsReleaseAsset")
    from(zipTree(layout.buildDirectory.file("downloads/$skikoJniLibsReleaseAssetName")))
    into(skikoJniLibsDestDir)
    doFirst {
        delete(skikoJniLibsDestDir)
    }
}

tasks.register("cleanSkikoJniLibs") {
    doLast {
        delete(skikoJniLibsDestDir)
    }
}

tasks.named("clean") {
    dependsOn("cleanSkikoJniLibs")
}
dependencies {
    implementation(libs.androidx.ui.android)
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.ui.text.android)
    implementation(libs.androidx.runtime.android)
    implementation(libs.androidx.foundation.android)
}

tasks.withType<MergeSourceSetFolders>().configureEach {
    dependsOn("unzipSkikoJniLibsReleaseAsset")
}

////////////////////////////////////////////////////////



secrets {
    // To add your Maps API key to this project:
    // 1. If the secrets.properties file does not exist, create it in the same folder as the local.properties file.
    // 2. Add this line, where YOUR_API_KEY is your API key:
    //        MAPS_API_KEY=YOUR_API_KEY
    propertiesFileName = "secrets.properties"

    // A properties file containing default secret values. This file can be
    // checked in version control.
    defaultPropertiesFileName = "local.defaults.properties"

    // Configure which keys should be ignored by the plugin by providing regular expressions.
    // "sdk.dir" is ignored by default.
    ignoreList.add("keyToIgnore") // Ignore the key "keyToIgnore"
    ignoreList.add("sdk.*")       // Ignore all keys matching the regexp "sdk.*"
}