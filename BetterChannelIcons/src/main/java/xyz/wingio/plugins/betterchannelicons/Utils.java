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
        if(name.endsWith("-logs") || name.endsWith("-log")) return R.d.ic_channels_24dp;
        if(name.endsWith("-support") || name.endsWith("-help")) return R.d.ic_help_24dp;
        if(channel.getId() == 824357609778708580L) return R.d.ic_theme_24dp;
        if(channel.getType() == Channel.GUILD_VOICE) {
        if(name.startsWith("discord.gg/") || name.startsWith(".gg/") || name.startsWith("gg/") || name.startsWith("dsc.gg/")) return R.d.ic_diag_link_24dp;
        if(name.startsWith("member count") || name.startsWith("members") || name.startsWith("member count")) return R.d.ic_people_white_24dp;
        return voiceChannelIcons.get(name);
        }
        return channelIcons.get(name);
    }

    public static Map<String, Integer> channelIcons = new HashMap<String, Integer>() {{
        put("faq", R.d.ic_help_24dp);
        put("help", R.d.ic_help_24dp);
        put("support", R.d.ic_help_24dp);
        put("info", R.d.ic_info_24dp);
        put("roles", R.d.ic_shieldstar_24dp);
        put("role-info", R.d.ic_shieldstar_24dp);
        put("offtopic", R.d.ic_chat_message_white_24dp);
        put("off-topic", R.d.ic_chat_message_white_24dp);
        put("general", R.d.ic_chat_message_white_24dp);
        put("general-chat", R.d.ic_chat_message_white_24dp);
        put("general-talk", R.d.ic_chat_message_white_24dp);
        put("talk", R.d.ic_chat_message_white_24dp);
        put("chat", R.d.ic_chat_message_white_24dp);
        put("art", R.d.ic_theme_24dp);
        put("fanart", R.d.ic_theme_24dp);
        put("fan-art", R.d.ic_theme_24dp);
        put("bot", R.d.ic_slash_command_24dp);
        put("bots", R.d.ic_slash_command_24dp);
        put("bot-spam", R.d.ic_slash_command_24dp);
        put("bot-commands", R.d.ic_slash_command_24dp);
        put("commands", R.d.ic_slash_command_24dp);
        put("memes", R.d.ic_emoji_picker_category_people);
        put("meme", R.d.ic_emoji_picker_category_people);
        put("meme-chat", R.d.ic_emoji_picker_category_people);
        put("shitpost", R.d.ic_emoji_picker_category_people);
        put("introductions", R.d.ic_raised_hand_action_24dp);
        put("introduce-yourself", R.d.ic_raised_hand_action_24dp);
        put("welcome", R.d.ic_raised_hand_action_24dp);
        put("welcomes", R.d.ic_raised_hand_action_24dp);
        put("intros", R.d.ic_raised_hand_action_24dp);
        put("media", R.d.ic_flex_input_image_24dp_dark);
        put("changes", R.d.ic_history_white_24dp);
        put("changelog", R.d.ic_history_white_24dp);
        put("logs", R.d.ic_channels_24dp);
        put("modlogs", R.d.ic_channels_24dp);
        put("starboard", R.d.ic_star_24dp);
        put("resources", R.d.ic_diag_link_24dp);
        put("links", R.d.ic_diag_link_24dp);
        put("socials", R.d.ic_diag_link_24dp);
        put("vc", R.d.ic_mic_grey_24dp);
        put("muted", R.d.ic_mic_grey_24dp);
        put("vc-chat", R.d.ic_mic_grey_24dp);
        put("voice-chat", R.d.ic_mic_grey_24dp);
        put("no-mic", R.d.ic_mic_grey_24dp);
        put("music", R.d.ic_headset_24dp);
        put("github", R.d.ic_github_white);
        put("github-commits", R.d.ic_github_white);
        put("github-notifications", R.d.ic_github_white);
    }};

    public static Map<String, Integer> voiceChannelIcons = new HashMap<String, Integer>() {{
        put("music", R.d.ic_headset_24dp);
    }};

    public static Map<String, String> convertToNewFormat(Map<String, Integer> icons) throws Throwable{
        Map<String, String> newIcons = new HashMap<>();
        Map<Integer, String> iconNameMap = Constants.getIconNameMap();
        List<String> keys = new ArrayList<>(icons.keySet());
        for(String key : keys){
        Integer iconIndex = icons.get(key);
        Integer icon = Constants.getIcons().get(iconIndex);
        newIcons.put(key, iconNameMap.get(icon));
        }
        return newIcons;
    }
}