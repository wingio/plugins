package xyz.wingio.plugins

import android.annotation.SuppressLint
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.*;
import android.view.*;
import android.widget.*;
import android.os.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aliucord.annotations.AliucordPlugin
import com.aliucord.patcher.*
import com.aliucord.entities.Plugin
import com.aliucord.Http
import com.aliucord.Utils
import com.aliucord.api.GatewayAPI

import com.discord.utilities.analytics.AnalyticSuperProperties
import com.discord.widgets.settings.WidgetSettings;
import com.discord.gateway.*;

import org.json.JSONObject

import xyz.wingio.plugins.sessions.*

import com.lytefast.flexinput.R;

@AliucordPlugin
class Sessions : Plugin() {

    init {
        needsResources = true
    }

    data class AuthSession(
        val authSessionIdHash: String?
    )

    companion object {
        var sessionIdHash: String? = null
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun start(context: Context) {

        GatewayAPI.onEvent<AuthSession>("READY") {
            sessionIdHash = it.authSessionIdHash
        }

        GatewayAPI.onEvent<AuthSession>("AUTH_SESSION_CHANGE") {
            sessionIdHash = it.authSessionIdHash
        }

        patcher.after<WidgetSettings>("onViewBound", View::class.java) { (_, view: CoordinatorLayout) ->
            val layout = (view.getChildAt(1) as NestedScrollView).getChildAt(0) as LinearLayoutCompat
            val ctx = layout.context
            val baseIndex = layout.indexOfChild(layout.findViewById<TextView>(Utils.getResId("qr_scanner", "id")))
            TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Icon).apply {
                text = "Sessions"
                setCompoundDrawablesWithIntrinsicBounds(Utils.tintToTheme(ctx.getDrawable(R.e.ic_phonelink_24dp)), null, null, null)
                setOnClickListener {
                    if(sessionIdHash == null) return@setOnClickListener
                    Utils.openPageWithProxy(ctx, SessionsPage())
                }
                layout.addView(this, baseIndex + 1)
            }
        }
    }

    override fun stop(context: Context) {
        patcher.unpatchAll()
    }

}