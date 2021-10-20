package xyz.wingio.plugins.discovery.views;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;

import com.aliucord.Utils;
import com.aliucord.utils.DimenUtils;

import com.discord.utilities.color.ColorCompat;
import com.discord.utilities.icon.IconUtils;

import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textfield.TextInputEditText;

import com.lytefast.flexinput.R;

public class SearchEditText extends CardView {
    public TextInputLayout layout;

    public SearchEditText(Context ctx) {
        super(ctx);
        LinearLayout root = new LinearLayout(ctx);
        LayoutInflater.from(ctx).inflate(Utils.getResId("widget_change_guild_identity", "layout"), root);
        layout = (TextInputLayout) root.findViewById(Utils.getResId("set_nickname_text", "id"));
        ((CardView) layout.getParent()).removeView(layout);
        addView(layout);
        setCardBackgroundColor(Color.TRANSPARENT);
        getRoot().setHint("Enter Text");
        setRadius(DimenUtils.getDefaultCardRadius());
    }

    public TextInputLayout getRoot() {
        return layout;
    }

    public TextInputEditText getEditText() {
        return (TextInputEditText) ((ViewGroup) getRoot().getChildAt(0)).getChildAt(0);
    }

    public SearchEditText setHint(String hint) {
        getRoot().setHint(hint);
        return this;
    }

    public SearchEditText setHint(int hint) {
        getRoot().setHint(hint);
        return this;
    }

    public SearchEditText setThemedEndIcon(Drawable icon) {
        getRoot().setEndIconDrawable(Utils.tintToTheme(icon.mutate()));
        return this;
    }

    public SearchEditText setThemedEndIcon(int icon) {
        getRoot().setEndIconDrawable(Utils.tintToTheme(ContextCompat.getDrawable(getRoot().getContext(), icon)));
        return this;
    }
}