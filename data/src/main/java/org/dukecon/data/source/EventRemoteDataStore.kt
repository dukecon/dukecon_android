package org.dukecon.data.source

import org.dukecon.data.model.*
import org.dukecon.data.repository.EventDataStore
import org.dukecon.data.repository.EventRemote
import javax.inject.Inject


/**
 * Implementation of the [EventDataStore] interface to provide a means of communicating
 * with the remote data source
 */
open class EventRemoteDataStore @Inject constructor(private val eventRemote: EventRemote) :
        EventDataStore {
    override fun getConference(): ConferenceEntity {
        return eventRemote.getConference()
    }

    override fun getMetaData(): MetaDataEntity {
        return eventRemote.getMetaData()
    }

    override fun getKeycloak(): KeycloakEntity {
        return eventRemote.getKeycloak()
    }

    override fun submitFeedback(feedback: FeedbackEntity): Any {
        return eventRemote.submitFeedback(feedback)
    }

    override fun saveFavorites(favorites: List<FavoriteEntity>): List<FavoriteEntity> {
        return eventRemote.saveFavorites(favorites)
    }

    // no call to API yet
    override fun getFavorites(): List<FavoriteEntity> {
        return eventRemote.getFavorites()
    }

    override fun getSpeaker(id: String): SpeakerEntity {
        return eventRemote.getSpeaker(id)
    }

    override fun getEvent(id: String): EventEntity {
        return eventRemote.getEvent(id)
    }

    override fun getRooms(): List<RoomEntity> {
        return eventRemote.getRooms()
    }

    override fun saveRooms(rooms: List<RoomEntity>) {
        throw UnsupportedOperationException()
    }

    override fun getSpeakers(): List<SpeakerEntity> {
        return eventRemote.getSpeakers()
    }

    override fun saveSpeakers(speakers: List<SpeakerEntity>) {
        throw UnsupportedOperationException()
    }

    override fun saveEvents(events: List<EventEntity>) {
        throw UnsupportedOperationException()
    }

    override fun getEvents(): List<EventEntity> {
        return eventRemote.getEvents()
    }

    override fun clearEvents() {
        throw UnsupportedOperationException()
    }
}