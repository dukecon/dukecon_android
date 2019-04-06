package org.dukecon.android.ui.features.timemachine

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import mu.KotlinLogging
import org.dukecon.android.ui.ext.getAppComponent
import org.threeten.bp.OffsetDateTime
import javax.inject.Inject

private val logger = KotlinLogging.logger {}

/**
 * An [BroadcastReceiver] subclass for handling calls via adb, e.g changing current date and time
 *
 * helper methods.
 */
class SetCustomdateTimeReceiver : BroadcastReceiver() {

    @Inject
    lateinit var currentTimeProvider: CustomizableCurrentTimeProvider

    override fun onReceive(context: Context?, intent: Intent?) {

        logger.info { "received" }

        if (context != null) {
            context.getAppComponent().inject(this)

            if (intent != null) {
                val action = intent.action
                if ("org.dukecon.android.ui.intent.TIME" == action) {
                    val extras = intent.extras
                    if (extras != null) {
                        handleSendText(extras) // Handle text being sent
                    }
                }
            }
        }
    }

    /***
     * adb shell am broadcast -a org.dukecon.android.ui.intent.TIME --es set_time "12:33:00 "
     * @param intent
     */
    private fun handleSendText(intent: Bundle) {
        val sharedText = intent.getString("set_time")
        if (sharedText != null) {
            logger.info { sharedText }
            try {
                val now = OffsetDateTime.now()
                val instant = OffsetDateTime.parse(sharedText).toInstant()
                currentTimeProvider.setCustomMillis(instant.toEpochMilli())
            } catch (e: IllegalArgumentException) {
                // ignore
            }
        }
    }
}
