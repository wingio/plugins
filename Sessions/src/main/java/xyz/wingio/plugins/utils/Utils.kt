package xyz.wingio.plugins.utils

import android.text.format.DateUtils

object Utils {

    fun getTimestampString(millis: Long): CharSequence = DateUtils.getRelativeTimeSpanString(
        /* time = */ millis,
        /* now = */ System.currentTimeMillis(),
        /* minResolution = */ 0L,
        /* flags = */ DateUtils.FORMAT_ABBREV_TIME
    )

}