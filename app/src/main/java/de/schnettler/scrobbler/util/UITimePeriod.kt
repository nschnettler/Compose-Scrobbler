package de.schnettler.scrobbler.util

import androidx.annotation.StringRes
import de.schnettler.common.TimePeriod
import de.schnettler.scrobbler.R

enum class UITimePeriod(val period: TimePeriod, @StringRes val titleRes: Int, @StringRes val shortTitleRes: Int) {
    OVERALL(TimePeriod.OVERALL, R.string.period_overall, R.string.period_overall_short),
    WEEK(TimePeriod.WEEK, R.string.period_week, R.string.period_week_short),
    MONTH(TimePeriod.MONTH, R.string.period_month, R.string.period_month_short),
    QUARTER_YEAR(TimePeriod.QUARTER_YEAR, R.string.period_quarter, R.string.period_quarter_short),
    HALF_YEAR(TimePeriod.HALF_YEAR, R.string.period_halfyear, R.string.period_halfyear_short),
    YEAR(TimePeriod.YEAR, R.string.period_year, R.string.period_year_short),
}