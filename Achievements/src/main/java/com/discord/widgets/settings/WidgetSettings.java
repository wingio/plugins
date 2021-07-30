package com.discord.widgets.settings;

import com.discord.app.AppFragment;
import com.discord.databinding.WidgetSettingsBinding;

public class WidgetSettings extends AppFragment {
    public static final class Model { }

    private WidgetSettingsBinding getBinding() { return new WidgetSettingsBinding(); }

    public configureUI(Model model) {}
}