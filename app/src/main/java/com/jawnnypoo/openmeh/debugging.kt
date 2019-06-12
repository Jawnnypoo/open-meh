package com.jawnnypoo.openmeh

import java.io.PrintStream

fun <T> T.printed(format: String = "%s\n", stream: PrintStream = System.out): T =
        this.also { stream.format(format, this) }