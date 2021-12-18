package xyz.wingio.plugins.betterchatbox;

import android.content.Context;
import android.view.*;

import com.aliucord.Logger;
import com.aliucord.PluginManager;
import com.aliucord.Utils;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.api.*;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.*;
import com.aliucord.utils.*;
import com.aliucord.wrappers.*;
import com.aliucord.views.ToolbarButton;

import com.discord.widgets.debugging.WidgetDebugging;
import com.discord.widgets.chat.input.*;
import com.discord.widgets.user.usersheet.WidgetUserSheet;
import com.discord.widgets.user.*;

import com.lytefast.flexinput.fragment.FlexInputFragment;

import java.util.*;

public class Util {
    public static SettingsAPI settings = PluginManager.plugins.get("BetterChatbox").settings;

    public static class API {

        public static class OnPressAction {
            public String name;
            public Plugin plugin;
            public View.OnClickListener listener;
    
            public OnPressAction(String name, Plugin plugin, View.OnClickListener listener) {
                this.name = name;
                this.plugin = plugin;
                this.listener = listener;
            }
        }
    
        public static class OnLongPressAction {
            public String name;
            public Plugin plugin;
            public View.OnLongClickListener listener;
    
            public OnLongPressAction(String name, Plugin plugin, View.OnLongClickListener listener) {
                this.name = name;
                this.plugin = plugin;
                this.listener = listener;
            }
        }
        
        public static Map<Integer, OnPressAction> onPressActions = new HashMap<>();
        public static Map<Integer, OnLongPressAction> onLongPressActions = new HashMap<>();

        public static long gId = 0L;
        public static long cId = 0L;

        public static AppFlexInputViewModel vm;

        public static void setGId(long gId) {
            API.gId = gId;
        }

        public static void setCId(long cId) {
            API.cId = cId;
        }

        public static void setVm(AppFlexInputViewModel vm) {
            API.vm = vm;
        }

        public static void onPressAction(View view, Long guildId, Long meId, FlexInputFragment fragment) {
            int lp = Settings.getAvOnClick();
            switch(lp){
                case 0:
                    break;
                case 1:
                    if(guildId == 0) WidgetUserSheet.Companion.show(meId, fragment.getParentFragmentManager()); else WidgetUserSheet.Companion.show(meId, cId, fragment.getParentFragmentManager(), gId);
                    break;
                case 2:
                    WidgetUserStatusSheet.Companion.show(fragment);
                    break;
                case 3:
                    vm.onGalleryButtonClicked();
                    break;
                case 4:
                    WidgetDebugging.Companion.launch(fragment.getContext());
                    break;
                default:
                    if(lp > 4) {
                        try {
                            onPressActions.get(lp).listener.onClick(view);
                        } catch (Throwable ignored) {}
                    }
                    break;
            }
        }

        public static boolean onLongPressAction(View view, Long guildId, Long meId, FlexInputFragment fragment) {
            int lp = Settings.getAvLongClick();
            switch(lp){
                case 0:
                    return false;
                case 1:
                    if(guildId == 0) WidgetUserSheet.Companion.show(meId, fragment.getParentFragmentManager()); else WidgetUserSheet.Companion.show(meId, cId, fragment.getParentFragmentManager(), gId);
                    break;
                case 2:
                    WidgetUserStatusSheet.Companion.show(fragment);
                    break;
                case 3:
                    vm.onGalleryButtonClicked();
                    break;
                case 4:
                    WidgetDebugging.Companion.launch(fragment.getContext());
                    break;
                default:
                    if(lp > 4) {
                        try {
                            return onLongPressActions.get(lp).listener.onLongClick(view);
                        } catch (Throwable ignored) {
                            xyz.wingio.plugins.BetterChatbox.logger.error("Error while executing onLongPressAction", ignored);
                            return false;
                        }
                    }
                break;
            }
            return true;
        }

        public static void addOnPressAction(String name, Plugin plugin, View.OnClickListener listener) {
            OnPressAction action = new OnPressAction(name, plugin, listener);
            onPressActions.put(onPressActions.size() + 5, action);
        }

        public static void addOnLongPressAction(String name, Plugin plugin, View.OnLongClickListener listener) {
            OnLongPressAction action = new OnLongPressAction(name, plugin, listener);
            onLongPressActions.put(onLongPressActions.size() + 5, action);
        }

        public static void getOnPressActions() {
            for(Plugin plugin : PluginManager.plugins.values()) {
                if(!PluginManager.isPluginEnabled(plugin.getName())) continue;
                try {
                    View.OnClickListener listener = (View.OnClickListener) ReflectUtils.getField(plugin, "onAvatarPress");
                    if(listener != null) {
                    addOnPressAction(plugin.getName(), plugin, listener);
                    }
                } catch (Throwable ignored) {}
            }
            if(onPressActions.size() == 0 && Settings.getAvOnClick() > 4) settings.setInt("av_on_press", 0);
        }

        public static void getOnLongPressActions() {
            for(Plugin plugin : PluginManager.plugins.values()) {
                if(!PluginManager.isPluginEnabled(plugin.getName())) continue;
                try {
                    View.OnLongClickListener listener = (View.OnLongClickListener) ReflectUtils.getField(plugin, "onAvatarLongPress");
                    if(listener != null) {
                        addOnLongPressAction(plugin.getName(), plugin, listener);
                    }
                } catch (Throwable ignored) {}
            }
            if(onLongPressActions.size() == 0 && Settings.getAvLongClick() > 4) settings.setInt("av_long_press", 0);
        }
    }
}