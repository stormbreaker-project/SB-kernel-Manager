plugins {
    id("sbkm.android.library")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "dev.danascape.kernelmanager.core.batterymonitor"
}

dependencies {
    api(project(":core:battery"))
    implementation(project(":core:model"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.serialization.json)
}
