package xyz.wingio.plugins.betterchannelicons;

import com.aliucord.PluginManager;
import com.aliucord.utils.ReflectUtils;
import com.lytefast.flexinput.R;

import java.util.*;
import java.lang.reflect.*;

public class Constants {
    public static Field[] fields = R.e.class.getDeclaredFields();
    public static final Map<String, String> iconMap = new HashMap<>() {{
        put("Chat Bubble", "ic_chat_message_white_24dp");
        put("Help", "ic_help_24dp");
        put("Info", "ic_info_24dp");
        put("Role", "ic_shieldstar_24dp");
        put("Art", "ic_theme_24dp");
        put("Laughing", "ic_emoji_picker_category_people");
        put("Hand Raised/Waving", "ic_raised_hand_action_24dp");
        put("Media", "ic_flex_input_image_24dp_dark");
        put("Changelog", "ic_history_white_24dp");
        put("Logs/Channels", "ic_channels_24dp");
        put("Star", "ic_star_24dp");
        put("Link", "ic_diag_link_24dp");
        put("Microphone", "ic_mic_grey_24dp");
        put("Mic Muted", "ic_mic_muted_grey_24dp");
        put("Headset", "ic_headset_24dp");
        put("Github", "ic_account_github_white_24dp");
        put("D-Pad", "ic_games_24dp");
        put("Controller", "ic_controller_24dp");
        put("Slash Command", "ic_slash_command_24dp");
    }};

    public static List<Integer> getIcons() throws Throwable{
        List<Integer> icons = new ArrayList<>();
        for(Field field : fields) {
            icons.add((Integer) field.get(R.e.class));
        }
        return icons;
    }

    public static Map<Integer, String> getIconNameMap() throws Throwable {
        Map<Integer, String> iconMap = new HashMap<>();
        for(Field field : fields) {
            iconMap.put((Integer) field.get(R.e.class), field.getName());
        }
        return iconMap;
    }

    public static Map<String, Integer> getIconMap() throws Throwable {
        Map<String, Integer> iconMap = new HashMap<>();
        for(Field field : fields) {
            iconMap.put(field.getName(), (Integer) field.get(R.e.class));
        }
        return iconMap;
    }

}