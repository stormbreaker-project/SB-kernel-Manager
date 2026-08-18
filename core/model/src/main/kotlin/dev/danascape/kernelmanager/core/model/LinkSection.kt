package dev.danascape.kernelmanager.core.model

/** A group of outbound links on the More screen. */
data class LinkSection(
    val id: String,
    val title: String,
    val items: List<LinkItem>,
)

data class LinkItem(
    val id: String,
    val label: String,
    val description: String?,
    /** Null when there is nothing to open yet — see [soon]. */
    val url: String?,
    val external: Boolean,
    /** Announced but not published: shown, not tappable. */
    val soon: Boolean,
) {
    val openable: Boolean get() = url != null && !soon
}
