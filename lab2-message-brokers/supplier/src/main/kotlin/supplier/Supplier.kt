package supplier

import cli.CLI
import order.OrderType

class Supplier() {
    private val orderTypes: List<OrderType>

    init {
        CLI.displayIntro()
        val keywords = ArrayList<String>();

        do {
            val keyword = CLI.prompt("Enter keyword:")
            keywords.add(keyword)
        } while (keyword.isNotBlank())

        orderTypes = OrderType.match(keywords)
        CLI.info("Matched: ${orderTypes.toString()}")
    }
}


