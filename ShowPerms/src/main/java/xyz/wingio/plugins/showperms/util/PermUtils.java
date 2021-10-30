package xyz.wingio.plugins.showperms.util;

import xyz.wingio.plugins.ShowPerms;
import xyz.wingio.plugins.showperms.PermData;

import com.aliucord.api.SettingsAPI;
import com.aliucord.PluginManager;
import com.aliucord.utils.ReflectUtils;
import com.discord.api.role.GuildRole;
import com.discord.api.permission.*;
import com.discord.utilities.permissions.PermissionUtils;
import com.discord.api.permission.PermissionOverwrite;

import java.util.*;
import java.lang.reflect.*;
import java.lang.*;

public class PermUtils {
    public PermUtils INSTANCE = new PermUtils();
    public static Field[] fields = Permission.class.getDeclaredFields();
    public static List<String> ignoredFieldsList = Arrays.asList("INSTANCE", "DEFAULT", "ALL", "NONE", "ELEVATED", "MODERATOR_PERMISSIONS", "MANAGEMENT_PERMISSIONS");
    public static SettingsAPI settings = PluginManager.plugins.get("ShowPerms").settings;

    public PermUtils() {}

    public static String capitalizeString(String string) {
        char[] chars = string.toLowerCase().toCharArray();
        boolean found = false;
        for (int i = 0; i < chars.length; i++) {
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                found = true;
            } else if (Character.isWhitespace(chars[i]) || chars[i]=='.' || chars[i]=='\'') { // You can add other chars here
                found = false;
            }
        }
        return String.valueOf(chars);
    }

    public static List<String> getPermissions() throws Throwable{
        List<String> permissions = new ArrayList<>();
        for(Field field : fields){
            if(!ignoredFieldsList.contains(field.getName())){
                Long permBit = (Long) field.get(Permission.INSTANCE);
                permissions.add(capitalizeString(field.getName().replaceAll("_", " ").toLowerCase()));
            }
        }
        return permissions;
    }

    public static List<String> getPermissions(Long bits) throws Throwable {
        List<String> permissions = new ArrayList<>();
        int format = settings.getInt("format", 0);
        boolean showFullAdmin = (format == 1);
        for(Field field : fields){
            if(!ignoredFieldsList.contains(field.getName())){
                Long permBit = (Long) field.get(Permission.INSTANCE);
                if(PermissionUtils.can(permBit, bits) || (PermissionUtils.can(Permission.ADMINISTRATOR, bits) && showFullAdmin)){
                    permissions.add(capitalizeString(field.getName().replaceAll("_", " ").toLowerCase()));
                }
            }
        }
        return permissions;
    }

    public static Map<String, PermData> getPermissions(List<GuildRole> guildRoles) throws Throwable {
        Map<String, PermData> permissions = new LinkedHashMap<>();
        for(GuildRole role : guildRoles) {
            if(role != null) {
                Long perms = (Long) ReflectUtils.getField(GuildRole.class, role, "permissions");
                List<String> permnames = getPermissions(perms);
                for (String permName : permnames) {
                    if(!permissions.containsKey(permName)){
                        permissions.put(permName, new PermData(permName, role));
                    }
                }
            }
        }

        return permissions;
    }

    public static long applyOverwrites(long bits, PermissionOverwrite overwrite) throws Throwable {
        long newBits = 0L;
        newBits = bits | overwrite.c();
        newBits = newBits &~ overwrite.d();
        return newBits;
    }
  
}