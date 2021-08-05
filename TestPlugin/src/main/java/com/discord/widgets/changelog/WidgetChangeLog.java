package com.discord.widgets.changelog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.VideoView;
import androidx.fragment.app.Fragment;
import com.discord.app.AppFragment;

/* compiled from: WidgetChangeLog.kt */
public final class WidgetChangeLog extends AppFragment {
    public static final Companion Companion = new Companion();
    
    /* compiled from: WidgetChangeLog.kt */
    public static final class Companion {
        private Companion() {
        }

        public final void launch(Context context, String str, String str2, String str3, String str4) {
            
        }
    }

    public WidgetChangeLog() {
        super();
    }

    /* private final void configureFooter() {
        getBinding().g.setOnClickListener(new WidgetChangeLog$configureFooter$1(this));
        getBinding().f1695c.setOnClickListener(new WidgetChangeLog$configureFooter$2(this));
        getBinding().d.setOnClickListener(new WidgetChangeLog$configureFooter$3(this));
    } */

    public static final void launch(Context context, String str, String str2, String str3, String str4) {
        Companion.launch(context, str, str2, str3, str4);
    }
}
