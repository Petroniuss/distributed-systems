package message

enum class OrderType(strPatterns: List<String>) {
    OXYGEN(
        listOf("oxygen", "tlen")),

    CLIMBING_BOOTS(
        listOf("buty", "climbing boots", "boots", "shoes")),

    BACKPACK(
        listOf("backpack", "plecak"));

    private val patterns =
        strPatterns.map { it.toRegex(RegexOption.IGNORE_CASE) }

    val queueName: String
        get() {
            return this.toString()
        }

    val routingKey: String
        get() {
            return "$prefix.${this.toString().toLowerCase()}"
        }

    private fun matches(keyword: String): Boolean {
        return this.patterns.any { keyword.matches(it) }
    }

    companion object {
        const val exchange = "ORDER_EXCHANGE"
        const val prefix = "order"

        fun match(keywords: List<String>): List<OrderType> {
            return keywords.mapNotNull { matches(it) }
        }

        private fun matches(keyword: String): OrderType? {
            return values().find { it.matches(keyword) }
        }
    }
}
