package xyz.wingio.plugins.discovery.api;

public class Category {
    public int id;
    public CategoryName name;
    public boolean is_primary;

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