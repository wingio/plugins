@file:Suppress("MISSING_DEPENDENCY_CLASS", "MISSING_DEPENDENCY_SUPERCLASS")

package xyz.wingio.plugins.betterchannelicons

import android.annotation.SuppressLint
import android.content.res.Resources
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat

import com.aliucord.PluginManager
import com.aliucord.Utils.getResId
import com.aliucord.Utils.tintToTheme
import com.aliucord.api.PatcherAPI
import com.aliucord.patcher.*
import com.aliucord.utils.ReflectUtils
import com.aliucord.wrappers.ChannelWrapper

import com.discord.databinding.WidgetChannelsListItemActionsBinding
import com.discord.databinding.WidgetChannelsListItemChannelBinding
import com.discord.databinding.WidgetChannelsListItemChannelVoiceBinding
import com.discord.databinding.WidgetHomeBinding
import com.discord.stores.StoreStream
import com.discord.utilities.permissions.PermissionUtils
import com.discord.widgets.channels.list.WidgetChannelsListAdapter.ItemChannelText
import com.discord.widgets.channels.list.WidgetChannelsListAdapter.ItemChannelVoice
import com.discord.widgets.channels.list.WidgetChannelsListItemChannelActions
import com.discord.widgets.channels.list.items.ChannelListItem
import com.discord.widgets.channels.list.items.ChannelListItemTextChannel
import com.discord.widgets.channels.list.items.ChannelListItemVoiceChannel
import com.discord.widgets.home.WidgetHome
import com.discord.widgets.home.WidgetHomeHeaderManager
import com.discord.widgets.home.WidgetHomeModel

import com.lytefast.flexinput.R
import xyz.wingio.plugins.BetterChannelIcons

class Patches(private val patcher: PatcherAPI) {

    private val isSHCEnabled: Boolean
        get() = PluginManager.plugins["ShowHiddenChannels"] != null &&
                PluginManager.isPluginEnabled("ShowHiddenChannels")

    @SuppressLint("SetTextI18n")
    fun addChannelAction() {
        patcher.after<WidgetChannelsListItemChannelActions>("configureUI", WidgetChannelsListItemChannelActions.Model::class.java) { (_, model: WidgetChannelsListItemChannelActions.Model) ->
            try {
                val binding = ReflectUtils.invokeMethod(this,"getBinding") as WidgetChannelsListItemActionsBinding?
                val root = (binding!!.root as ViewGroup).getChildAt(0) as ViewGroup

                val editIcn = tintToTheme(ContextCompat.getDrawable(root.context, R.e.ic_edit_24dp)!!.mutate())

                val edit = TextView(root.context, null, 0, R.i.UiKit_Settings_Item_Icon).apply {
                    text = "Change Icon"
                    setCompoundDrawablesWithIntrinsicBounds(editIcn, null, null, null)
                    setOnClickListener {
                        AddChannelSheet(
                            ChannelWrapper(model.channel).id,
                            BetterChannelIcons.pluginSettings
                        ).show(this@after.parentFragmentManager, "Edit Channel Icon")
                    }
                }

                if (isSHCEnabled && !PermissionUtils.INSTANCE.hasAccess(model.channel, model.permissions)) {
                    root.addView(edit)
                } else {
                    root.addView(edit, 2)
                }
            } catch (e: Throwable) {
                BetterChannelIcons.logger.error("Error configuring channel actions", e)
            }
        }
    }

