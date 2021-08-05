/*
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */

package com.aliucord.entities;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;

import androidx.annotation.NonNull;

import com.aliucord.api.CommandsAPI;
import com.aliucord.api.PatcherAPI;
import com.aliucord.api.SettingsAPI;
import com.discord.app.AppBottomSheet;
import com.discord.app.AppFragment;

/** Base Plugin class all plugins must extend */
@SuppressWarnings("unused")
public abstract class Plugin {
    /** Plugin Manifest */
    public static class Manifest {
        /** Plugin Author */
        public static class Author {
            /** The name of the plugin author */
            public String name;
            /** The id of the plugin author */
            public long id;

            /**
             * Constructs an Author with the specified name and an ID of 0
             * @param name The name of the author
             */
            public Author(String name) {
                this(name, 0);
            }
            /**
             * Constructs an Author with the specified name and ID
             * @param name The name of the author
             * @param id The id of the author
             */
            public Author(String name, long id) {
                this.name = name;
                this.id = id;
            }

            @NonNull
            @Override
            public String toString() { return name; }
        }

        /** The authors of this plugin */
        public Author[] authors = new Author[]{};
        /** A short description of this plugin */
        public String description = "";
        /** The current version of this plugin */
        public String version = "1.0.0";
        // TODO: public String discord;
        /** The updater JSON url */
        public String updateUrl;
        /** Changelog featuring recent updates, written in markdown */
        public String changelog;
        /** Image or video link that will be displayed at the top of the changelog */
        public String changelogMedia = "https://cdn.discordapp.com/banners/169256939211980800/eda024c8f40a45c88265a176f0926bea.jpg?size=2048";
    }

    /** Method returning the {@link Manifest} of your Plugin */
    @NonNull
    public abstract Manifest getManifest();
}
