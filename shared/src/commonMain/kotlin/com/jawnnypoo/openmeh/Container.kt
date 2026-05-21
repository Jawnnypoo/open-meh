package com.jawnnypoo.openmeh

import com.jawnnypoo.openmeh.data.GitHubRepository
import com.jawnnypoo.openmeh.data.MehRepository
import com.jawnnypoo.openmeh.github.GitHubClient
import com.jawnnypoo.openmeh.shared.api.MehClient

/**
 * Lightweight service locator. Initialized once by the platform entry point (e.g.
 * `OpenMehApplication` on Android) so commonMain code can grab repositories without an explicit DI
 * framework.
 */
object Container {

    private var initialized = false

    lateinit var mehRepository: MehRepository
        private set
    lateinit var gitHubRepository: GitHubRepository
        private set

    var debug: Boolean = false
        private set

    fun initialize(apiKey: String, debug: Boolean) {
        if (initialized) return
        initialized = true
        this.debug = debug
        val mehClient = MehClient(apiKey, debug)
        val gitHubClient = GitHubClient(debug)
        mehRepository = MehRepository(mehClient)
        gitHubRepository = GitHubRepository(gitHubClient)
    }
}
