package com.jawnnypoo.openmeh.extension

import android.widget.TextView
import androidx.core.text.parseAsHtml
import com.commit451.coilimagegetter.CoilImageGetter
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

private val renderer: HtmlRenderer = HtmlRenderer.builder().build()

private val parser: Parser = Parser.builder().build()

/**
 * Sets markdown text on a TextView, rendering the markdown using CommonMark
 */
fun TextView.setMarkdownText(text: String) {
    val document = parser.parse(text)
    val html = renderer.render(document)
    val getter = CoilImageGetter(this)
    this.text = html.parseAsHtml(imageGetter = getter)
}