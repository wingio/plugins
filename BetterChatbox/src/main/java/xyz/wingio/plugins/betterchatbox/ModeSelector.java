package xyz.wingio.plugins.betterchatbox;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import android.text.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aliucord.Utils;
import com.aliucord.utils.DimenUtils;

import com.discord.app.AppDialog;

import com.lytefast.flexinput.R;
import kotlin.jvm.functions.Function1;
import kotlin.Unit;

public class ModeSelector extends AppDialog {
    public static final String RESULT = "RESULT";

    public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
        private final String[] modes;

        public Adapter(String[] modes) {
            this.modes = modes;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView text = new TextView(parent.getContext(), null, 0, R.i.UiKit_Settings_Item_Icon);
            text.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new ViewHolder(text);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bind(modes[position]);
        }

        @Override
        public int getItemCount() {
            return modes.length;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView text;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                text = (TextView) itemView;
            }

            public void bind(String mode) {
                text.setText(mode);
                text.setOnClickListener(v -> onItemPicked(mode));
            }
        }
    } 

    private String title;
    private String[] modes;
    private Function1<String, Unit> listener;

    public ModeSelector(String title) {
        this(title, new String[] {"None", "Open Profile Sheet", "Change Status", "Add Attachment"});
    }

    public ModeSelector(String title, String[] modes) {
        super(Utils.getResId("widget_settings_language_select", "layout"));
        this.title = title;
        this.modes = modes;
    }

    @Override
    public void onViewBound(View view) {
        super.onViewBound(view);
        int p = DimenUtils.dpToPx(16);
        RecyclerView rv = (RecyclerView) view.findViewById(Utils.getResId("settings_language_select_list", "id"));
        rv.setAdapter(new Adapter(modes));
        ((ViewGroup) rv.getParent()).removeViewAt(0);
        TextView titleTv = new TextView(view.getContext(), null, 0, R.i.UiKit_Sheet_Header_Title);
        titleTv.setText(title);
        titleTv.setGravity(Gravity.CENTER_HORIZONTAL);
        titleTv.setPadding(p, p, p, p);
        titleTv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ((ViewGroup) rv.getParent()).addView(titleTv, 0);

    }

    public void onItemPicked(String mode) {
        if(listener != null) listener.invoke(mode);
        dismiss();
    }

    public void setOnResultListener(Function1<String, Unit> listener) {
        this.listener = listener;
    }

    public Function1<String, Unit> getOnResultListener() {
        return listener;
    }

}