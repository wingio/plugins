package com.discord.widgets.chat;

import com.discord.app.AppBottomSheet;

public final class WidgetUrlActions extends AppBottomSheet {
    public WidgetUrlActions() {
        super(false, 1, null);
    }

    private final String getUrl() {return "";}

    @Override 
    public int getContentViewResId() {return 0;}
}