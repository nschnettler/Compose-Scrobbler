package de.schnettler.common

enum class TimePeriod(private val key: String, val niceName: String) {
    OVERALL("overall", "Insgesamt"),
    WEEK("7day", "Letzte Woche"),
    MONTH("1month", "Letzter Monat"),
    QUARTER_YEAR("3month", "Letztes Quartal"),
    HALF_YEAR("6month", "Letztes Halbjahr"),
    YEAR("12month", "Letztes Jahr");

    override fun toString(): String {
        return key
    }}