    fun setToolbarIcon(resources: Resources) {
        patcher.after<WidgetHomeHeaderManager>("configure", WidgetHome::class.java, WidgetHomeModel::class.java, WidgetHomeBinding::class.java) { (_, widget: WidgetHome, model: WidgetHomeModel, _: WidgetHomeBinding) ->
            try {
                if (!BetterChannelIcons.pluginSettings.getBool("setToolbarIcon", true)) return@after
                if (model.channel == null) return@after

                val channel = ChannelWrapper(model.channel)

                val root = widget.actionBarTitleLayout.k.root
                val channelIcon = root.findViewById<ImageView>(getResId("toolbar_icon", "id"))

                if (channel.isGuild()) {
                    val guild = StoreStream.getGuilds().guilds[channel.guildId]

                    if (guild!!.rulesChannelId != null && guild.rulesChannelId == channel.id) {
                        channelIcon.setImageDrawable(
                            tintToTheme(
                                drawable = ResourcesCompat.getDrawable(
                                    /* res = */ resources,
                                    /* id = */ resources.getIdentifier(
                                        /* name = */ "ic_rules_24dp",
                                        /* defType = */ "drawable",
                                        /* defPackage = */ "xyz.wingio.plugins"
                                    ),
                                    /* theme = */ null
                                )
                            )
                        )
                    }
                }

                if (channel.id == 811275162715553823L /* #plugins-list */ || channel.id == 845784407846813696L /* #new-plugins */) {
                    channelIcon.setImageDrawable(
                        tintToTheme(
                            drawable = ResourcesCompat.getDrawable(
                                /* res = */ resources,
                                /* id = */ resources.getIdentifier(
                                    /* name = */ "ic_plugin_24dp",
                                    /* defType = */ "drawable",
                                    /* defPackage = */ "xyz.wingio.plugins"
                                ),
                                /* theme = */ null
                            )
                        )
                    )
                }

                val icon = Utils.getChannelIcon(channel)

                if (icon != null) channelIcon.setImageDrawable(
                    tintToTheme(
                        drawable = ContextCompat.getDrawable(
                            /* context = */ widget.requireContext(),
                            /* id = */ icon
                        )
                    )
                )
            } catch (e: Throwable) {
                BetterChannelIcons.logger.error("Error setting channel icon in title bar", e)
            }
        }
    }

    fun setVoiceIcon() {
        patcher.after<ItemChannelVoice>("onConfigure", Int::class.java, ChannelListItem::class.java) { (_, _: Int, channelListItem: ChannelListItem) ->
            try {
                (channelListItem as? ChannelListItemVoiceChannel)?.let { channelItem ->
                    val channel = ChannelWrapper(channelItem.channel)
                    val icon = Utils.getChannelIcon(channel)

                    if (icon != null) {
                        val binding = ReflectUtils.getField(
                            /* instance = */ this,
                            /* fieldName = */ "binding"
                        ) as WidgetChannelsListItemChannelVoiceBinding?

                        binding!!.root.findViewById<ImageView>(
                            getResId(
                                name = "channels_item_voice_channel_speaker",
                                type = "id"
                            )
                        ).setImageResource(icon)
                    }
                }
            } catch (e: Throwable) {
                BetterChannelIcons.logger.error("Error setting channel icon", e)
            }
        }
    }

    fun setTextIcon(resources: Resources)  {
        patcher.after<ItemChannelText>("onConfigure", Int::class.java, ChannelListItem::class.java) { (_, _: Int, channelListItem: ChannelListItem) ->
            try {
                (channelListItem as? ChannelListItemTextChannel)?.let { channelItem ->
                    val channel = ChannelWrapper(channelItem.channel)

                    val binding = ReflectUtils.getField(
                        /* instance = */ this,
                        /* fieldName = */ "binding"
                    ) as WidgetChannelsListItemChannelBinding?

                    val channelIcon = binding!!.root.findViewById<ImageView>(
                        getResId(
                            name = "channels_item_channel_hash",
                            type = "id"
                        )
                    )

                    if (channel.isGuild()) {
                        val guild = StoreStream.getGuilds().guilds[channel.guildId]

                        if (guild!!.rulesChannelId != null && guild.rulesChannelId == channel.id) {
                            channelIcon.setImageDrawable(
                                ResourcesCompat.getDrawable(
                                    /* res = */ resources,
                                    /* id = */ resources.getIdentifier(
                                        /* name = */ "ic_rules_24dp",
                                        /* defType = */ "drawable",
                                        /* defPackage = */ "xyz.wingio.plugins"
                                    ),
                                    /* theme = */ null
                                )
                            )
                        }
                    }

                    if (channel.id == 811275162715553823L /* #plugins-list */ || channel.id == 845784407846813696L /* #new-plugins */) {
                        channelIcon.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                /* res = */ resources,
                                /* id = */ resources.getIdentifier(
                                    /* name = */ "ic_plugin_24dp",
                                    /* defType = */ "drawable",
                                    /* defPackage = */ "xyz.wingio.plugins"
                                ),
                                /* theme = */ null
                            )
                        )
                    }

                    Utils.getChannelIcon(channel)?.let { channelIcon.setImageResource(it) }
                }
            } catch (e: Throwable) {
                BetterChannelIcons.logger.error("Error setting channel icon", e)
            }
        }
    }

}