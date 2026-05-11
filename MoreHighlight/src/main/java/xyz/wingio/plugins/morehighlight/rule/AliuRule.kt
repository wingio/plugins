@file:Suppress("MISSING_DEPENDENCY_CLASS", "MISSING_DEPENDENCY_SUPERCLASS")

package xyz.wingio.plugins.morehighlight.rule

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.aliucord.PluginManager
import com.aliucord.Utils
import com.aliucord.Utils.openPageWithProxy
import com.aliucord.entities.Plugin
import com.aliucord.utils.ReflectUtils
import com.discord.simpleast.core.node.Node
import com.discord.simpleast.core.parser.ParseSpec
import com.discord.simpleast.core.parser.Parser
import com.discord.simpleast.core.parser.Rule
import com.discord.utilities.textprocessing.MessageParseState
import com.discord.utilities.textprocessing.MessageRenderContext
import xyz.wingio.plugins.morehighlight.node.ClickableNode
import xyz.wingio.plugins.morehighlight.node.TextNode
import java.util.regex.Matcher
import java.util.regex.Pattern

class AliuRule(
    private val context: Context
): Rule<MessageRenderContext, ClickableNode<MessageRenderContext?>, MessageParseState>(
    Pattern.compile("^ac://([A-Za-z0-9$]+)")
) {

    override fun parse(
        matcher: Matcher,
        parser: Parser<MessageRenderContext?, in ClickableNode<MessageRenderContext?>?, MessageParseState?>,
        s: MessageParseState?
    ): ParseSpec<MessageRenderContext?, MessageParseState?> {
        val pluginSettings = PluginManager.plugins[matcher.group(1)]?.settingsTab
            ?: return ParseSpec<MessageRenderContext?, MessageParseState?>(TextNode(matcher.group(0)!!), s)

        val node: Node<MessageRenderContext> = ClickableNode(matcher.group(1)!!, context, {
            when (pluginSettings.type) {
                Plugin.SettingsTab.Type.PAGE -> {
                    val page = if (pluginSettings.args != null) ReflectUtils.invokeConstructorWithArgs(pluginSettings.page, *pluginSettings.args) else pluginSettings.page.newInstance()
                    openPageWithProxy(context, page)
                }

                Plugin.SettingsTab.Type.BOTTOM_SHEET -> {
                    val sheet = if (pluginSettings.args != null) ReflectUtils.invokeConstructorWithArgs(pluginSettings.bottomSheet, *pluginSettings.args) else pluginSettings.bottomSheet.newInstance()
                    sheet.show(fragmentManager, pluginSettings::class.simpleName)
                }

                else -> {}
            }
        }, {})

        return ParseSpec(node, s)
    }

    companion object {
        private val fragmentManager: FragmentManager = Utils.appActivity.supportFragmentManager
    }

}