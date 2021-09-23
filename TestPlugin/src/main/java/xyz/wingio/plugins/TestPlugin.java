package xyz.wingio.plugins;

import com.google.android.material.chip.ChipGroup;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.*;
import android.view.*;
import android.widget.*;
import android.os.*;

import androidx.annotation.NonNull;
import androidx.annotation.DrawableRes;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

import com.aliucord.Constants;
import com.aliucord.Utils;
import com.aliucord.Logger;
import com.aliucord.PluginManager;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.PinePatchFn;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.wrappers.*;
import com.aliucord.utils.ReflectUtils;
import xyz.wingio.plugins.testplugin.*;

import com.discord.api.channel.Channel;
import com.discord.databinding.WidgetChannelsListItemChannelVoiceBinding;
import com.discord.models.guild.Guild;
import com.discord.stores.*;
import com.discord.widgets.channels.list.WidgetChannelsListAdapter;
import com.discord.widgets.channels.list.items.*;

import com.lytefast.flexinput.R;

import java.util.*;
import java.lang.reflect.*;
import java.lang.*;

@AliucordPlugin
public class TestPlugin extends Plugin {

  public TestPlugin() {
    settingsTab = new SettingsTab(PluginSettings.class).withArgs(settings);
    needsResources = true;
  }
  
  public Logger logger = new Logger("TestPlugin");

  @NonNull
  @Override
  public Manifest getManifest() {
    var manifest = new Manifest();
    manifest.authors =
    new Manifest.Author[] {
    new Manifest.Author("Wing", 298295889720770563L),
    };
    manifest.description = "Used for testing: permission viewer";
    manifest.version = "1.1.0";
    manifest.updateUrl =
    "https://raw.githubusercontent.com/wingio/plugins/builds/updater.json";
    manifest.changelog = "New Features {updated marginTop}\n======================\n\n* **Rebranded!** We are now XintoCord";
    return manifest;
  }

  @Override
  public void start(Context context) throws Throwable {

  }

  @Override
  public void stop(Context context) { patcher.unpatchAll(); }
}

