package xyz.wingio.plugins;

import android.content.Context;
import android.webkit.*;
import android.view.*;

import com.aliucord.Logger;
import com.aliucord.Utils;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import com.aliucord.fragments.SettingsPage;

@SuppressWarnings("unused")
@AliucordPlugin
public class WebViewCord extends Plugin {

  public static Logger logger = new Logger("WebViewCord");

  public class NiceWebView extends WebView {

    public NiceWebView(Context context) {
      super(context);
      getSettings().setJavaScriptEnabled(true);
      getSettings().setDomStorageEnabled(true);
      setWebViewClient(new WebViewClient(){ @Override public boolean shouldOverrideUrlLoading(WebView view, String url){return false;}});
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
      requestDisallowInterceptTouchEvent(true);
      return super.onTouchEvent(event);
    }
  }

  public class WebViewPage extends SettingsPage {
    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void onViewBound(View view) {
        super.onViewBound(view);
        getHeaderBar().setVisibility(View.GONE);
        setPadding(0);
        NiceWebView wv = new NiceWebView(view.getContext());
        wv.loadUrl("http://discord.com/app");
        wv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        getLinearLayout().addView(wv);
    }
  }

  @Override
  public void start(Context context) throws Throwable {
    Utils.openPageWithProxy(Utils.appActivity, new WebViewPage());
  }

  @Override
  public void stop(Context context) {
      patcher.unpatchAll();
      commands.unregisterAll();
  }
}