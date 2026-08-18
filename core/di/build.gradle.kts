plugins {
    id("sbkm.android.library")
    id("sbkm.android.compose")
}

android {
    namespace = "dev.danascape.kernelmanager.core.di"
}

dependencies {
    api(project(":core:data"))
    implementation(project(":core:datastore"))
    implementation(project(":core:network"))
    implementation(libs.ktor.client.okhttp)
}
