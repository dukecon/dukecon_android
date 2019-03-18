package org.dukecon.android.ui.features.event

import android.content.Context
import android.text.format.DateUtils
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import org.threeten.bp.OffsetDateTime

internal class SessionPagerAdapter : PagerAdapter() {
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    var dates: List<OffsetDateTime> = emptyList()
    var context: Context? = null

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        context = container.context
        val v = EventsListView(container.context)
        v.setDate(dates[position], showFavoritesOnly)
        container.addView(v)
        return v
    }

    override fun getPageTitle(position: Int): CharSequence {
        return DateUtils.formatDateTime(context, dates[position].toInstant().toEpochMilli(), DateUtils.FORMAT_SHOW_DATE)
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int {
        return dates.size
    }

    fun clear() {
        dates = arrayListOf()
    }

    private var showFavoritesOnly: Boolean = false;

    fun showEventDates(eventDate: List<OffsetDateTime>, showFavoritesOnly: Boolean) {
        dates = eventDate
        this.showFavoritesOnly = showFavoritesOnly
    }
}