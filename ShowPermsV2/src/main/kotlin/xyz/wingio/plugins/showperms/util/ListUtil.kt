package xyz.wingio.plugins.showperms.util

/**
 * Returns a list of elements sorted according to either their
 * natural ascending or descending sort order
 *
 * @param sortOrder The order in which to sort items, either ascending or descending
 * @param selector Used to determine what the items should be sorted by
 * @return A sorted list
 */
inline fun <T, R: Comparable<R>> Collection<T>.sortedBy(
    sortOrder: SortOrder,
    crossinline selector: (T) -> R?
): List<T> {
    return when (sortOrder) {
        SortOrder.Ascending -> sortedBy(selector)
        SortOrder.Descending -> sortedByDescending(selector)
    }
}