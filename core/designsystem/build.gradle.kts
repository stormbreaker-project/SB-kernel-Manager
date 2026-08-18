plugins {
    id("sbkm.android.library")
    id("sbkm.android.compose")
}

android {
    namespace = "dev.danascape.kernelmanager.core.designsystem"
}

dependencies {
    api(project(":design-system"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.browser)
    implementation(libs.androidx.compose.ui.text.google.fonts)
}
