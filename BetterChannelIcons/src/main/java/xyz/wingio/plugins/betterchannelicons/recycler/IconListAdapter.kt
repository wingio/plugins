@file:Suppress("MISSING_DEPENDENCY_CLASS", "MISSING_DEPENDENCY_SUPERCLASs")

package xyz.wingio.plugins.betterchannelicons.recycler

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

import com.aliucord.Utils.getResId
import com.aliucord.Utils.tintToTheme
import com.aliucord.fragments.SettingsPage

import xyz.wingio.plugins.BetterChannelIcons

class IconListAdapter(
    page: SettingsPage,
    private val icons: MutableMap<String, String>
) : RecyclerView.Adapter<IconListAdapter.IconListHolder>() {

    inner class IconListHolder(private val adapter: IconListAdapter, val item: RecyclerItem) :
        RecyclerView.ViewHolder(item), View.OnClickListener {

        override fun onClick(view: View) {
            adapter.onClick(view.context, adapterPosition)
        }

    }

    private val ctx: Context? = page.context

    override fun getItemCount(): Int {
        return ArrayList(icons.keys).size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconListHolder {
        return IconListHolder(this, RecyclerItem(ctx))
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: IconListHolder, position: Int) {
        val name = icons.keys.toList()[position]
        val iconId = icons[name]!!

        holder.item.name.text = name
        holder.item.delete.setOnClickListener {
            icons.remove(name)
            BetterChannelIcons.pluginSettings.setObject("icons", icons)
            notifyDataSetChanged()
        }

        try {
            val icon = tintToTheme(
                ContextCompat.getDrawable(
                    /* context = */ ctx!!,
                    /* id = */ getResId(name = iconId, type = "drawable")
                )!!.mutate()
            )

            holder.item.name.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null)
        } catch (e: Throwable) {
            BetterChannelIcons.logger.error("Failed to load icon", e)
        }
    }

    fun onClick(context: Context, adapterPosition: Int) {}

}
