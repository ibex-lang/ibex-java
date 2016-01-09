package nl.thijsmolendijk.molen.parsing

/**
 * Represents a range in a source.
 *
 * Created by molenzwiebel on 09-01-16.
 */
data class SourceRange(internal val filename: String, internal val source: String, internal var start: Int, internal var end: Int)