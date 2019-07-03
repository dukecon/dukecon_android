package org.dukecon.data.repository

import org.dukecon.data.model.*

interface EventRemote {
    fun getConference():ConferenceEntity
    fun getEvents(): List<EventEntity>
    fun getSpeakers(): List<SpeakerEntity>
    fun getRooms(): List<RoomEntity>
    fun getEvent(id: String): EventEntity
    fun getSpeaker(id: String): SpeakerEntity
    fun submitFeedback(feedback: FeedbackEntity): Any
    fun getKeycloak(): KeycloakEntity
    fun getMetaData(): MetaDataEntity
    fun getFavorites(): List<FavoriteEntity>
    fun saveFavorites(favorite: List<FavoriteEntity>): List<FavoriteEntity>
}