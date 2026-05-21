package com.jawnnypoo.openmeh.ui.components

import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.material3.RichText

@Composable
actual fun MarkdownBlock(
    markdown: String,
    foregroundColor: Color,
    modifier: Modifier,
) {
    CompositionLocalProvider(LocalContentColor provides foregroundColor) {
        RichText(modifier = modifier) {
            Markdown(content = markdown)
        }
    }
}
