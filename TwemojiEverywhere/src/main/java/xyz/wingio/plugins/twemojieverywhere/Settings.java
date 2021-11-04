package xyz.wingio.plugins.twemojieverywhere;

import android.content.Context;

import com.aliucord.PluginManager;
import com.aliucord.api.SettingsAPI;

public class Settings {
    public static SettingsAPI settings = PluginManager.plugins.get("TwemojiEverywhere").settings;

    public static boolean inMemberList() {
        if(enabledEverywhere()) return false;
        return settings.getBool("show_in_member_list", true);
    }

    public static boolean inChatNames() {
        if(enabledEverywhere()) return false;
        return settings.getBool("chat_names", true);
    }

    public static boolean inProfileSheet() {
        if(enabledEverywhere()) return false;
        return settings.getBool("in_profile_sheet", true);
    }
    
    public static boolean inServerName() {
        if(enabledEverywhere()) return false;
        return settings.getBool("in_server_name", true);
    }

    public static boolean inServerPopoutName() {
        return settings.getBool("in_server_popout_name", false);
    }

    public static boolean enabledEverywhere() {
        return settings.getBool("true_twemoji", false);
    }
}