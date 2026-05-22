package com.jawnnypoo.openmeh.di

import com.jawnnypoo.openmeh.data.GitHubRepository
import com.jawnnypoo.openmeh.data.MehRepository
import com.jawnnypoo.openmeh.util.Prefs
import com.russhwolf.settings.Settings
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metrox.viewmodel.ViewModelGraph

/**
 * Root dependency graph for the app. Repositories, ViewModels, and platform inputs
 * (api key, debug flag, [Settings]) are wired here. Built once in the Android Application
 * via [Factory.create] and held for the lifetime of the process.
 */
@SingleIn(AppScope::class)
@DependencyGraph(scope = AppScope::class)
interface AppGraph : ViewModelGraph {

    val mehRepository: MehRepository
    val gitHubRepository: GitHubRepository
    val prefs: Prefs

    /** Build-time debug flag, also exposed to the UI via `LocalIsDebug`. */
    val isDebug: Boolean

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(
            @Provides apiKey: ApiKey,
            @Provides isDebug: Boolean,
            @Provides settings: Settings,
        ): AppGraph
    }
}
