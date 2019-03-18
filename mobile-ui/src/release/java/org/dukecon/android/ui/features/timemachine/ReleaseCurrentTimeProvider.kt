package org.dukecon.android.ui.features.timemachine

import org.dukecon.domain.features.time.CurrentTimeProvider
import org.threeten.bp.OffsetDateTime

class ReleaseCurrentTimeProvider : CurrentTimeProvider {

    override fun currentTimeMillis(): Long {
        return OffsetDateTime.now().toInstant().toEpochMilli()
    }
}
