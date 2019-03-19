package org.dukecon.android.ui.features.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import org.dukecon.android.ui.R
import org.dukecon.android.ui.features.eventdetail.EventDetailActivity
import org.dukecon.android.ui.utils.DrawableUtils
import org.dukecon.android.ui.utils.asBitmap
import org.dukecon.domain.features.notifications.NotificationProvider
import org.dukecon.domain.model.Event

class LocalNotificationProvider(private val context: Context) : NotificationProvider {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun scheduleFeedbackNotification(session: Event) {
        val pendingIntent = createPendingIntent(session)
        session.endTime.toInstant().toEpochMilli().let {
            alarmManager.set(AlarmManager.ELAPSED_REALTIME, it, pendingIntent)
        }
    }

    override fun unscheduleFeedbackNotification(session: Event) {
        val pendingIntent = createPendingIntent(session)
        alarmManager.cancel(pendingIntent)
    }

    private fun createPendingIntent(session: Event): PendingIntent {
        val noteBuilder = NotificationCompat.Builder(context, context.getString(R.string.feedback_note_channel))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getString(R.string.note_feedback_title, session.title))
                .setContentText(context.getString(R.string.note_feedback_msg))
                .setAutoCancel(true)

        DrawableUtils.create(context, R.mipmap.ic_launcher)?.asBitmap().let {
            noteBuilder.setLargeIcon(it)
        }

        val intent = Intent(context, EventDetailActivity::class.java)
        intent.putExtra("session_id", session.eventId)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        noteBuilder.setContentIntent(pendingIntent)

        val noteIntent = Intent(context, NotificationPublisher::class.java)
        noteIntent.putExtra("NOTIFICATION", noteBuilder.build())
        noteIntent.putExtra("NOTIFICATION_ID", 0)
        return PendingIntent.getBroadcast(context, 0, noteIntent, PendingIntent.FLAG_CANCEL_CURRENT)
    }
}