package xyz.wingio.plugins.usertags

import com.aliucord.PluginManager
import com.aliucord.Utils
import com.aliucord.api.CommandsAPI
import com.aliucord.api.CommandsAPI.CommandResult
import com.aliucord.entities.CommandContext
import com.discord.api.commands.ApplicationCommandType
import com.discord.models.commands.ApplicationCommandOption

fun CommandsAPI.registerCommand() {
    registerCommand(
        "usertags",
        "Modify a tag for a particular user",
        listOf(setOption, clearOption)
    ) { ctx: CommandContext ->
        when {
            ctx.containsArg("set") -> tagSet(ctx)
            ctx.containsArg("clear") -> tagClear(ctx)
            else -> CommandResult()
        }
    }
}

private fun tagSet(ctx: CommandContext): CommandResult {
    val settings = PluginManager.plugins["UserTags"]!!.settings
    val setArgs = ctx.getSubCommandArgs("set")

    val user = setArgs!!["user"] as String?
    val label = setArgs["label"] as String?
    val verified = setArgs["verified"] as Boolean? ?: false

    if (user == null || user == "" || label == null || label == "") {
        return CommandResult("Missing arguments", null, false)
    }

    settings.setString(user, label.toString())
    settings.setBool(user + "_verified", verified)
    return CommandResult("Set tag", null, false)
}

private fun tagClear(ctx: CommandContext): CommandResult {
    val settings = PluginManager.plugins["UserTags"]!!.settings
    val clearArgs = ctx.getSubCommandArgs("clear")
    val user = clearArgs!!["user"] as String?

    if (user == null || user == "") {
        return CommandResult("Missing arguments", null, false)
    }

    settings.setString(user, null)
    return CommandResult("Cleared tag", null, false)
}

private val userOption: ApplicationCommandOption = Utils.createCommandOption(
    type = ApplicationCommandType.USER,
    name = "user",
    description = "User you want to give a tag to",
    required = true,
    default = true,
)

private val labelOption: ApplicationCommandOption = Utils.createCommandOption(
    type = ApplicationCommandType.STRING,
    name = "label",
    description = "The label for the tag",
    required = true,
    default = true,
)

private val verifiedOption: ApplicationCommandOption = Utils.createCommandOption(
    type = ApplicationCommandType.BOOLEAN,
    name = "verified",
    description = "Whether the tag should show as verified",
    required = false,
    default = true,
)

private val setOption: ApplicationCommandOption = Utils.createCommandOption(
    type = ApplicationCommandType.SUBCOMMAND,
    name = "set",
    description = "Set a tag",
    required = true,
    default = true,
    subCommandOptions = listOf(userOption, labelOption, verifiedOption),
)

private val clearOption: ApplicationCommandOption = Utils.createCommandOption(
    type = ApplicationCommandType.SUBCOMMAND,
    name = "clear",
    description = "Clear a tag",
    required = true,
    default = true,
    subCommandOptions = listOf(userOption),
)