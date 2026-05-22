package com.jawnnypoo.openmeh.data

import com.jawnnypoo.openmeh.github.Contributor
import com.jawnnypoo.openmeh.github.GitHubClient
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@Inject
@SingleIn(AppScope::class)
class GitHubRepository(
    private val gitHubClient: GitHubClient,
) {
    suspend fun contributors(owner: String, repo: String): List<Contributor> {
        return gitHubClient.contributors(owner, repo)
    }
}
