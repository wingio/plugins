package com.aliucord.plugins;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.aliucord.api.CommandsAPI;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import java.util.*;

@AliucordPlugin
@SuppressWarnings("unused")
public class DN extends Plugin {

    @Override
    public void start(Context context) {
        commands.registerCommand(
            "deez",
            "Deez Nuts >:)",
            Collections.emptyList(),
            args -> {
                return new CommandsAPI.CommandResult("Deez Nuts");
            }
        );

    }

    @Override
    public void stop(Context context) {
        commands.unregisterAll();
    }

}
