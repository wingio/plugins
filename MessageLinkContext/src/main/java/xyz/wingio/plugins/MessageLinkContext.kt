package xyz.wingio.plugins

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.NestedScrollView
import com.aliucord.Constants
import com.aliucord.Utils
import com.aliucord.Utils.showToast
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.Hook
import com.discord.databinding.WidgetChatListActionsBinding
import com.discord.stores.StoreStream
import com.discord.utilities.color.ColorCompat
import com.discord.widgets.chat.list.actions.WidgetChatListActions
import com.lytefast.flexinput.R
import java.lang.reflect.InvocationTargetException


@AliucordPlugin(requiresRestart = true)
class MessageLinkContext : Plugin() {

    init {
        settingsTab = SettingsTab(
            PluginSettings::class.java,
            SettingsTab.Type.BOTTOM_SHEET
        ).withArgs(settings)
    }


    override fun start(context: Context) {


        val shareMessagesViewId = Utils.getResId("dialog_chat_actions_share", "id")
        val icon = ContextCompat.getDrawable(context, R.e.ic_link_white_24dp)!!
            .mutate()
        val copyMessageUrlViewId = View.generateViewId()


        with(WidgetChatListActions::class.java) {
            val getBinding = getDeclaredMethod("getBinding").apply { isAccessible = true }

            val replaceShare = settings.getBool("replaceShare", true)
            val hideShare = settings.getBool("hideShare", false)
            val addCopyUrl = settings.getBool("addCopyUrl", false)

            patcher.patch( //creating the option
                getDeclaredMethod("onViewCreated", View::class.java, Bundle::class.java),
                Hook { callFrame ->
                    val shareMessagesViewId = Utils.getResId("dialog_chat_actions_share", "id")
                    val binding =
                        getBinding.invoke(callFrame.thisObject) as WidgetChatListActionsBinding
                    val shareMessageView =
                        binding.a.findViewById<TextView>(shareMessagesViewId).apply {
                            visibility = View.VISIBLE
                        }
                    val linearLayout =
                        (callFrame.args[0] as NestedScrollView).getChildAt(0) as LinearLayout
                    val ctx = linearLayout.context
                    icon.setTint(ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal))
                    val copyMessageUrl =
                        TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Icon).apply {
                            text = "Copy Message Link"
                            id = copyMessageUrlViewId
                            typeface = ResourcesCompat.getFont(ctx, Constants.Fonts.whitney_medium)
                            setCompoundDrawablesRelativeWithIntrinsicBounds(icon, null, null, null)
                        }
                    if (replaceShare || hideShare) linearLayout.removeView(shareMessageView)
                    if (addCopyUrl || replaceShare) linearLayout.addView(
                        copyMessageUrl,
                        if (addCopyUrl) 9 else 14
                    )
                })

            patcher.patch( //setting onClickListener
                getDeclaredMethod("configureUI", WidgetChatListActions.Model::class.java),
                Hook { callFrame ->
                    if (!replaceShare && !addCopyUrl) {
                        return@Hook
                    }
                    try {
                        val binding =
                            getBinding.invoke(callFrame.thisObject) as WidgetChatListActionsBinding
                        val shareMessageView =
                            binding.a.findViewById<TextView>(copyMessageUrlViewId).apply {
                                visibility = View.VISIBLE
                            }

                        shareMessageView.setOnClickListener {
                            try {
                                val msg = (callFrame.args[0] as WidgetChatListActions.Model).message
                                var guildId =
                                    StoreStream.getChannels().getChannel(msg.channelId).h()
                                        .toString()
                                if (guildId == "0") guildId = "@me"
                                val imageUri = String.format(
                                    "https://discord.com/channels/%s/%s/%s",
                                    guildId,
                                    msg.channelId,
                                    msg.id
                                )
                                Utils.setClipboard(
                                    "message link",
                                    imageUri
                                )
                                showToast("Copied link", showLonger = false)
                                (callFrame.thisObject as WidgetChatListActions).dismiss()
                            } catch (e: IllegalAccessException) {
                                e.printStackTrace()
                            } catch (e: InvocationTargetException) {
                                e.printStackTrace()
                            }
                        }
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }
                })
        }
    }

    override fun stop(context: Context) = patcher.unpatchAll()
}
