package com.aliucord.plugins.achievements;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.TextView;
import android.content.Context;

import androidx.core.content.res.ResourcesCompat;

import com.aliucord.Constants;
import com.aliucord.*;
import com.aliucord.api.SettingsAPI;
import com.aliucord.api.NotificationsAPI;
import com.aliucord.fragments.SettingsPage;
import com.aliucord.views.Divider;
import com.aliucord.entities.NotificationData;

import com.discord.views.CheckedSetting;
import com.discord.views.RadioManager;
import com.lytefast.flexinput.R$h;

import kotlin.Unit;
import java.util.Arrays;

@SuppressLint("SetTextI18n")
class Achievement {
    private static String name;
    private static String description;
    private static String id;

    public Achievement(Context ctx, String name, String description, String id) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.settings = PluginManager.plugins.get("Achievements").settings;
    }

    public static boolean isUnlocked() {
        return this.settings.getBool("ach_" + this.id, false);
    }

    public static void unlock() {
        if(this.isUnlocked()){
            NotificationData notD = new NotificationData();
            notD.setTitle("Achievement Unlocked");
            notD.setBody(Utils.renderMD("**" + this.name + "**: " + this.description));
            notD.setAutoDismissPeriodSecs(5);
            notD.setIconUrl("https://media.discordapp.net/attachments/656712865344126997/869987570090655784/76018874.png");
            notD.setOnClick(v -> {
                Utils.log("Achievement Unlocked: ach_" + this.id);
                return Unit.a;
            });
    
            NotificationsAPI.display(notD);
            this.settings.setBool("ach_" + this.id, true);
        }
    }
}