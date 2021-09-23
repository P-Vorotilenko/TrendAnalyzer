package vorotilenko.trendanalyzer.fragments.select

/**
 * Item in a list of exchanges or currencies.
 */
open class ListItem(
    /**
     * Name that will be shown to user.
     */
    open val name: String,
    /**
     * Image resource of item's logo.
     */
    open val logoRes: Int
)
