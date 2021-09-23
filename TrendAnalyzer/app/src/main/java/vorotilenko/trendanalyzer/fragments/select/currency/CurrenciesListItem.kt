package vorotilenko.trendanalyzer.fragments.select.currency

import vorotilenko.trendanalyzer.fragments.select.ListItem

/**
 * Item in a list of currencies.
 */
data class CurrenciesListItem(
    /**
     * Name that will be shown to user.
     */
    override val name: String,
    /**
     * Ticker.
     */
    val ticker: String,
    /**
     * Image resource of currency's logo.
     */
    override val logoRes: Int
) : ListItem(name, logoRes)
