package org.dukecon.android.ui.features.event

import android.content.Context
import android.text.format.DateUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.view_sessions.view.*
import org.dukecon.android.ui.R
import org.dukecon.android.ui.ext.getComponent
import org.dukecon.android.ui.features.main.MainComponent
import org.dukecon.presentation.feature.event.EventDateListContract
import org.joda.time.DateTime
import javax.inject.Inject

class EventDateView(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
        FrameLayout(context, attrs, defStyle), EventDateListContract.View {

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    @Inject
    lateinit var presenter: EventDateListContract.Presenter

    private val adapter: SessionPagerAdapter

    init {
        context.getComponent<MainComponent>().sessionListComponent().inject(this)

        LayoutInflater.from(context).inflate(R.layout.view_sessions, this, true)

        adapter = SessionPagerAdapter()
        pager.adapter = adapter

        tabs.setupWithViewPager(pager)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        presenter.onAttach(this)
    }

    override fun onDetachedFromWindow() {
        presenter.onDetach()
        super.onDetachedFromWindow()
    }

    override fun showNoSessionDates() {
        adapter.clear()
        adapter.notifyDataSetChanged()
    }

    override fun showSessionDates(eventDate: List<DateTime>) {
        adapter.showEventDates(eventDate)
        adapter.notifyDataSetChanged()

        if (eventDate.size > 1) {
            tabs.visibility = View.VISIBLE
        }
    }

    override fun scrollToCurrentDay() {
        if (adapter.dates.isNotEmpty()) {
            val index = adapter.dates.indexOfFirst { DateUtils.isToday(it.millis) }
            if (index >= 0) {
                pager.setCurrentItem(index, false)
            }
        }
    }
}