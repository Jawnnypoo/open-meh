package com.jawnnypoo.openmeh.data

import com.jawnnypoo.openmeh.shared.api.MehClient
import com.jawnnypoo.openmeh.shared.response.MehResponse
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@Inject
@SingleIn(AppScope::class)
class MehRepository(
    private val mehClient: MehClient,
) {
    suspend fun currentDeal(): MehResponse {
        return mehClient.meh()
    }
}
