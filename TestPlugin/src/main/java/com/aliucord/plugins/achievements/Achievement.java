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
public class Achievement {
    private static String name;
    private static String description;
    private static String id;
    private static SettingsAPI sets;

    public Achievement(Context ctx, String name, String description, String id) {};

    public static boolean isUnlocked() {return false};

    public static void unlock() {};
}