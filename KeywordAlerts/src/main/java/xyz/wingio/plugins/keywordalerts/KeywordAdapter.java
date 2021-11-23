package xyz.wingio.plugins.keywordalerts;

import xyz.wingio.plugins.KeywordAlerts;

import android.content.Context;
import android.view.*;
import android.widget.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.util.Base64;
import android.app.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.DimenRes;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.ContextCompat;

import com.aliucord.PluginManager;
import com.aliucord.api.*;
import com.aliucord.Utils;
import com.aliucord.utils.*;
import com.aliucord.Http;
import com.aliucord.Logger;
import com.aliucord.fragments.SettingsPage;
import com.aliucord.fragments.InputDialog;

import com.lytefast.flexinput.R;

import kotlin.jvm.functions.Function1;
import java.util.*;

import java.io.*;

public class KeywordAdapter extends RecyclerView.Adapter<KeywordAdapter.KeywordHolder> {

    public class KeywordHolder extends RecyclerView.ViewHolder {
        private final KeywordAdapter adapter;
        public final View item;

        public KeywordHolder(KeywordAdapter adapter, View item) {
            super(item);
            this.adapter = adapter;
            this.item = item;
        }
    }

    private final Context ctx;
    private final List<Keyword> keywords;
    private final SettingsPage page;
    private final SettingsAPI settings = PluginManager.plugins.get("KeywordAlerts").settings;

    public KeywordAdapter(SettingsPage page, List<Keyword> keywords) {
        this.keywords = keywords;
        this.page = page;
        ctx = page.getContext();
    }

    @Override
    public int getItemCount() {
        return keywords.size();
    }

    @NonNull
    @Override
    public KeywordHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new KeywordHolder(this, new Card(ctx));
    }

    @Override
    public void onBindViewHolder(@NonNull KeywordHolder holder, int position) {
        Keyword keyword = keywords.get(position);
        Card card = (Card) holder.item;

        card.word.k.a().setText(keyword.getWord());
        card.word.setChecked(keyword.isEnabled());
        card.word.setOnCheckedListener(checked -> {
            Map<Long, Keyword> keywordMap = settings.getObject("keywords", new HashMap<>(), KeywordAlerts.keywordsType);
            keyword.setEnabled(checked);
            keywordMap.put(keyword.getId(), keyword);
            settings.setObject("keywords", keywordMap);
        });

        card.regex.setChecked(keyword.isRegex());
        card.regex.setOnCheckedListener(checked -> {
            Map<Long, Keyword> keywordMap = settings.getObject("keywords", new HashMap<>(), KeywordAlerts.keywordsType);
            keyword.setRegex(checked);
            keywordMap.put(keyword.getId(), keyword);
            settings.setObject("keywords", keywordMap);
        });

        card.whitelist.setChecked(keyword.whitelistEnabled());
        card.channels.setVisibility(keyword.whitelistEnabled() ? View.VISIBLE : View.GONE);
        card.whitelist.setOnCheckedListener(checked -> {
            Map<Long, Keyword> keywordMap = settings.getObject("keywords", new HashMap<>(), KeywordAlerts.keywordsType);
            keyword.setWhitelistEnabled(checked);
            keywordMap.put(keyword.getId(), keyword);
            card.channels.setVisibility(checked ? View.VISIBLE : View.GONE);
            settings.setObject("keywords", keywordMap);
        });


        card.delete.setOnClickListener(v -> {
            Map<Long, Keyword> keywordMap = settings.getObject("keywords", new HashMap<>(), KeywordAlerts.keywordsType);
            keywordMap.remove(keyword.getId());
            settings.setObject("keywords", keywordMap);
            keywords.remove(keyword);
            notifyItemRemoved(position);
        });

        card.edit.setOnClickListener(v -> {
            InputDialog dialog = new InputDialog()
                .setTitle("Edit Keyword")
                .setPlaceholderText("Word or Regex")
                .setDescription("Supports Regex");
            dialog.setOnDialogShownListener(w -> {
                dialog.getInputLayout().getEditText().setText(keyword.getWord());
            });
            dialog.setOnOkListener(w -> {
                if(dialog.getInput().isEmpty()) {
                    Toast.makeText(ctx, "Keyword cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                Map<Long, Keyword> keywords = settings.getObject("keywords", new HashMap<>(), KeywordAlerts.keywordsType);
                keyword.setWord(dialog.getInput());
                keywords.put(keyword.getId(), keyword);
                settings.setObject("keywords", keywords);
                notifyItemChanged(position);
                dialog.dismiss();
            });
            dialog.show(page.getFragmentManager(), this.getClass().getSimpleName());
        });

        card.channels.setOnClickListener(v -> {
            Utils.openPageWithProxy(ctx, new ChannelPage(((PluginSettings) page).plugin, keyword));
        });
    }

    public void add(Keyword keyword) {
        keywords.add(keyword);
        notifyItemInserted(keywords.size() - 1);
    }
}
