/*
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */

package com.aliucord.plugins.favoritemessages;

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
import android.text.style.ClickableSpan;
import android.view.*;
import android.widget.*;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import androidx.core.content.res.ResourcesCompat;

import com.aliucord.PluginManager;
import com.aliucord.Utils;
import com.aliucord.Logger;
import com.aliucord.CollectionUtils;
import com.aliucord.plugins.FavoriteMessages;
import com.aliucord.api.SettingsAPI;
import com.aliucord.entities.Plugin;
import com.aliucord.fragments.ConfirmDialog;
import com.aliucord.fragments.SettingsPage;
import com.aliucord.utils.ReflectUtils;
import com.aliucord.views.TextInput;
import com.aliucord.views.ToolbarButton;
import com.aliucord.widgets.PluginCard;
import com.aliucord.widgets.BottomSheet;
import com.discord.utilities.color.ColorCompat;
import com.discord.utilities.textprocessing.*;
import com.discord.app.AppBottomSheet;
import com.discord.app.AppFragment;
import com.discord.widgets.user.usersheet.WidgetUserSheet;
import com.discord.widgets.changelog.WidgetChangeLog;
import com.discord.models.message.Message;
import com.discord.stores.*;
import com.facebook.drawee.span.DraweeSpanStringBuilder;
import com.lytefast.flexinput.R;

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
        private MessageCard messageCard;
        private SettingsAPI sets = PluginManager.plugins.get("FavoriteMessages").settings;

        public MessageOptions(StoredMessage msg, MessageCard msgCard) {
            this.message = msg;
            this.messageCard = msgCard;
        }

        private StoredMessage getMessage() {
            return message;
        }

        @Override
        public void onViewCreated(View view, Bundle bundle) {
            super.onViewCreated(view, bundle);
            Context optCtx = requireContext();
            Drawable icon = FavoriteMessages.pluginIcon;
            icon.mutate();
            if (icon != null) icon.setTint(
                ColorCompat.getThemedColor(optCtx, R.b.colorInteractiveNormal)
            );
            
            var copyId = View.generateViewId();
            TextView copyOption = new TextView(optCtx, null, 0, R.h.UiKit_Settings_Item_Icon);
            copyOption.setText("Copy Text");
            copyOption.setId(copyId);
            copyOption.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
            copyOption.setOnClickListener(e -> {
                Utils.setClipboard("Message Content", getMessage().content);
                Utils.showToast(optCtx, "Copied message content");
                dismiss();
            });

            var unfavId = View.generateViewId();
            TextView unfavOption = new TextView(optCtx, null, 0, R.h.UiKit_Settings_Item_Icon);
            unfavOption.setText("Unfavorite");
            unfavOption.setId(unfavId);
            unfavOption.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
            unfavOption.setOnClickListener(e -> {
                Map<Long, StoredMessage> favorites = sets.getObject("favorites", new HashMap<>(), FavoriteMessages.msgType);
                favorites.remove(getMessage().id);
                sets.setObject("favorites", favorites);
                Utils.showToast(optCtx, "Unfavorited message");
                dismiss();
            });

            addView(copyOption);
            addView(unfavOption);
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
            Long meId = StoreStream.getUsers().getMe().getId();
            try {
                DraweeSpanStringBuilder cnt = DiscordParser.parseChannelMessage(ctx, msg.content, new MessageRenderContext(ctx, meId, true), new MessagePreprocessor(meId, null), DiscordParser.ParserOptions.DEFAULT, false);
                holder.card.contentView.setText(cnt);
            } catch (Throwable e) {
                Logger l = new Logger("FavoriteMessages");
                l.error("Error displaying message content", e);
            }
            Bitmap avatar = holder.card.getBitmapFromURL(String.format("https://cdn.discordapp.com/avatars/%s/%s.png", msg.author.id, msg.author.avatar));
            holder.card.avatarView.setImageBitmap(avatar);
            holder.card.authorView.setText(msg.author.name);
            
            holder.card.setOnLongClickListener(e -> {
                new MessageOptions(msg, holder.card).show(fragment.getParentFragmentManager(), "Message Options");
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
        input.setHint(context.getString(R.g.search));

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
