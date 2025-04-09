package xyz.wingio.plugins.morehighlight;

import com.discord.simpleast.core.node.Node;
import com.discord.utilities.textprocessing.*;

import android.text.SpannableStringBuilder;
import android.text.style.*;

public class BulletPointNode<MessageRenderContext> extends Node<MessageRenderContext> {
  String content;

  public BulletPointNode(String content){
    super();
    this.content = content;
  }

  @Override
  public void render(SpannableStringBuilder builder, MessageRenderContext renderContext) {
    int length = builder.length();
    builder.append("• " + content);
    
    builder.setSpan(new StyleSpan(android.graphics.Typeface.NORMAL), length, builder.length(), 
        SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
  }
}