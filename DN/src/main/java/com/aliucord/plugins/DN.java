package com.aliucord.plugins;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.aliucord.api.CommandsAPI;
import com.aliucord.entities.Plugin;
import java.util.*;

@SuppressWarnings("unused")
public class DN extends Plugin {

    @NonNull
    @Override
    public Manifest getManifest() {
        Manifest manifest = new Manifest();
        manifest.authors = new Manifest.Author[]{ new Manifest.Author("Wing", 298295889720770563L) };
        manifest.description = "Deez nuts";
        manifest.version = "1.1";
        manifest.updateUrl = "https://raw.githubusercontent.com/wingio/plugins/builds/updater.json";
        return manifest;
    }

    @Override
    public void start(Context context) {
        commands.registerCommand(
            "deez",
            "Deez Nuts >:)",
            args -> new CommandsAPI.CommandResult("Deez Nuts")
        )

    }

    @Override
    public void stop(Context context) {
        commands.unregisterAll();
    }

}