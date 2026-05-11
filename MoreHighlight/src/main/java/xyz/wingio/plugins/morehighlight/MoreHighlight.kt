package xyz.wingio.plugins.morehighlight

import android.content.Context

import com.aliucord.Logger
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.*
import com.aliucord.utils.lazyField

import com.discord.simpleast.core.node.Node
import com.discord.simpleast.core.parser.Parser
import com.discord.simpleast.core.parser.Rule
import com.discord.utilities.textprocessing.AstRenderer
import com.discord.utilities.textprocessing.DiscordParser
import com.discord.utilities.textprocessing.DiscordParser.ParserOptions
import com.discord.utilities.textprocessing.MessageParseState
import com.discord.utilities.textprocessing.MessagePreprocessor
import com.discord.utilities.textprocessing.MessageRenderContext
import com.discord.utilities.textprocessing.node.BasicRenderContext
import com.discord.utilities.textprocessing.node.EditedMessageNode
import com.discord.utilities.textprocessing.node.ZeroSpaceWidthNode
import com.facebook.drawee.span.DraweeSpanStringBuilder

import xyz.wingio.plugins.morehighlight.rule.AliuRule
import xyz.wingio.plugins.morehighlight.rule.BulletPointRule
import xyz.wingio.plugins.morehighlight.rule.ColorRule
import xyz.wingio.plugins.morehighlight.rule.HeaderRule
import xyz.wingio.plugins.morehighlight.rule.IssueRule
import xyz.wingio.plugins.morehighlight.rule.RedditRule
import xyz.wingio.plugins.morehighlight.rule.RepoRule
import xyz.wingio.plugins.morehighlight.rule.SlashCommandRule
import xyz.wingio.plugins.morehighlight.rule.SubtextRule

import java.util.regex.Pattern

@AliucordPlugin
class MoreHighlight: Plugin() {

    private val rulesField by lazyField<Parser<*, *, *>>("rules")

    init {
        settingsTab = SettingsTab(PluginSettings::class.java).withArgs(this)
    }

    @Suppress("UNCHECKED_CAST")
    override fun start(context: Context) {
        patcher.instead<DiscordParser?>("parseChannelMessage", Context::class.java, String::class.java, MessageRenderContext::class.java, MessagePreprocessor::class.java, ParserOptions::class.java, Boolean::class.javaPrimitiveType!!) { (_, ctx: Context, content: String?, renderContext: MessageRenderContext, messagePreprocessor: MessagePreprocessor, _: ParserOptions, isEdited: Boolean) ->
            val parser=
                DiscordParser.createParser(true, true, true, false, false) as Parser<MessageRenderContext, Node<MessageRenderContext>, MessageParseState>
            val rules =
                rulesField[parser] as ArrayList<Rule<MessageRenderContext, out Node<MessageRenderContext>, MessageParseState>>

            rules.addAll(0, listOf(
                SlashCommandRule(),
                ColorRule(),
                AliuRule(ctx),
                IssueRule(ctx),
                RepoRule(ctx),
                RedditRule(ctx),
                HeaderRule(),
                SubtextRule(ctx),
                BulletPointRule(ctx)
            ))

            rulesField[parser] = rules

            val parsed = Parser.`parse$default`(parser, content ?: "", MessageParseState.`access$getInitialState$cp`(), null, 4, null) as MutableList<Node<BasicRenderContext>>
            messagePreprocessor.process(parsed)

            if (isEdited) parsed.add(EditedMessageNode(ctx))
            parsed.add(ZeroSpaceWidthNode())

            AstRenderer.render(parsed, renderContext)
        }
    }

    override fun stop(context: Context) = patcher.unpatchAll()

    companion object {
        const val PREF_SHOW_REPO_NAME = "show_repo_name"
        const val PREF_HEADER_SIZE = "header_size_scale"
    }

}