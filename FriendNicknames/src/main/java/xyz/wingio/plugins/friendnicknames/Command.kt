package xyz.wingio.plugins.friendnicknames

import com.aliucord.Utils
import com.aliucord.api.CommandsAPI
import com.aliucord.api.CommandsAPI.CommandResult
import com.aliucord.api.SettingsAPI
import com.aliucord.entities.CommandContext
import com.discord.api.commands.ApplicationCommandType
import com.discord.models.commands.ApplicationCommandOption

fun registerCommand(
    commandsAPI: CommandsAPI,
    settings: SettingsAPI
) {
    commandsAPI.registerCommand(
        "nick",
        "Modify a nickname for a particular user",
        listOf(setOption, clearOption)
    ) { ctx: CommandContext ->
        when {
            ctx.containsArg("set") -> nickSet(ctx, settings)
            ctx.containsArg("clear") -> nickClear(ctx, settings)
            else -> CommandResult("No subcommand provided", null, false)
        }
    }
}

private fun nickSet(ctx: CommandContext, settings: SettingsAPI): CommandResult {
    val setArgs = ctx.getSubCommandArgs("set")!!
    val user = setArgs["user"] as String?
    val nickname = setArgs["nickname"] as String?

    if (user == null || user == "" || nickname == null || nickname == "") {
        return CommandResult("Missing Arguments", null, false)
    }

    settings.setString(user, nickname)
    return CommandResult("Friend nickname updated!", null, false);
}

private fun nickClear(ctx: CommandContext, settings: SettingsAPI): CommandResult {
    val setArgs = ctx.getSubCommandArgs("clear")!!
    val user = setArgs["user"] as String?

    if (user == null || user == "") {
        return CommandResult("Missing Arguments", null, false)
    }

    settings.setString(user, null)
    return CommandResult("Friend nickname cleared!", null, false);
}

val userOption: ApplicationCommandOption = Utils.createCommandOption(
    type = ApplicationCommandType.USER,
    name = "user",
    description = "User you want to set a nickname to",
    required = true,
    default = true
)

val nickOption: ApplicationCommandOption = Utils.createCommandOption(
    type = ApplicationCommandType.STRING,
    name = "nickname",
    description = "The nickname",
    required = true,
    default = true,
)

val setOption: ApplicationCommandOption = Utils.createCommandOption(
    type = ApplicationCommandType.SUBCOMMAND,
    name = "set",
    description = "Set a nickname",
    required = true,
    default = true,
    subCommandOptions = listOf(userOption, nickOption),
)

val clearOption: ApplicationCommandOption = Utils.createCommandOption(
    type = ApplicationCommandType.SUBCOMMAND,
    name = "clear",
    description = "Clear a nickname",
    required = true,
    default = true,
    subCommandOptions = listOf(userOption),
)