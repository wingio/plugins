package xyz.wingio.plugins.discovery.api;

public class Category {
    public int id;
    public CategoryName name;
    public boolean is_primary;

    public static final int GAMING = 1;
    public static final int MUSIC = 2;
    public static final int ENTERTAINMENT = 3;
    public static final int ART = 4;
    public static final int SCIENCE = 5;
    public static final int EDUCATION = 6;

    public static class CategoryName {
        public String defaultName;
        public Localizations localizations;
    }

    public static class Localizations {
        public String de;
        public String fr;
        public String ru;
    }
}