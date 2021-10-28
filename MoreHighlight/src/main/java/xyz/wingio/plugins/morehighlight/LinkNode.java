package xyz.wingio.plugins.morehighlight;

import com.aliucord.Utils;

import android.content.Context;

import com.discord.simpleast.core.node.Node;
import com.discord.utilities.textprocessing.*;
import com.discord.utilities.color.ColorCompat;

import com.discord.utilities.spans.ClickableSpan;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

import com.lytefast.flexinput.R;

import kotlin.Unit;

public class LinkNode<MessageRenderContext> extends Node<MessageRenderContext> {
  String content;
  String url;
  Context context;

  public LinkNode(String content, String url, Context context){
    super();
    this.content = content;
    this.url = url;
    this.context = context;
  }

  @Override
  public void render(SpannableStringBuilder builder, MessageRenderContext renderContext) {
    Object clickableSpan = new ClickableSpan(Integer.valueOf(ColorCompat.getThemedColor(context, R.b.colorTextLink)), false, v -> {return Unit.a;}, v-> {Utils.launchUrl(url); return Unit.a;});
    int i = builder.length();
    builder.append(content);
    builder.setSpan(clickableSpan, i, builder.length(), 33);
  }
}