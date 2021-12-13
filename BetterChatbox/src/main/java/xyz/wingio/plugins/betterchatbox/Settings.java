package xyz.wingio.plugins.betterchatbox;

import com.aliucord.PluginManager;
import com.aliucord.api.SettingsAPI;
import com.aliucord.utils.DimenUtils;

public class Settings {
    public static SettingsAPI settings = PluginManager.plugins.get("BetterChatbox").settings;

    public static final int AVATAR_DISPLAY_NONE = 0;
    public static final int AVATAR_DISPLAY_NORMAL = 1;
    public static final int AVATAR_DISPLAY_INLINE = 2;

    public static String getHint() {
        return settings.getString("hint", "");
    }

    public static int getAvDisplay() {
        return settings.getInt("av_display", 0);
    }

    public static int getAvOnClick() {
        return settings.getInt("av_on_press", 1);
    }
    
    public static int getAvLongClick() {
        return settings.getInt("av_long_press", 2);
    }

    public static boolean showAvatar() {
        return getAvDisplay() != AVATAR_DISPLAY_NONE;
    }

    public static boolean useSmallBtn() {
        return settings.getBool("small_gallery_button", true);
    }

    public static boolean useOldIcn() {
        return settings.getBool("old_gallery_icon", false);
    }

    public static boolean useSquareChatbox() {
        return settings.getBool("square_chatbox", false);
    }

    public static boolean swapActions() {
        return settings.getBool("av_reverse", false);
    }

    public static boolean showSend() {
        return settings.getBool("show_send", false);
    }

    public static int getAvRadius() {
        return settings.getInt("av_r", DimenUtils.dpToPx(20));
    }

    public static int getCBRadius() {
        return settings.getInt("cb_r", DimenUtils.dpToPx(20));
    }

    public static int getBtnRadius() {
        return settings.getInt("btn_r", DimenUtils.dpToPx(20));
    }

    public static int getAvSize() {
        return settings.getInt("av_size", DimenUtils.dpToPx(40));
    }

    public static int getCbHeight() {
        return settings.getInt("cb_size", DimenUtils.dpToPx(40));
    }

    public static int getBtnSize() {
        return settings.getInt("btn_size", DimenUtils.dpToPx(40));
    }

    public static boolean shouldChangeMargin() {
        return (getAvDisplay() == AVATAR_DISPLAY_INLINE || getAvDisplay() == AVATAR_DISPLAY_NONE) && useSmallBtn();
    }
}