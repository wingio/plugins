package xyz.wingio.plugins.sessions

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.view.*
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.aliucord.PluginManager
import com.aliucord.plugins.R
import com.discord.utilities.color.ColorCompat
import com.discord.utilities.time.ClockFactory
import com.discord.utilities.time.TimeUtils
import xyz.wingio.plugins.utils.Utils

class Adapter(var data: MutableList<Session>) :
    RecyclerView.Adapter<Adapter.ViewHolder?>() {

    var onLogoutClick: (sessionId: String) -> Unit = {}

    class ViewHolder(private val adapter: Adapter, val item: SessionCard) : RecyclerView.ViewHolder(item)

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(this, SessionCard(parent.context))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val session = data[position]
        val mobileOsList = listOf("Android", "iOS")

        holder.item.logOutBtn.apply {
            visibility = View.VISIBLE
            setOnClickListener {
                onLogoutClick(session.idHash)
            }
        }

        holder.item.title = "${session.clientInfo.os ?: "Unknown"} ${if(session.clientInfo.platform != null) " · ${session.clientInfo.platform}" else ""}"
        holder.item.location = session.clientInfo.location ?: "Unknown Location"

        holder.item.timestamp.visibility = View.VISIBLE
        holder.item.timestampText = Utils.getTimestampString(session.approxLastUsedTime.g())

        holder.item.isMobile = mobileOsList.contains(session.clientInfo.os ?: "Unknown")
    }

    fun updateData(newData: List<Session>?) {
        newData?.let {
            data.clear()
            addData(newData)
        }
    }

    fun addData(data: List<Session>?) {
        this.data.addAll(data!!)
        notifyDataSetChanged()
    }

}