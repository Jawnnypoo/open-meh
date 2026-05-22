package com.jawnnypoo.openmeh.di

import androidx.lifecycle.ViewModel
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.MetroViewModelFactory
import dev.zacsweers.metrox.viewmodel.ViewModelAssistedFactory
import kotlin.reflect.KClass

/**
 * Concrete factory that bridges Metro's `@ContributesIntoMap`-contributed ViewModel providers to
 * androidx.lifecycle's `ViewModelProvider.Factory`. Required because [MetroViewModelFactory] is
 * abstract — we subclass it, contribute it as a binding, and the graph injects the multibinding
 * maps.
 */
@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class OpenMehViewModelFactory(
    override val viewModelProviders: Map<KClass<out ViewModel>, () -> ViewModel>,
    override val assistedFactoryProviders: Map<KClass<out ViewModel>, () -> ViewModelAssistedFactory>,
    override val manualAssistedFactoryProviders:
        Map<KClass<out ManualViewModelAssistedFactory>, () -> ManualViewModelAssistedFactory>,
) : MetroViewModelFactory()
