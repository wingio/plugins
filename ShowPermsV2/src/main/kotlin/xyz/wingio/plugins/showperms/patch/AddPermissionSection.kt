@file:Suppress(
    "MISSING_DEPENDENCY_CLASS", "MISSING_DEPENDENCY_SUPERCLASS",
    "MISSING_DEPENDENCY_SUPERCLASS_WARNING", "ERROR_SUPPRESSION"
)

package xyz.wingio.plugins.showperms.patch

import android.annotation.SuppressLint
import android.view.View
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.LinearLayout.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
import android.widget.TextView
import com.google.android.material.chip.ChipGroup

import com.aliucord.Utils
import com.aliucord.api.PatcherAPI
import com.aliucord.patcher.after
import com.aliucord.patcher.component1
import com.aliucord.patcher.component2
import com.aliucord.utils.DimenUtils.dp
import com.aliucord.utils.ReflectUtils
import com.aliucord.wrappers.ChannelWrapper.Companion.name
import com.aliucord.wrappers.ChannelWrapper.Companion.permissionOverwrites
import com.aliucord.wrappers.GuildRoleWrapper.Companion.color
import com.aliucord.wrappers.GuildRoleWrapper.Companion.name
import com.aliucord.wrappers.GuildRoleWrapper.Companion.position

import com.discord.databinding.WidgetUserSheetBinding
import com.discord.stores.StoreStream
import com.discord.widgets.user.usersheet.WidgetUserSheet
import com.discord.widgets.user.usersheet.WidgetUserSheetViewModel

import xyz.wingio.plugins.showperms.util.Format
import xyz.wingio.plugins.showperms.view.PermChip
import xyz.wingio.plugins.showperms.util.Permission
import xyz.wingio.plugins.showperms.util.PermUtil
import xyz.wingio.plugins.showperms.util.PermUtil.applyOverwrites
import xyz.wingio.plugins.showperms.util.Settings
import xyz.wingio.plugins.showperms.util.sortedBy

import java.util.ArrayList
import com.lytefast.flexinput.R

private val sectionId = View.generateViewId()
private val contentId = Utils.getResId("user_sheet_content", "id")
private val guildHeaderId = Utils.getResId("user_sheet_guild_header", "id")
private val connectionsHeaderId = Utils.getResId("user_sheet_connections_header", "id")

@SuppressLint("SetTextI18n")
fun PatcherAPI.addPermissionSection() {
    after<WidgetUserSheet>(
        "configureGuildSection",
        WidgetUserSheetViewModel.ViewState.Loaded::class.java
    ) { (_, viewState: WidgetUserSheetViewModel.ViewState.Loaded) ->
        if (viewState.guildMember == null) return@after

        // Views
        val binding = ReflectUtils.invokeMethod(this, "getBinding") as WidgetUserSheetBinding
        val content = binding.root.findViewById<LinearLayout>(contentId)
        val guildHeader = content.findViewById<TextView>(guildHeaderId)
        val connectionsHeader = content.findViewById<TextView>(connectionsHeaderId)

        val context = content.context

        // Relevant information
        val guild = StoreStream.getGuilds().getGuild(viewState.guildId)!!
        val guildRoles = StoreStream.getGuilds().roles[guild.id] ?: return@after
        val userRoles = ArrayList(viewState.roleItems!!)
        val applyOverwrites = Settings.applyOverwrites && viewState.channel != null
        if (guildRoles.containsKey(guild.id)) userRoles.add(guildRoles[guild.id]) // Adds the implicit @everyone role

        if (Settings.showRoleCount && userRoles.isNotEmpty()) {
            guildHeader.text = "${guild.name} • ${userRoles.size} roles"
        }

        // Creating the permissions section
        // ================================

        val section = LinearLayout(context).apply {
            id = sectionId
            orientation = LinearLayout.VERTICAL
            layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
                setMargins(16.dp, 0, 16.dp, 0)
            }
        }
        if (content.findViewById<LinearLayout>(sectionId) == null) content.addView(
            section,
            content.indexOfChild(connectionsHeader)
        )

        TextView(context, null, 0, R.i.UserProfile_Section_Header).apply {
            text = "Permissions${if (applyOverwrites) " - #${viewState.channel.name}" else ""}"
            layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
                setMargins(0, 0, 0, 8.dp)
            }
            section.addView(this)
        }

        val permList = ChipGroup(context).apply {
            chipSpacingVertical = 4.dp
            chipSpacingHorizontal = 4.dp
            addView(PermChip.detailsButton(context))
            section.addView(this)
        }

        if (guild.isOwner(viewState.user.id)) {
            PermChip(context).apply {
                setPermName("Server Owner")
                permList.addView(this)
            }
            return@after // Permissions are irrelevant for server owners
        }

        // Obtain, sort, and filter the user's permissions
        // ===============================================

        PermUtil.getRolePermissions(userRoles)
            .let { perms ->
                if (applyOverwrites) perms.applyOverwrites(
                    overwrites = viewState.channel.permissionOverwrites,
                    roles = userRoles,
                    userId = viewState.user.id
                ) else perms
            }
            .let { perms -> // Filter based on user settings
                when (Settings.format) {
                    Format.Default   -> perms
                    Format.MinAdmin  -> perms.filter { (perm) -> perm == Permission.ADMINISTRATOR }.ifEmpty { perms }
                    Format.FullAdmin -> {
                        perms
                            .firstOrNull { (perm) -> perm == Permission.ADMINISTRATOR }
                            ?.let { (_, role) -> Permission.values().map { it to role } }
                            ?: perms
                    }
                }
            }
            .sortedBy(Settings.permissionNameSort) { (perm) -> perm.displayName } // Alphabetically
            .sortedBy(Settings.rolePosSort) { (_, role) -> role.position } // Highest to lowest role
            .forEach { (perm, role) ->
                permList.addView(
                    PermChip(context).apply {
                        setPermName(perm.displayName)
                        setDotColor(role.color)
                        setOnClickListener { Utils.showToast(role.name) }
                    }
                )
            }
    }
}