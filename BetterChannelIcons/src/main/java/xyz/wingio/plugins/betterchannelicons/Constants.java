package xyz.wingio.plugins.betterchannelicons;

import com.aliucord.PluginManager;
import com.aliucord.utils.ReflectUtils;
import com.lytefast.flexinput.R;

import java.util.*;
import java.lang.reflect.*;

public class Constants {
    public static Field[] fields = R.d.class.getDeclaredFields();
    public static final Map<String, Integer> iconMap = new HashMap<>() {{
        put("Chat Bubble", R.d.ic_chat_message_white_24dp);
        put("Help", R.d.ic_help_24dp);
        put("Info", R.d.ic_info_24dp);
        put("Role", R.d.ic_shieldstar_24dp);
        put("Art", R.d.ic_theme_24dp);
        put("Laughing", R.d.ic_emoji_picker_category_people);
        put("Hand Raised/Waving", R.d.ic_raised_hand_action_24dp);
        put("Media", R.d.ic_flex_input_image_24dp_dark);
        put("Changelog", R.d.ic_history_white_24dp);
        put("Logs/Channels", R.d.ic_channels_24dp);
        put("Star", R.d.ic_star_24dp);
        put("Link", R.d.ic_diag_link_24dp);
        put("Microphone", R.d.ic_mic_grey_24dp);
        put("Mic Muted", R.d.ic_mic_muted_grey_24dp);
        put("Headset", R.d.ic_headset_24dp);
        put("Github", R.d.ic_github_white);
        put("D-Pad", R.d.ic_games_24dp);
        put("Controller", R.d.ic_controller_24dp);
    }};

    public static List<Integer> getIcons() throws Throwable{
        List<Integer> icons = new ArrayList<>();
        for(Field field : fields) {
            icons.add((Integer) field.get(R.d.class));
        }
        return icons;
    }
}