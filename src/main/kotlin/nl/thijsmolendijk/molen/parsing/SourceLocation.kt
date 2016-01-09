package nl.thijsmolendijk.molen.parsing

/**
 * Location in a source file.
 *
 * Created by molenzwiebel on 09-01-16.
 */
data class SourceLocation(internal val filename: String, internal val source: String, internal val offset: Int) {
    fun range(other: SourceLocation): SourceRange {
        return SourceRange(filename, source, Math.min(offset, other.offset), Math.max(offset, other.offset));
    }

    fun range(other: Int): SourceRange {
        return SourceRange(filename, source, offset, offset + other);
    }
}