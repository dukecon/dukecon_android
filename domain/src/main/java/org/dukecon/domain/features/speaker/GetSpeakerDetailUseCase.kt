package org.dukecon.domain.features.speaker

import io.reactivex.Single
import org.dukecon.domain.executor.PostExecutionThread
import org.dukecon.domain.executor.ThreadExecutor
import org.dukecon.domain.interactor.SingleUseCase
import org.dukecon.domain.model.Speaker
import org.dukecon.domain.repository.EventRepository
import javax.inject.Inject

/**
 * Use case used for retreiving a single [Speaker] instances from the [EventRepository] with id
 */
open class GetSpeakerDetailUseCase @Inject constructor(val eventRepository: EventRepository,
                                                     threadExecutor: ThreadExecutor,
                                                     postExecutionThread: PostExecutionThread) :
        SingleUseCase<Speaker, String>(threadExecutor, postExecutionThread) {
    override fun buildUseCaseObservable(params: String?): Single<Speaker> {
        return eventRepository.getSpeaker(params as String)
    }

}