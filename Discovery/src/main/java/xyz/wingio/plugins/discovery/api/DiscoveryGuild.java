package xyz.wingio.plugins.discovery.api;

import java.util.*;

public class DiscoveryGuild {
    public Long id;
    public String name;
    public String description;
    public String icon;
    public String splash;
    public String banner;
    public List<String> features;
    public Long approximate_presence_count;
    public Long approximate_member_count;
    public Long premium_subscription_count;
    public String preferred_locale;
    public boolean auto_removed;
    public String discovery_splash;
    public List<Emoji> emojis;
    public int emoji_count;
    public List<Sticker> stickers;
    public int sticker_count;
    public int primary_category_id;
    public Category primary_category;
    public List<Category> categories;
    public List<String> keywords;
    public String vanity_url_code;

    public static class Emoji {
        public String name;
        public Long id;
        public boolean require_colons;
        public boolean managed;
        public boolean animated;
        public boolean available;
    }

    public static class Sticker {
        public String name;
        public Long id;
        public boolean available;
        public Long guild_id;
        public int sort_value;
        public int format_type;
        public String description;
        public String tags;
    }  
}