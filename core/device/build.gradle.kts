plugins {
    id("sbkm.android.library")
}

android {
    namespace = "dev.danascape.kernelmanager.core.device"
}

dependencies {
    api(project(":core:model"))
    implementation(libs.androidx.core.ktx)
    testImplementation(libs.junit)
}

dependencies {
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
