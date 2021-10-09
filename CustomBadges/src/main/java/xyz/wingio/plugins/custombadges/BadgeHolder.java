package xyz.wingio.plugins.custombadges;

import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aliucord.Utils;
import com.aliucord.utils.*;
import com.facebook.drawee.view.SimpleDraweeView;

public class BadgeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final BadgeAdapter adapter;
    public final ImageView icon;

    public BadgeHolder(BadgeAdapter adapter, ImageView image) {
        super(image);
        this.adapter = adapter;
        this.icon = image;
    }

    @Override public void onClick(View view) {
        adapter.onClick(view.getContext(), getAdapterPosition());
    }
}