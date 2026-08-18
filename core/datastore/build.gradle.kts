plugins {
    id("sbkm.android.library")
}

android {
    namespace = "dev.danascape.kernelmanager.core.datastore"
}

dependencies {
    implementation(project(":core:model"))
    implementation(libs.androidx.datastore.preferences)
}
