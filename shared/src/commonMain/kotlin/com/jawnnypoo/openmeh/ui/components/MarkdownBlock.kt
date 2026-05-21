package com.jawnnypoo.openmeh.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * Renders a markdown string. Implemented per platform because the underlying renderer
 * (compose-richtext on Android) is not yet multiplatform on the version we use.
 */
@Composable
expect fun MarkdownBlock(
    markdown: String,
    foregroundColor: Color,
    modifier: Modifier = Modifier,
)
