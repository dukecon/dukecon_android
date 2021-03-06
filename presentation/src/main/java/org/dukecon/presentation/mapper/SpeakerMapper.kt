package org.dukecon.presentation.mapper

import org.dukecon.domain.model.Speaker
import org.dukecon.presentation.model.EventView
import org.dukecon.presentation.model.SpeakerView
import javax.inject.Inject

/**
 * Map a [SpeakerView] to and from a [Speaker] instance when data is moving between
 * this layer and the Domain layer
 */
open class SpeakerMapper @Inject constructor() : Mapper<SpeakerView, Speaker> {

    /**
     * Map a [Speaker] instance to a [EventView] instance
     */
    override fun mapToView(type: Speaker): SpeakerView {
        return SpeakerView(type.id, type.name, type.title, type.avatar)
    }
}
