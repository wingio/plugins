package xyz.wingio.plugins.sessions

import com.discord.api.utcdatetime.UtcDateTime

data class Session (
    val idHash: String,
    val approxLastUsedTime: UtcDateTime,
    val clientInfo: ClientInfo
)

data class ClientInfo (
    val os: String?,
    val platform: String?,
    val location: String?
)

data class SessionResponse (
    val userSessions: MutableList<Session>
)

data class SessionsLogoutBody(
    val session_id_hashes: List<String>,
    val password: String,
    val code: String?
)