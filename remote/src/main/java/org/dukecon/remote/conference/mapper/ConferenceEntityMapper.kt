package org.dukecon.remote.conference.mapper

import org.dukecon.android.api.model.Conference
import org.dukecon.data.model.ConferenceEntity
import javax.inject.Inject

class ConferenceEntityMapper @Inject constructor(
        private val speakerEntityMapper: SpeakerEntityMapper,
        private val eventEntityMapper: EventEntityMapper
) : EntityMapper<Conference, ConferenceEntity> {
    override fun mapFromRemote(type: Conference): ConferenceEntity {
        return ConferenceEntity(
                type.speakers.map { speaker ->
                    speakerEntityMapper.mapFromRemote(speaker)
                },
                type.events.map { event ->
                    eventEntityMapper.mapFromRemote(event)
                },
                listOf()
        )
    }
}
