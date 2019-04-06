package org.dukecon.remote.conference

import com.google.gson.stream.MalformedJsonException
import org.dukecon.android.api.ConferencesApi
import org.dukecon.android.api.model.Event
import org.dukecon.android.api.model.Speaker
import org.dukecon.data.model.*
import org.dukecon.data.repository.EventRemote
import org.dukecon.remote.conference.mapper.*
import java.io.IOException
import javax.inject.Inject

/**
 * Remote implementation for retrieving Event instances. This class implements the
 * [EventRemote] from the Data layer as it is that layers responsibility for defining the
 * operations in which data store implementation layers can carry out.
 */
class EventRemoteImpl @Inject constructor(
        private val conferenceApi: ConferencesApi,
        private val conferenceId: String,
        private val entityMapper: EventEntityMapper,
        private val feedbackEntityMapper: FeedbackEntityMapper,
        private val speakersEntityMapper: SpeakerEntityMapper,
        private val roomEntityMapper: RoomEntityMapper,
        private val keycloakEntityMapper: KeycloakEntityMapper,
        private val metaDataEntityMapper: MetaDataEntityMapper,
        private val favoritesEntityMapper:FavoritesEntityMapper
) : EventRemote {

    override fun getFavorites(): List<FavoriteEntity> {
        try {
            val call = conferenceApi.getFavorites()
            val response = call.execute()
            if (response.isSuccessful) {
                if (response.body() != null) {
                    val favorites = response.body()
                    if (favorites != null) {
                        return favorites.map {
                            favoritesEntityMapper.mapFromRemote(it)
                        }
                    }
                }
            }
        } catch (ex: MalformedJsonException) {
            // TODO app proper error handling for user logged out
            return emptyList()
        }
        return emptyList()
    }

    override fun saveFavorites(favorite: List<FavoriteEntity>): List<FavoriteEntity> {
        val call = conferenceApi.sendFavorites(conferenceId, favorite.map { favoritesEntityMapper.mapToRemote(it) })
        val response = call.execute()
        if (response.isSuccessful) {
            return getFavorites()
        } else {
            throw  IOException()
        }
    }

    override fun getMetaData(): MetaDataEntity {
        val call = conferenceApi.getMeta(conferenceId)
        val response = call.execute()
        if (response.isSuccessful) {
            if (response.body() != null) {
                val metaData = response.body()
                if (metaData != null) {
                    return metaDataEntityMapper.mapFromRemote(metaData)
                }
            }
        }
        throw IOException()
    }

    override fun getKeycloak(): KeycloakEntity {
        val call = conferenceApi.getKeyCloak(conferenceId)
        val response = call.execute()
        if (response.isSuccessful) {
            val keycloak = response.body()
            if (keycloak != null) {
                return keycloakEntityMapper.mapFromRemote(keycloak)
            }
        }
        throw IOException()
    }

    override fun submitFeedback(feedback: FeedbackEntity): Any {
        val call = conferenceApi.updateFeedback(
                conferenceId,
                feedback.sessionId,
                feedbackEntityMapper.mapToRemote(feedback)
        )
        val response = call.execute()
        if (response.isSuccessful) {
            return Any()
        } else {
            throw  IOException()
        }
    }

    override fun getSpeaker(id: String): SpeakerEntity {
        val call = conferenceApi.getSpeakers(conferenceId)
        val response = call.execute()
        if (response.isSuccessful) {
            if (response.body() != null) {
                val eventsList = response.body()
                if (eventsList != null) {
                    val found = eventsList.find { event ->
                        event.id == id
                    } ?: emptySpeakerEntity()
                    return speakersEntityMapper.mapFromRemote(found)
                }
            }
        }
        throw IOException()
    }

    private fun emptySpeakerEntity(): Speaker {
        val speaker = Speaker()
        speaker.id = ""
        return speaker
    }

    override fun getEvent(id: String): EventEntity {
        val call = conferenceApi.getEvents(conferenceId)
        val response = call.execute()
        if (response.isSuccessful) {
            if (response.body() != null) {
                val eventsList = response.body()
                if (eventsList != null) {
                    val found = eventsList.find { event ->
                        event.id == id
                    } ?: emptyEntity()
                    return entityMapper.mapFromRemote(found)
                }
            }
        }
        throw IOException()
    }

    private fun emptyEntity(): Event {
        val event = Event()
        event.id = ""
        return event
    }

    override fun getRooms(): List<RoomEntity> {
        val call = conferenceApi.getMeta(conferenceId)
        val response = call.execute()
        if (response.isSuccessful) {
            if (response.body() != null) {
                val eventsList = response.body()
                if (eventsList != null) {
                    return eventsList.locations.map { roomEntityMapper.mapFromRemote(it) }

                }
            }
        }
        throw IOException()
    }

    override fun getSpeakers(): List<SpeakerEntity> {
        val call = conferenceApi.getSpeakers(conferenceId)
        val response = call.execute()
        if (response.isSuccessful) {
            if (response.body() != null) {
                val eventsList = response.body()
                if (eventsList != null) {
                    return eventsList.map { speakersEntityMapper.mapFromRemote(it) }
                }
            }
        }
        throw IOException()
    }

    /**
     * Retrieve a list of [EventEntity] instances from the [ConferencesApi].
     */
    override fun getEvents(): List<EventEntity> {
        val call = conferenceApi.getEvents(conferenceId)
        val response = call.execute()
        if (response.isSuccessful) {
            if (response.body() != null) {
                val eventsList = response.body()
                if (eventsList != null) {
                    return eventsList.map { entityMapper.mapFromRemote(it) }
                }
            }
        }
        throw IOException()
    }
}
