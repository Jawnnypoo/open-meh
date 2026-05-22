package com.jawnnypoo.openmeh.ui

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Compose-side surface for the build's debug flag. Provided by the Android entry point from the
 * DI graph and read where UI needs to gate debug-only affordances (e.g. the "post notification"
 * menu item in [com.jawnnypoo.openmeh.ui.screen.DealScreen]).
 */
val LocalIsDebug: ProvidableCompositionLocal<Boolean> = staticCompositionLocalOf { false }
