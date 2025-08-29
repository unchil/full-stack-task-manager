import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.serialization)

}

group = "org.example.ktor"
version = "1.0.0"

application {
    mainClass.set("org.example.ktor.MainKt")
}

kotlin {

    dependencies {
        implementation(libs.ktor.client.core)
        implementation(libs.ktor.client.cio)
        implementation(libs.logback)
        implementation(libs.ktor.client.logging)
        implementation(libs.ktor.client.negotiation)
        implementation(libs.kotlinx.serialization)
        implementation(libs.ktor.serialization.json)
        implementation(libs.ktor.serialization.xml)
        implementation(libs.kotlinx.dataframe)
        implementation(libs.sqlite)
        implementation(libs.exposed.core)
        implementation(libs.exposed.jdbc)

        implementation("org.json:json:20250517")
    }
}
