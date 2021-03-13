package cli

import com.github.ajalt.mordant.markdown.Markdown
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextColors.*
import com.github.ajalt.mordant.terminal.Terminal


object CLI {
    const val introMarkdownFilename = "/intro.md"
    val terminal = Terminal()

    val promptStyle = yellow
    val infoStyle = blue
    val successStyle = green
    val warningStyle = red

    val actionEmoji = "\uD83D\uDCD5"
    val warningEmoji = "\uD83D\uDCD9"
    val okEmoji = "\uD83D\uDCD7"
    val coolEmoji = "\uD83D\uDE0E"

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


