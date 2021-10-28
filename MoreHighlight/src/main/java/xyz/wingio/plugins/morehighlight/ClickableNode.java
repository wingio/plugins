package xyz.wingio.plugins.morehighlight;

import com.aliucord.Utils;

import android.content.Context;
import android.view.View;

import com.discord.simpleast.core.node.Node;
import com.discord.utilities.textprocessing.*;
import com.discord.utilities.color.ColorCompat;

import com.discord.utilities.spans.ClickableSpan;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

import com.lytefast.flexinput.R;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class ClickableNode<MessageRenderContext> extends Node<MessageRenderContext> {
  String content;
  String url;
  Context context;
  Function1<View, Unit> onClick;
  Function1<View, Unit> onLongClick;

  public ClickableNode(String content, String url, Context context){
    this(content, url, context, v -> {return Unit.a;}, v -> {return Unit.a;});
  }

  public ClickableNode(String content, String url, Context context, Function1<View, Unit> onClick){
    this(content, url, context, onClick, v -> {return Unit.a;});
  }

  public ClickableNode(String content, String url, Context context, Function1<View, Unit> onClick, Function1<View, Unit> onLongClick){
    super();
    this.content = content;
    this.url = url;
    this.context = context;
    this.onClick = onClick;
    this.onLongClick = onLongClick;
  }

  public void setOnClickListener(Function1<View, Unit> onClick){
    this.onClick = onClick;
  }

  public void setOnLongClickListener(Function1<View, Unit> onLongClick){
    this.onLongClick = onLongClick;
  }

  @Override
  public void render(SpannableStringBuilder builder, MessageRenderContext renderContext) {
    Object clickableSpan = new ClickableSpan(Integer.valueOf(ColorCompat.getThemedColor(context, R.b.colorTextLink)), false, onLongClick, onClick);
    int i = builder.length();
    builder.append(content);
    builder.setSpan(clickableSpan, i, builder.length(), 33);
  }
}