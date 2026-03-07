package com.jawnnypoo.openmeh.ui.util

private val imagePattern = Regex("!\\[[^\\]]*\\]\\(([^)]+)\\)")
private val linkPattern = Regex("\\[([^\\]]+)]\\(([^)]+)\\)")
private val emphasisPattern = Regex("[*_~`]")
private val headingQuotePattern = Regex("(?m)^\\s*[>#]+\\s*")
private val extraSpacePattern = Regex("\\n{3,}")

fun markdownToPlainText(markdown: String): String {
    if (markdown.isBlank()) {
        return markdown
    }
    return markdown
        .replace(imagePattern, "$1")
        .replace(linkPattern, "$1 ($2)")
        .replace(headingQuotePattern, "")
        .replace(emphasisPattern, "")
        .replace(extraSpacePattern, "\n\n")
        .trim()
        .ifEmpty { markdown }
}
