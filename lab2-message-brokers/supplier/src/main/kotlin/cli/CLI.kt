package cli

import com.github.ajalt.mordant.markdown.Markdown
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextColors.*
import com.github.ajalt.mordant.terminal.Terminal


object CLI {
    private const val introMarkdownFilename = "/intro.md"
    private val terminal = Terminal()

    private val promptStyle = yellow
    private val infoStyle = blue
    private val successStyle = green
    private val warningStyle = red

    private const val actionEmoji = "\uD83D\uDCD5"
    private const val warningEmoji = "\uD83D\uDCD9"
    private const val okEmoji = "\uD83D\uDCD7"
    private const val coolEmoji = "\uD83D\uDE0E"

    fun prompt(msg: String): String {
        terminal.print("$actionEmoji-$msg>", promptStyle);
        return getLine()
    }

    fun info(msg: String): Unit {
        terminal.println("$okEmoji-$msg", infoStyle)
    }

    fun success(msg: String): Unit {
        terminal.println("$coolEmoji $msg", successStyle)
    }

    fun warning(msg: String) {
        terminal.println("$warningEmoji $msg", warningStyle)
    }

    fun displayIntro() {
        val file = this::class.java.getResource(introMarkdownFilename)
            .readText()

        val markdown = Markdown(file)
        terminal.println(markdown)
    }

    private fun getLine(): String {
        return readLine() ?: "";
    }

}


