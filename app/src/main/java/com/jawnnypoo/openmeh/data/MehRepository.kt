package com.jawnnypoo.openmeh.data

import com.jawnnypoo.openmeh.shared.api.MehClient
import com.jawnnypoo.openmeh.shared.response.MehResponse

class MehRepository(
    private val mehClient: MehClient,
) {
    suspend fun currentDeal(): MehResponse {
        return mehClient.meh()
    }
}
