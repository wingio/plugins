@file:Suppress("MISSING_DEPENDENCY_CLASS", "MISSING_DEPENDENCY_SUPERCLASS")

package xyz.wingio.plugins.usertags

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.aliucord.PluginManager
import com.aliucord.Utils.getResId
import com.aliucord.api.PatcherAPI
import com.aliucord.patcher.*
import com.discord.databinding.UserProfileHeaderViewBinding
import com.discord.databinding.WidgetChannelMembersListItemUserBinding
import com.discord.models.message.Message
import com.discord.models.user.CoreUser
import com.discord.utilities.user.UserUtils
import com.discord.widgets.channels.memberlist.adapter.ChannelMembersListAdapter
import com.discord.widgets.channels.memberlist.adapter.ChannelMembersListViewHolderMember
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemMessage
import com.discord.widgets.user.profile.UserProfileHeaderView
import com.discord.widgets.user.profile.UserProfileHeaderViewModel
import com.lytefast.flexinput.R

fun PatcherAPI.patchTagInMessage() {
    val settings = PluginManager.plugins["UserTags"]!!.settings
    val tagViewId = getResId("chat_list_adapter_item_text_tag", "id")

    after<WidgetChatListAdapterItemMessage>("configureItemTag", Message::class.java, Boolean::class.javaPrimitiveType!!) { (_, message: Message, isForumPostOP: Boolean) ->
        val author = message.author ?: return@after
        val coreUser = CoreUser(author)

        val (tag, verified) = settings.getTag(coreUser.id)

        val isDev = coreUser.id == UserTags.DEV_ID
        val isServer = (message.type == 0 && message.messageReference != null)
        val isWebhook = message.webhookId != null

        val showTag = isDev || coreUser.isBot || isForumPostOP || coreUser.isSystemUser || isWebhook || tag != null
        val textView = itemView.findViewById<TextView>(tagViewId)

        if (textView != null) {
            textView.visibility = if (showTag) View.VISIBLE else View.GONE

            // TODO: Use the localized strings for these
            textView.text = when {
                isDev -> "DEV"
                coreUser.isSystemUser -> "SYSTEM"
                isServer -> "SERVER"
                isWebhook -> "WEBHOOK"
                coreUser.isBot -> "BOT"
                tag == null && isForumPostOP -> "OP"
                else -> tag
            }

            if (UserUtils.INSTANCE.isVerifiedBot(coreUser) || coreUser.id == UserTags.DEV_ID || verified) {
                textView.setCompoundDrawablesWithIntrinsicBounds(R.e.ic_verified_10dp, 0, 0, 0)
            }
        }
    }
}

fun PatcherAPI.patchTagInMemberList() {
    val settings = PluginManager.plugins["UserTags"]!!.settings
    val bindingField = ChannelMembersListViewHolderMember::class.java.getDeclaredField("binding").apply { isAccessible = true }

    after<ChannelMembersListViewHolderMember>("bind", ChannelMembersListAdapter.Item.Member::class.java, Function0::class.java) { (_, user: ChannelMembersListAdapter.Item.Member) ->
        val binding = bindingField[this] as WidgetChannelMembersListItemUserBinding
        val layout = binding.root as ConstraintLayout

        var (tag, verified) = settings.getTag(user.userId)
        val isDev = user.userId == UserTags.DEV_ID

        if (isDev) tag = "DEV"
        if (tag != null && !user.isBot) layout.findAndSetTag(tag, isDev, verified)
    }
}

fun PatcherAPI.patchTagInProfile() {
    val settings = PluginManager.plugins["UserTags"]!!.settings
    val profileBinding = UserProfileHeaderView::class.java.getDeclaredField("binding").apply { isAccessible = true }

    after<UserProfileHeaderView>("updateViewState", UserProfileHeaderViewModel.ViewState.Loaded::class.java) {(_, loadedState: UserProfileHeaderViewModel.ViewState.Loaded) ->
        val binding = profileBinding[this] as UserProfileHeaderViewBinding
        val user = loadedState.user ?: return@after
        val isDev = user.id == UserTags.DEV_ID

        var (tag, verified) = settings.getTag(user.id)

        if (isDev) tag = "UserTags Developer"
        if (tag != null && !user.isBot) binding.a.findAndSetTag(tag, isDev, verified)
    }
}