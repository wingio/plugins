package com.discord.widgets.chat;

import com.discord.app.AppBottomSheet;
import com.discord.databinding.WidgetUrlActionsBinding;

public final class WidgetUrlActions extends AppBottomSheet {
    public WidgetUrlActions() {
        super(false);
    }

    private final WidgetUrlActionsBinding getBinding() {return new WidgetUrlActionsBinding();}

    private final String getUrl() {return "";}

    @Override 
    public int getContentViewResId() {return 0;}
}