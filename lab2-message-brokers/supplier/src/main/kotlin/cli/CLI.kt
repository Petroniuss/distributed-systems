package cli

import com.github.ajalt.mordant.markdown.Markdown
import com.github.ajalt.mordant.rendering.TextColors.blue
import com.github.ajalt.mordant.rendering.TextColors.yellow
import com.github.ajalt.mordant.rendering.TextColors.white
import com.github.ajalt.mordant.terminal.Terminal


object CLI {
    const val introMarkdownFilename = "/intro.md"
    val terminal = Terminal()
    val promptStyle = yellow
    val infoStyle = blue

    val actionEmoji = "\uD83D\uDCD5"
    val warningEmoji = "\uD83D\uDCD9"
    val okEmoji = "\uD83D\uDCD7"

    fun prompt(msg: String): String {
        terminal.print("$actionEmoji-$msg>", promptStyle);
        return getLine()
    }

    fun info(msg: String): Unit {
        terminal.print("$okEmoji-$msg", infoStyle)
    }

    fun displayIntro() {
        println(introMarkdownFilename)
        val file = this::class.java.getResource(introMarkdownFilename)
            .readText()

        val markdown = Markdown(file)
        terminal.println(markdown)
    }

    private fun getLine(): String {
        return readLine() ?: "";
    }
}


