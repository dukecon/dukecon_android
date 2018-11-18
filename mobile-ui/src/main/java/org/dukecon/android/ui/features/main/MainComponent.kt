package org.dukecon.android.ui.features.main

import org.dukecon.android.ui.features.event.EventsListComponent
import dagger.Subcomponent
import org.dukecon.android.ui.features.login.di.LoginComponent
import org.dukecon.android.ui.features.speaker.SpeakerListComponent

@Subcomponent(modules = arrayOf(MainModule::class))
interface MainComponent {
    fun sessionListComponent(): EventsListComponent
    fun speakerListComponent(): SpeakerListComponent
    fun loginComponent(): LoginComponent
    fun inject(mainActivity: MainActivity)
}