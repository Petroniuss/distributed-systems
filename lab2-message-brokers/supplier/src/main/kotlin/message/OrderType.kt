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

    private fun matches(keyword: String): Boolean {
        return this.patterns.any { keyword.matches(it) }
    }

    companion object {
        fun match(keywords: List<String>): List<OrderType> {
            return keywords.mapNotNull { matches(it) }
        }

        private fun matches(keyword: String): OrderType? {
            return values().find { it.matches(keyword) }
        }
    }
}
