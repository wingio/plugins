package xyz.wingio.plugins.betterchannelicons

import com.aliucord.PluginManager
import com.aliucord.Utils.getResId
import com.aliucord.wrappers.ChannelWrapper

import com.discord.api.channel.Channel

import com.google.gson.reflect.TypeToken

import com.lytefast.flexinput.R

import java.lang.reflect.Type

object Utils {

    @JvmField
    val iconStoreType: Type = TypeToken.getParameterized(
        HashMap::class.java, String::class.java, String::class.java // HashMap<String, String>
    ).getType()

    private val settings = PluginManager.plugins["BetterChannelIcons"]!!.settings

    fun getChannelIcon(channel: ChannelWrapper?): Int? {
        if (channel?.name == null) return null

        val name = channel.name.lowercase()
        val icons: Map<String, String> = settings.getObject("icons", mapOf(), iconStoreType)

        // Icons chosen by user

        // Get icon specific to individual channel, rather than any with a given name
        if (channel.id != 0L && icons.containsKey("id:" + channel.id)) return getResId(
            icons["id:" + channel.id]!!,
            "drawable"
        )
        if (icons.containsKey(name)) return getResId(icons[name]!!, "drawable") // Exact channel name match

        // Default icons

        if (name.endsWith("-logs") || name.endsWith("-log")) return R.e.ic_channels_24dp // Log-esque icon for log channels
        if (name.endsWith("-support") || name.endsWith("-help")) return R.e.ic_help_24dp // Question mark in a circle for help and support channels
        if (channel.id == 824357609778708580L) return R.e.ic_theme_24dp // Special icon for #themes channel in the Aliucord server

        if (channel.type == Channel.GUILD_VOICE) {
            if (listOf("discord.gg/", ".gg/", "gg/", "dsc.gg/").any { name.startsWith(it) }) return R.e.ic_diag_link_24dp // Some servers have a voice channel that they use to display the invite
            return if (listOf("member count", "members").any { name.startsWith(it) }) R.e.ic_people_white_24dp else voiceChannelIcons[name] // Use group icon for member count VCs or fallback to a default vc icon
        }

        return channelIcons[name]
    }

    private val channelIcons: Map<String, Int> = mapOf(
        "faq" to R.e.ic_help_24dp,
        "help" to R.e.ic_help_24dp,
        "support" to R.e.ic_help_24dp,
        "info" to R.e.ic_info_24dp,
        "roles" to R.e.ic_shieldstar_24dp,
        "role-info" to R.e.ic_shieldstar_24dp,
        "offtopic" to R.e.ic_chat_message_white_24dp,
        "off-topic" to R.e.ic_chat_message_white_24dp,
        "general" to R.e.ic_chat_message_white_24dp,
        "general-chat" to R.e.ic_chat_message_white_24dp,
        "general-talk" to R.e.ic_chat_message_white_24dp,
        "talk" to R.e.ic_chat_message_white_24dp,
        "chat" to R.e.ic_chat_message_white_24dp,
        "art" to R.e.ic_theme_24dp,
        "fanart" to R.e.ic_theme_24dp,
        "fan-art" to R.e.ic_theme_24dp,
        "bot" to R.e.ic_slash_command_24dp,
        "bots" to R.e.ic_slash_command_24dp,
        "bot-spam" to R.e.ic_slash_command_24dp,
        "bot-commands" to R.e.ic_slash_command_24dp,
        "commands" to R.e.ic_slash_command_24dp,
        "memes" to R.e.ic_emoji_picker_category_people,
        "meme" to R.e.ic_emoji_picker_category_people,
        "meme-chat" to R.e.ic_emoji_picker_category_people,
        "shitpost" to R.e.ic_emoji_picker_category_people,
        "introductions" to R.e.ic_raised_hand_action_24dp,
        "introduce-yourself" to R.e.ic_raised_hand_action_24dp,
        "welcome" to R.e.ic_raised_hand_action_24dp,
        "welcomes" to R.e.ic_raised_hand_action_24dp,
        "intros" to R.e.ic_raised_hand_action_24dp,
        "media" to R.e.ic_flex_input_image_24dp_dark,
        "changes" to R.e.ic_history_white_24dp,
        "changelog" to R.e.ic_history_white_24dp,
        "logs" to R.e.ic_channels_24dp,
        "modlogs" to R.e.ic_channels_24dp,
        "starboard" to R.e.ic_star_24dp,
        "resources" to R.e.ic_diag_link_24dp,
        "links" to R.e.ic_diag_link_24dp,
        "socials" to R.e.ic_diag_link_24dp,
        "vc" to R.e.ic_mic_grey_24dp,
        "muted" to R.e.ic_mic_grey_24dp,
        "vc-chat" to R.e.ic_mic_grey_24dp,
        "voice-chat" to R.e.ic_mic_grey_24dp,
        "no-mic" to R.e.ic_mic_grey_24dp,
        "music" to R.e.ic_headset_24dp,
        "github" to R.e.ic_account_github_white_24dp,
        "github-commits" to R.e.ic_account_github_white_24dp,
        "github-notifications" to R.e.ic_account_github_white_24dp
    )

    private val voiceChannelIcons: Map<String, Int> = mapOf(
        "music" to R.e.ic_headset_24dp
    )

}