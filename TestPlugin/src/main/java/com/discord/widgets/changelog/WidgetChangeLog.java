package com.discord.widgets.changelog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.VideoView;
import androidx.fragment.app.Fragment;
import c.a.e.l;
import c.d.b.a.a;
import c.f.g.a.a.b;
import c.f.g.a.a.d;
import com.discord.app.AppFragment;
import com.discord.databinding.WidgetChangeLogBinding;
import com.discord.stores.StoreStream;
import com.discord.utilities.analytics.AnalyticsTracker;
import com.discord.utilities.time.ClockFactory;
import com.discord.utilities.view.extensions.ViewExtensions;
import com.discord.utilities.viewbinding.FragmentViewBindingDelegate;
import com.discord.utilities.viewbinding.FragmentViewBindingDelegateKt;
import com.facebook.drawee.controller.AbstractDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import d0.g0.t;
import d0.t.h0;
import d0.z.d.m;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.reflect.KProperty;
/* compiled from: WidgetChangeLog.kt */
public final class WidgetChangeLog extends AppFragment {
    public static final /* synthetic */ KProperty[] $$delegatedProperties = {a.V(WidgetChangeLog.class, "binding", "getBinding()Lcom/discord/databinding/WidgetChangeLogBinding;", 0)};
    public static final Companion Companion = new Companion(null);
    private static final String INTENT_EXTRA_BODY = "INTENT_EXTRA_BODY";
    private static final String INTENT_EXTRA_REVISION = "INTENT_EXTRA_REVISION";
    private static final String INTENT_EXTRA_VERSION = "INTENT_EXTRA_VERSION";
    private static final String INTENT_EXTRA_VIDEO = "INTENT_EXTRA_VIDEO";
    private final FragmentViewBindingDelegate binding$delegate = FragmentViewBindingDelegateKt.viewBinding(this, WidgetChangeLog$binding$2.INSTANCE, new WidgetChangeLog$binding$3(this));
    private int maxScrolledPercent;
    private long openedTimestamp;
    private final WidgetChangeLog$thumbnailControllerListener$1 thumbnailControllerListener = new WidgetChangeLog$thumbnailControllerListener$1(this);
    private AbstractDraweeController<Object, Object> thumbnailDraweeController;

    /* compiled from: WidgetChangeLog.kt */
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
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

    @Override // androidx.fragment.app.Fragment
    public void onDestroy() { }

    @Override // com.discord.app.AppFragment, androidx.fragment.app.Fragment
    public void onPause() { }

    @Override // com.discord.app.AppFragment
    public void onViewBound(View view) { }
}
