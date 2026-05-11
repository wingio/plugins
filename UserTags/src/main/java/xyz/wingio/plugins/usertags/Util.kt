package xyz.wingio.plugins.usertags

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.aliucord.Utils.getResId
import com.aliucord.api.SettingsAPI
import com.lytefast.flexinput.R

private val tagViewId = getResId("username_tag", "id")

fun SettingsAPI.getTag(userId: Long): Pair<String?, Boolean>
    = getString(userId.toString(), null) to getBool("${userId}_verified", false)

fun ViewGroup.findAndSetTag(tag: String, isDev: Boolean, verified: Boolean) {
    val tagText = findViewById<TextView>(tagViewId)
    tagText.visibility = View.VISIBLE
    tagText.text = tag
    if (isDev || verified) {
        tagText.setCompoundDrawablesWithIntrinsicBounds(R.e.ic_verified_10dp, 0, 0, 0)
    }
}