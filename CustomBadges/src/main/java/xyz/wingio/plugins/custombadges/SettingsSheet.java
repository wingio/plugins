package xyz.wingio.plugins.custombadges;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.content.res.Resources;
import android.content.res.AssetManager;
import android.net.Uri;
import android.text.*;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Base64;
import android.view.*;
import android.widget.*;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import androidx.core.content.res.ResourcesCompat;

import xyz.wingio.plugins.CustomBadges;
import com.aliucord.Utils;
import com.aliucord.utils.*;
import com.aliucord.Logger;
import com.aliucord.PluginManager;
import com.aliucord.api.SettingsAPI;
import com.aliucord.fragments.SettingsPage;
import com.aliucord.utils.ReflectUtils;
import com.aliucord.widgets.BottomSheet;
import com.discord.widgets.user.Badge;
import com.aliucord.views.TextInput;
import com.aliucord.views.Button;
import com.discord.app.AppBottomSheet;
import com.discord.app.AppFragment;
import com.discord.views.CheckedSetting;
import com.discord.utilities.color.ColorCompat;
import com.discord.stores.StoreStream;
import com.discord.widgets.tabs.NavigationTab;
import com.lytefast.flexinput.R;

import kotlin.Unit;
import java.io.*;
import java.util.*;

public class SettingsSheet extends BottomSheet {
    private EditUser page;
    private SettingsAPI settings;
    private Long userId;
    private StoredBadge currBadge;
    private int index;

    public SettingsSheet(EditUser page, SettingsAPI settings, Long userId) {
        this.page = page;
        this.settings = settings;
        this.userId = userId;
    }

    public SettingsSheet(EditUser page, SettingsAPI settings, Long userId, StoredBadge currBadge, int index) {
        this.page = page;
        this.settings = settings;
        this.userId = userId;
        this.currBadge = currBadge;
        this.index = index;
    }

    @Override
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        int p = DimenUtils.dpToPx(16);
        
        setPadding(p);
        Context ctx = requireContext();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, p/2);


        LinearLayout iconLayout = new LinearLayout(ctx);
        iconLayout.setOrientation(LinearLayout.HORIZONTAL);
        iconLayout.setGravity(Gravity.CENTER);
        iconLayout.setLayoutParams(params);

        TextInput iconName = new TextInput(ctx);
        iconName.setHint("Drawable Name (ex. ic_verified_badge)");
        LinearLayout.LayoutParams iconLParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        iconName.setLayoutParams(iconLParams);
        if(currBadge != null) { iconName.getEditText().setText(currBadge.getIcon()); }

        TextInput toastText = new TextInput(ctx);
        toastText.setHint("Toast (Will be shown when tapping on the badge)");
        toastText.setLayoutParams(params);
        if(currBadge != null) toastText.getEditText().setText(currBadge.getToast());


        var resources = ctx.getResources();
        Drawable badge = ResourcesCompat.getDrawable(resources, resources.getIdentifier(currBadge == null ? "ic_verified_badge" : currBadge.getIcon(), "drawable", "com.discord"), null);
        if(currBadge == null) badge.setTint(ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal));

        var icon = new ImageView(ctx);
        icon.setImageDrawable(badge);
        var iconParams = new LinearLayout.LayoutParams(DimenUtils.dpToPx(32), DimenUtils.dpToPx(32));
        iconParams.setMargins(p / 2, 0, p, 0);
        icon.setLayoutParams(iconParams);
        icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        
        Button add = new Button(ctx);
        add.setText(currBadge != null ? "Save" : "Add");


        iconLayout.addView(icon);
        iconLayout.addView(iconName);

        add.setOnClickListener(v -> {
            String name = iconName.getEditText().getText().toString();
            String toast = toastText.getEditText().getText().toString();

            if (name.isEmpty()) {
                Toast.makeText(ctx, "Please enter a drawable name", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    Drawable d = ResourcesCompat.getDrawable(resources, resources.getIdentifier(name, "drawable", "com.discord"), null);
                    if (d == null) {
                        Toast.makeText(ctx, "Drawable not found", Toast.LENGTH_SHORT).show();
                    }
                    StoredBadge badge1 = new StoredBadge(toast.isEmpty() ? "Custom Badge" : toast, "CustomBadges Badge", name);
                    Map<Long, List<StoredBadge>> userBadges = settings.getObject("userBadges", new HashMap<>(), CustomBadges.badgeStoreType);
                    List<StoredBadge> badges = userBadges.get(userId);
                    if (badges == null) {
                        badges = new ArrayList<>();
                    }
                    if(currBadge == null) {
                        badges.add(badge1);
                    } else {
                        badges.set(index, badge1);
                    }
                    userBadges.put(userId, badges);
                    settings.setObject("userBadges", userBadges);
                    Toast.makeText(ctx, currBadge == null ? "Added badge" : "Edited badge", Toast.LENGTH_SHORT).show();
                    dismiss();
                    page.reRender();
                } catch (Exception e) {
                    Logger logger = new Logger("CustomBadges");
                    logger.error("Error setting badge", e);
                    Toast.makeText(ctx, "Drawable not found", Toast.LENGTH_SHORT).show();
                }
            }
        });

        EditText editText = iconName.getEditText();
        if (editText != null) {
            editText.setMaxLines(1);
            editText.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {
                    String iconId = String.valueOf(s);
                    try {
                        Drawable newBadge = ResourcesCompat.getDrawable(resources, resources.getIdentifier(iconId, "drawable", "com.discord"), null);
                        icon.setImageDrawable(newBadge);
                    } catch (Throwable error) {
                        icon.setImageDrawable(badge);
                    }
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                public void onTextChanged(CharSequence s, int start, int before, int count) { }
            });
        }

        TextView instructions = new TextView(ctx);
        SpannableStringBuilder builder = new SpannableStringBuilder("Icon Guide");
        builder.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                StoreStream.Companion.getMessagesLoader().jumpToMessage(847566769258233926L, 890729756444725268L);
                getActivity().onBackPressed();
                page.getActivity().onBackPressed();
                page.fragment.getActivity().onBackPressed();
                StoreStream.Companion.getTabsNavigation().selectTab(NavigationTab.HOME, true);
            }
        }, 0, "Icon Guide".length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        instructions.setMovementMethod(LinkMovementMethod.getInstance());
        instructions.setText(builder);
        instructions.setGravity(Gravity.CENTER);
        instructions.setLayoutParams(params);

        addView(iconLayout);
        addView(toastText);
        addView(instructions);
        addView(add);
    }

    private CheckedSetting createSwitch(Context context, SettingsAPI sets, String key, String label, CharSequence subtext, boolean defaultValue, boolean reRender) {
        CheckedSetting cs = Utils.createCheckedSetting(context, CheckedSetting.ViewType.SWITCH, label, subtext);
        cs.setChecked(sets.getBool(key, defaultValue));
        cs.setOnCheckedListener(c -> {
            sets.setBool(key, c);
        });
        return cs;
    }
}