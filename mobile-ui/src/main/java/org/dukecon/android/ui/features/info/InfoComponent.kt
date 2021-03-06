package org.dukecon.android.ui.features.info

import dagger.Subcomponent

@Subcomponent(modules = arrayOf(InfoModule::class))
interface InfoComponent {
    fun inject(infoView: LicenceView)
}