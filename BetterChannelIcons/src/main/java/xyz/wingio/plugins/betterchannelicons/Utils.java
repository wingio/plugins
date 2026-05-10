package xyz.wingio.plugins.betterchannelicons;

import android.content.Context;
import android.graphics.drawable.*;
import android.graphics.*;
import android.view.*;
import android.widget.*;

import androidx.core.graphics.ColorUtils;
import androidx.core.content.ContextCompat;

import com.aliucord.Logger;
import com.aliucord.PluginManager;
import com.aliucord.utils.*;
import com.aliucord.api.*;
import com.aliucord.patcher.*;
import com.aliucord.wrappers.*;

import com.discord.api.channel.Channel;
import com.discord.databinding.*;
import com.discord.utilities.color.ColorCompat;
import com.discord.utilities.permissions.PermissionUtils;
import com.discord.widgets.channels.list.*;
import com.lytefast.flexinput.R;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.*;
import java.util.*;

public class Utils {
    public static final Type iconStoreType = TypeToken.getParameterized(HashMap.class, String.class, String.class).getType();
    public static final Type oldIconStoreType = TypeToken.getParameterized(HashMap.class, String.class, Integer.class).getType();
    private static SettingsAPI settings = PluginManager.plugins.get("BetterChannelIcons").settings;

    public static Drawable themeDrawable(Context ctx, Drawable drawable){
        drawable = drawable.mutate();
        drawable.setTint(ColorUtils.setAlphaComponent(ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal), 153));
        return drawable;
    }

    public static Integer getChannelIcon(ChannelWrapper channel) throws Throwable {
        if(channel == null || channel.getName() == null) return null;
        var name = channel.getName().toLowerCase();
        Map<String, String> icons = settings.getObject("icons", new HashMap<>(), iconStoreType);
        if(channel.getId() != 0 && icons.containsKey("id:" + channel.getId())) return com.aliucord.Utils.getResId(icons.get("id:" + channel.getId()), "drawable");
        if(icons.containsKey(name)) return com.aliucord.Utils.getResId(icons.get(name), "drawable");
        if(name.endsWith("-logs") || name.endsWith("-log")) return R.e.ic_channels_24dp;
        if(name.endsWith("-support") || name.endsWith("-help")) return R.e.ic_help_24dp;
        for (String key : icons.keySet()) {
            if (key.startsWith("startswith:") && name.startsWith(key.split(":", 2)[1])) return com.aliucord.Utils.getResId(icons.get(key), "drawable");
            if (key.startsWith("endswith:") && name.endsWith(key.split(":", 2)[1])) return com.aliucord.Utils.getResId(icons.get(key), "drawable");
            if (key.startsWith("contains:") && name.contains(key.split(":", 2)[1])) return com.aliucord.Utils.getResId(icons.get(key), "drawable");
        }
        if(channel.getId() == 824357609778708580L) return R.e.ic_theme_24dp;
        if(channel.getType() == Channel.GUILD_VOICE) {
            if(name.startsWith("discord.gg/") || name.startsWith(".gg/") || name.startsWith("gg/") || name.startsWith("dsc.gg/")) return R.e.ic_diag_link_24dp;
            if(name.startsWith("member count") || name.startsWith("members") || name.startsWith("member count")) return R.e.ic_people_white_24dp;
            return voiceChannelIcons.get(name);
        }
        return channelIcons.get(name);
    }

    public static Map<String, Integer> channelIcons = new HashMap<String, Integer>() {{
        put("faq", R.e.ic_help_24dp);
        put("help", R.e.ic_help_24dp);
        put("support", R.e.ic_help_24dp);
        put("info", R.e.ic_info_24dp);
        put("roles", R.e.ic_shieldstar_24dp);
        put("role-info", R.e.ic_shieldstar_24dp);
        put("offtopic", R.e.ic_chat_message_white_24dp);
        put("off-topic", R.e.ic_chat_message_white_24dp);
        put("general", R.e.ic_chat_message_white_24dp);
        put("general-chat", R.e.ic_chat_message_white_24dp);
        put("general-talk", R.e.ic_chat_message_white_24dp);
        put("talk", R.e.ic_chat_message_white_24dp);
        put("chat", R.e.ic_chat_message_white_24dp);
        put("art", R.e.ic_theme_24dp);
        put("fanart", R.e.ic_theme_24dp);
        put("fan-art", R.e.ic_theme_24dp);
        put("bot", R.e.ic_slash_command_24dp);
        put("bots", R.e.ic_slash_command_24dp);
        put("bot-spam", R.e.ic_slash_command_24dp);
        put("bot-commands", R.e.ic_slash_command_24dp);
        put("commands", R.e.ic_slash_command_24dp);
        put("memes", R.e.ic_emoji_picker_category_people);
        put("meme", R.e.ic_emoji_picker_category_people);
        put("meme-chat", R.e.ic_emoji_picker_category_people);
        put("shitpost", R.e.ic_emoji_picker_category_people);
        put("introductions", R.e.ic_raised_hand_action_24dp);
        put("introduce-yourself", R.e.ic_raised_hand_action_24dp);
        put("welcome", R.e.ic_raised_hand_action_24dp);
        put("welcomes", R.e.ic_raised_hand_action_24dp);
        put("intros", R.e.ic_raised_hand_action_24dp);
        put("media", R.e.ic_flex_input_image_24dp_dark);
        put("changes", R.e.ic_history_white_24dp);
        put("changelog", R.e.ic_history_white_24dp);
        put("logs", R.e.ic_channels_24dp);
        put("modlogs", R.e.ic_channels_24dp);
        put("starboard", R.e.ic_star_24dp);
        put("resources", R.e.ic_diag_link_24dp);
        put("links", R.e.ic_diag_link_24dp);
        put("socials", R.e.ic_diag_link_24dp);
        put("vc", R.e.ic_mic_grey_24dp);
        put("muted", R.e.ic_mic_grey_24dp);
        put("vc-chat", R.e.ic_mic_grey_24dp);
        put("voice-chat", R.e.ic_mic_grey_24dp);
        put("no-mic", R.e.ic_mic_grey_24dp);
        put("music", R.e.ic_headset_24dp);
        put("github", R.e.ic_account_github_white_24dp);
        put("github-commits", R.e.ic_account_github_white_24dp);
        put("github-notifications", R.e.ic_account_github_white_24dp);
    }};

    public static Map<String, Integer> voiceChannelIcons = new HashMap<String, Integer>() {{
        put("music", R.e.ic_headset_24dp);
    }};

    public static Map<String, String> convertToNewFormat(Map<String, Integer> icons) throws Throwable{
        Map<String, String> newIcons = new HashMap<>();
        Map<Integer, String> iconNameMap = Constants.getIconNameMap();
        List<String> keys = new ArrayList<>(icons.keySet());
        for(String key : keys) {
            Integer iconIndex = icons.get(key);
            Integer icon = Constants.getIcons().get(iconIndex);
            newIcons.put(key, iconNameMap.get(icon));
        }
        return newIcons;
    }
}
