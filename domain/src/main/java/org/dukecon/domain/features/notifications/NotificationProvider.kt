package org.dukecon.domain.features.notifications

import org.dukecon.domain.model.Event

interface NotificationProvider {
    fun scheduleFeedbackNotification(session: Event)
    fun unscheduleFeedbackNotification(session: Event)
}