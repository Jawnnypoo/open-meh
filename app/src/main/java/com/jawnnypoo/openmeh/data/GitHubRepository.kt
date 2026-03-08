package com.jawnnypoo.openmeh.data

import com.jawnnypoo.openmeh.github.Contributor
import com.jawnnypoo.openmeh.github.GitHubClient

class GitHubRepository(
    private val gitHubClient: GitHubClient,
) {
    suspend fun contributors(owner: String, repo: String): List<Contributor> {
        return gitHubClient.contributors(owner, repo)
    }
}
