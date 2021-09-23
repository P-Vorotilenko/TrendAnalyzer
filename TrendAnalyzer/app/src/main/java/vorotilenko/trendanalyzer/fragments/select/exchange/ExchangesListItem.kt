package vorotilenko.trendanalyzer.fragments.select.exchange

import vorotilenko.trendanalyzer.fragments.select.ListItem

/**
 * Item in a list of exchanges.
 */
data class ExchangesListItem(
    /**
     * Name that will be shown to user.
     */
    override val name: String,
    /**
     * Image resource of exchange's logo.
     */
    override val logoRes: Int
) : ListItem(name, logoRes)
