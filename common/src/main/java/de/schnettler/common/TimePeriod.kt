package de.schnettler.common

enum class TimePeriod(private val key: String) {
    OVERALL("overall"),
    WEEK("7day"),
    MONTH("1month"),
    QUARTER_YEAR("3month"),
    HALF_YEAR("6month"),
    YEAR("12month");

    override fun toString(): String {
        return key
    }}