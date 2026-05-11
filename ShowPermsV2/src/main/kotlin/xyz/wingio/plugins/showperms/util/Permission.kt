package xyz.wingio.plugins.showperms.util

/**
 * A permission that can be assigned to a role, or overwritten in a channel.
 *
 * The order here matters as the ordinal is used as the amount of bits to shift when
 * calculating permissions from the bitfield.
 *
 * @param displayName Human-readable name for the permission
 */
enum class Permission(
    val displayName: String
) {
    CREATE_INSTANT_INVITE("Create Invite"),
    KICK_MEMBERS("Kick, Approve, and Reject Members"),
    BAN_MEMBERS("Ban Members"),
    ADMINISTRATOR("Administrator"),
    MANAGE_CHANNELS("Manage Channels"),
    MANAGE_GUILD("Manage Server"),
    ADD_REACTIONS("Add Reactions"),
    VIEW_AUDIT_LOG("View Audit Log"),
    PRIORITY_SPEAKER("Priority Speaker"),
    STREAM("Video"),
    VIEW_CHANNEL("View Channels"),
    SEND_MESSAGES("Send Messages"),
    SEND_TTS_MESSAGES("Send Text-to-Speech Messages"),
    MANAGE_MESSAGES("Manage Messages"),
    EMBED_LINKS("Embed Links"),
    ATTACH_FILES("Attach Files"),
    READ_MESSAGE_HISTORY("Read Message History"),
    MENTION_EVERYONE("Mention @everyone, @here, and All Roles"),
    USE_EXTERNAL_EMOJIS("Use External Emoji"),
    VIEW_GUILD_INSIGHTS("View Server Insights"),
    CONNECT("Connect"),
    SPEAK("Speak"),
    MUTE_MEMBERS("Mute Members"),
    DEAFEN_MEMBERS("Deafen Members"),
    MOVE_MEMBERS("Move Members"),
    USE_VAD("Use Voice Activity"),
    CHANGE_NICKNAME("Change Nickname"),
    MANAGE_NICKNAMES("Manage Nicknames"),
    MANAGE_ROLES("Manage Roles"),
    MANAGE_WEBHOOKS("Manage Webhooks"),
    MANAGE_EXPRESSIONS("Manage Expressions"),
    USE_APPLICATION_COMMANDS("Use Application Commands"),
    REQUEST_TO_SPEAK("Request to Speak"),
    MANAGE_EVENTS("Manage Events"),
    MANAGE_THREADS("Manage Threads"),
    CREATE_PUBLIC_THREADS("Create Public Threads"),
    CREATE_PRIVATE_THREADS("Create Private Threads"),
    USE_EXTERNAL_STICKERS("Use External Stickers"),
    SEND_MESSAGES_IN_THREADS("Send Messages in Threads"),
    USE_EMBEDDED_ACTIVITIES("Use Activities"),
    MODERATE_MEMBERS("Timeout Members"),
    VIEW_CREATOR_MONETIZATION_ANALYTICS("View Monetization Analytics"),
    USE_SOUNDBOARD("Use Soundboard"),
    CREATE_EXPRESSIONS("Create Expressions"),
    CREATE_EVENTS("Create Events"),
    USE_EXTERNAL_SOUNDS("Use External Sounds"),
    SEND_VOICE_MESSAGES("Send Voice Messages"),
    USE_CLYDE_AI("Use Clyde"), // Deprecated but here so that I can use the ordinal for the bit shift
    SET_VOICE_CHANNEL_STATUS("Set Voice Channel Status"),
    SEND_POLLS("Send Polls"),
    USE_EXTERNAL_APPS("Use External Apps"),
    PIN_MESSAGES("Pin Messages")
}