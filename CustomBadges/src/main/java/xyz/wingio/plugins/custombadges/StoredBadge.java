package xyz.wingio.plugins.custombadges;

import com.google.gson.internal.LinkedTreeMap;
import java.util.*;

public class StoredBadge {
    public String toast;
    public String description;
    public String icon;

    public StoredBadge(String toast, String description, String icon) {
        this.toast = toast;
        this.description = description;
        this.icon = icon;
    }

    public String getToast() {
        return toast;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }

    public static StoredBadge copy(Object obj){
        if(obj instanceof StoredBadge){
            StoredBadge badge = (StoredBadge) obj;
            return badge;
        }
        if(obj instanceof LinkedTreeMap){
            Map<String, Object> map = (Map<String, Object>) obj;
            String newToast = (String) map.get("toast");
            String newDescription = (String) map.get("description");
            String newIcon = (String) map.get("icon");
            return new StoredBadge(newToast, newDescription, newIcon);
        }
        return null;
    }
}