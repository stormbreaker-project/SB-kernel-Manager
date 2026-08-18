plugins {
    id("sbkm.android.feature")
}

android {
    namespace = "dev.danascape.kernelmanager.feature.more"
}

dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
}
