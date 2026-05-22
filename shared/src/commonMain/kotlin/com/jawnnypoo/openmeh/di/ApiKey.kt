package com.jawnnypoo.openmeh.di

import kotlin.jvm.JvmInline

/**
 * Inline wrapper around the meh.com API key. Wrapping the [String] gives it a distinct type so
 * the DI graph can resolve it unambiguously — and so we never accidentally pass an unrelated
 * `String` where the api key is expected.
 */
@JvmInline
value class ApiKey(val value: String)
