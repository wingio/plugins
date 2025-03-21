package xyz.wingio.plugins.morehighlight;

import com.discord.simpleast.core.node.Node;
import com.discord.utilities.textprocessing.*;

import android.text.SpannableStringBuilder;
import android.text.style.*;

public class HeaderNode<MessageRenderContext> extends Node<MessageRenderContext> {
  String content;
  int level;

  public HeaderNode(String content, int level){
    super();
    this.content = content;
    this.level = level;
  }

  @Override
  public void render(SpannableStringBuilder builder, MessageRenderContext renderContext) {
    int length = builder.length();
    builder.append(content);
    
    // Apply styling based on header level
    float sizeProportion = 1.0f;
    switch(level) {
      case 1: // h1
        sizeProportion = 2.0f;
        break;
      case 2: // h2
        sizeProportion = 1.5f;
        break;
      case 3: // h3
        sizeProportion = 1.2f;
        break;
      default:
        sizeProportion = 1.0f;
    }
    
    // Apply relative text size
    builder.setSpan(new RelativeSizeSpan(sizeProportion), length, builder.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
    
    // Apply bold styling
    builder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), length, builder.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
  }
}
