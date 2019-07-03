package org.dukecon.data.repository

import org.dukecon.data.model.*

interface EventDataStore {

    fun clearEvents()

    fun getEvents(): List<EventEntity>
    fun getEvent(id: String): EventEntity
    fun saveEvents(events: List<EventEntity>)

    fun getSpeakers(): List<SpeakerEntity>
    fun getSpeaker(id: String): SpeakerEntity
    fun saveSpeakers(speakers: List<SpeakerEntity>)

    fun getRooms(): List<RoomEntity>
    fun saveRooms(rooms: List<RoomEntity>)

    fun getFavorites(): List<FavoriteEntity>
    fun saveFavorites(favorite: List<FavoriteEntity>): List<FavoriteEntity>
    fun submitFeedback(feedback: FeedbackEntity): Any

    fun getKeycloak(): KeycloakEntity

    fun getMetaData(): MetaDataEntity

    fun getConference():ConferenceEntity
}