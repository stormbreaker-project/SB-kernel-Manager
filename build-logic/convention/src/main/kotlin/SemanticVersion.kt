/**
 * The app's version, as MAJOR.MINOR.PATCH.
 */
data class SemanticVersion(
    val major: Int,
    val minor: Int,
    val patch: Int,
) {
    val name: String get() = "$major.$minor.$patch"

    val code: Int get() = major * 10_000 + minor * 100 + patch

    companion object {
        private val FORMAT = Regex("""^(\d+)\.(\d+)\.(\d+)$""")

        fun parse(raw: String): SemanticVersion {
            val match = FORMAT.matchEntire(raw.trim())
                ?: error("app version must be MAJOR.MINOR.PATCH, got \"$raw\"")
            val (major, minor, patch) = match.destructured
            val version = SemanticVersion(major.toInt(), minor.toInt(), patch.toInt())
            require(version.minor <= 99 && version.patch <= 99) {
                "minor and patch must each be 99 or less, got \"$raw\""
            }
            return version
        }
    }
}
