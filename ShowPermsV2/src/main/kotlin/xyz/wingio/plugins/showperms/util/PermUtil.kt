@file:Suppress("MISSING_DEPENDENCY_CLASS", "MISSING_DEPENDENCY_SUPERCLASS", "MISSING_DEPENDENCY_SUPERCLASS_WARNING")

package xyz.wingio.plugins.showperms.util

import com.aliucord.utils.ReflectUtils
import com.aliucord.utils.lazyField
import com.aliucord.wrappers.GuildRoleWrapper.Companion.position
import com.aliucord.wrappers.PermissionOverwriteWrapper.Companion.allowed
import com.aliucord.wrappers.PermissionOverwriteWrapper.Companion.denied
import com.aliucord.wrappers.PermissionOverwriteWrapper.Companion.id
import com.aliucord.wrappers.PermissionOverwriteWrapper.Companion.type
import com.discord.api.permission.PermissionOverwrite
import com.discord.api.role.GuildRole
import com.discord.models.user.User
import com.discord.stores.StoreStream

object PermUtil {

    private val permsField by lazyField<GuildRole>("permissions")

    /**
     * Creates a map containing every permission and the role that grants it.
     *
     * Each permission will only be attributed to one role, specifically the lowest role that grants it.
     * This means that the color associated with the permission will be for the role that actually adds the permission.
     *
     * @param roles Roles belonging to a particular guild member
     * @return A map of permissions to roles, represented as a list for easier filtering and altering
     */
    fun getRolePermissions(roles: List<GuildRole>): List<Pair<Permission, GuildRole>> {
        val rolePerms = mutableListOf<Pair<Permission, GuildRole>>()
        for (role in roles.reversed()) {
            val permBitfield = permsField[role] as? Long ?: continue
            val perms = getPermissions(permBitfield)

            for (permission in perms) {
                if (rolePerms.any { (perm) -> perm == permission }) continue
                rolePerms.add(permission to role)
            }
        }

        return rolePerms
    }

    /**
     * Applies channel-specific permission overwrites to the base permissions obtained using [getRolePermissions]
     *
     * @param overwrites The permission overwrites for a given channel
     * @param roles The roles belonging to the guild member
     * @param userId The user id of the guild member
     *
     * @return Updated map of permissions to roles, represented as a list for easier filtering and altering
     */
    fun List<Pair<Permission, GuildRole>>.applyOverwrites(
        overwrites: List<PermissionOverwrite>,
        roles: List<GuildRole>,
        userId: Long
    ): List<Pair<Permission, GuildRole>> {
        val rolePerms = this.toMutableList()
        val roleOverwrites = overwrites
            .filter { it.type == PermissionOverwrite.Type.ROLE && it.id in roles.map { r -> r.id } } // Only get relevant role overwrites
            .sortedBy { // Overwrites must be applied in ascending order of role position, starting with @everyone
                roles.first { r -> r.id == it.id }.position
            }

        roleOverwrites.forEach { overwrite ->
            rolePerms.applyOverwrite(overwrite, roles.first { it.id == overwrite.id })
        }

        overwrites.find { it.id == userId }?.let { // After role overwrites are applied, we apply the member-specific overwrites
            val user: User? = StoreStream.getUsers().users.getOrDefault(userId, null)
            rolePerms.applyOverwrite(it, createFakeRole(if (user == null) "Member Overwrite" else "@${user.username}"))
        }

        return rolePerms
    }

    /**
     * Parses a 64 bit integer bitfield into a list of permissions
     *
     * @param permissionBits A 64 bit integer representing a set of permissions
     * @return List of [Permissions][Permission]
     */
    private fun getPermissions(permissionBits: Long): List<Permission> {
        val list = mutableListOf<Permission>()
        Permission.values().forEach {
            val bit = 1L shl it.ordinal // The ordinal is used as the shift amount for convenience
            if (permissionBits and bit == bit) list.add(it)
        }
        return list
    }

    /**
     * Applies an individual overwrite to the previously obtained base permissions
     *
     * @param overwrite The overwrite to apply
     * @param role The role to attribute any new permissions to
     */
    private fun MutableList<Pair<Permission, GuildRole>>.applyOverwrite(overwrite: PermissionOverwrite, role: GuildRole) {
        val denied = getPermissions(overwrite.denied)
        val allowed = getPermissions(overwrite.allowed)

        removeIf { (perm) -> perm in denied }
        for (allowedPerm in allowed) {
            if (any { (perm) -> perm == allowedPerm }) continue
            add(allowedPerm to role)
        }
    }

    /**
     * Creates a [GuildRole] class using reflection. This fake role has no color,
     * and is always set to the highest possible position so that it appears above
     * all actual roles.
     *
     * @param name The name to give this fake role
     */
    private fun createFakeRole(name: String): GuildRole {
        return ReflectUtils.allocateInstance(GuildRole::class.java).apply {
            ReflectUtils.setField(this, "name", name)
            ReflectUtils.setField(this, "color", 0)
            ReflectUtils.setField(this, "position", Int.MAX_VALUE)
        }
    }

}