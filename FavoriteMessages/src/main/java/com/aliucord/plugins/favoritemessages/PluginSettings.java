/*
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */

package com.aliucord.plugins.favoritemessages;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.net.Uri;
import android.text.*;
import android.text.style.ClickableSpan;
import android.view.*;
import android.widget.*;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

import com.aliucord.*;
import com.aliucord.plugins.FavoriteMessages;
import com.aliucord.api.SettingsAPI;
import com.aliucord.entities.Plugin;
import com.aliucord.fragments.ConfirmDialog;
import com.aliucord.fragments.SettingsPage;
import com.aliucord.utils.ReflectUtils;
import com.aliucord.views.TextInput;
import com.aliucord.views.ToolbarButton;
import com.aliucord.widgets.PluginCard;
import com.discord.app.AppBottomSheet;
import com.discord.app.AppFragment;
import com.discord.widgets.user.usersheet.WidgetUserSheet;
import com.discord.widgets.changelog.WidgetChangeLog;
import com.discord.models.message.Message;
import com.lytefast.flexinput.R$d;
import com.lytefast.flexinput.R$g;

import kotlin.Unit;
import java.io.File;
import java.util.*;

public class PluginSettings extends SettingsPage {
    private static final int uniqueId = View.generateViewId();
    private final SettingsAPI settings;
    public PluginSettings(SettingsAPI settings) {
        this.settings = settings;
    }

    public static class MessageOptions extends BottomSheet {
        private StoredMessage message;

        public MessageOptions(StoredMessage msg) {
            this.message = msg
        }

        private StoredMessage getMessage() {
            return message;
        }

        @Override
        public void onViewCreated(View view, Bundle bundle) {
            super.onViewCreated(view, bundle);
            Context optCtx = requireContext();
            var resources = FavoriteMessages.resources;
            Drawable icon = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_editfriend", "drawable", "com.aliucord.plugins"), null );
            if (icon != null) icon.setTint(
            ColorCompat.getThemedColor(optCtx, R$b.colorInteractiveNormal)
            );
            
            var copyId = View.generateViewId();
            TextView copyOption = new TextView(ctx, null, 0, R$h.UiKit_Settings_Item_Icon);
            copyOption.setText("Copy Text");
            copyOption.setId(copyId);
            copyOption.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
            copyOption.setOnClickListener(e -> {
                Utils.setClipboard("Message Content", getMessage().content);
                Utils.showToast(optCtx, "Copied message content");
                dismiss();
            });

            addView(copyOption);
        }
    }

    public static class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> implements Filterable {
        public static final class ViewHolder extends RecyclerView.ViewHolder {
            private final Adapter adapter;
            public final MessageCard card;

            @SuppressLint("SetTextI18n")
            public ViewHolder(Adapter adapter, MessageCard card) {
                super(card);
                this.adapter = adapter;
                this.card = card;
            }
        }

        private final AppFragment fragment;
        private final Context ctx;
        private final List<StoredMessage> originalData;
        private List<StoredMessage> data;

        public Adapter(AppFragment fragment, Map<Long, StoredMessage> favorites) {
            super();

            this.fragment = fragment;
            ctx = fragment.requireContext();
            
            this.originalData = new ArrayList<StoredMessage>(favorites.values());
            originalData.sort(Comparator.comparing(p -> p.content));

            data = originalData;
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(this, new MessageCard(ctx));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            StoredMessage msg = data.get(position);
            holder.card.contentView.setText(Utils.renderMD(msg.content));
            Bundle bundle = new Bundle();
            bundle.putString("content", msg.content);
            holder.card.setOnLongClickListener(e -> {
                new MessageOptions(msg).show();
                return true;
            });
        }

        public void copyText(int position) {
            StoredMessage msg = data.get(position);
            Utils.setClipboard("Message Text", msg.content);
            Utils.showToast(ctx, "Copied message content");
        }

        private final Adapter _this = this;

        private final Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<StoredMessage> resultsList;
                if (constraint == null || constraint.equals(""))
                    resultsList = originalData;
                else {
                    String search = constraint.toString().toLowerCase().trim();
                    resultsList = CollectionUtils.filter(originalData, p -> {
                                String content = p.content;
                                if (content.toLowerCase().contains(search)) return true;
                                return false;
                            }
                    );
                }
                FilterResults results = new FilterResults();
                results.values = resultsList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                List<StoredMessage> res = (List<StoredMessage>) results.values;
                DiffUtil.calculateDiff(new DiffUtil.Callback() {
                    @Override
                    public int getOldListSize() {
                        return getItemCount();
                    }
                    @Override
                    public int getNewListSize() {
                        return res.size();
                    }
                    @Override
                    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                        return data.get(oldItemPosition).content.equals(res.get(newItemPosition).content);
                    }
                    @Override
                    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                        return true;
                    }
                }, false).dispatchUpdatesTo(_this);
                data = res;
            }
        };

        @Override
        public Filter getFilter() {
            return filter;
        }
    }
        

    @Override
    @SuppressLint("SetTextI18n")
    public void onViewBound(View view) {
        super.onViewBound(view);
        //noinspection ResultOfMethodCallIgnored
        setActionBarTitle("Favorite Messages");

        Context context = requireContext();
        int padding = Utils.getDefaultPadding();
        int p = padding / 2;

        TextInput input = new TextInput(context);
        input.setHint(context.getString(R$g.search));

        RecyclerView recyclerView = new RecyclerView(context);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        Map<Long, StoredMessage> favorites = settings.getObject("favorites", new HashMap<>(), FavoriteMessages.msgType);
        Adapter adapter = new Adapter(this, favorites);
        recyclerView.setAdapter(adapter);
        ShapeDrawable shape = new ShapeDrawable(new RectShape());
        shape.setTint(Color.TRANSPARENT);
        shape.setIntrinsicHeight(padding);
        DividerItemDecoration decoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        decoration.setDrawable(shape);
        recyclerView.addItemDecoration(decoration);
        recyclerView.setPadding(0, padding, 0, 0);

        addView(input);
        addView(recyclerView);

        EditText editText = input.getEditText();
        if (editText != null) {
            editText.setMaxLines(1);
            editText.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {
                    adapter.getFilter().filter(s);
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                public void onTextChanged(CharSequence s, int start, int before, int count) { }
            });
        }
    }

}
