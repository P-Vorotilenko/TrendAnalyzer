package vorotilenko.trendanalyzer.activities.select

/**
 * Item in a list of exchanges or currencies
 */
data class ListItem(
    /**
     * Name that will be shown to user
     */
    val name: String,
    /**
     * Image resource of exchange's or currency's logo
     */
    val logoRes: Int
)
