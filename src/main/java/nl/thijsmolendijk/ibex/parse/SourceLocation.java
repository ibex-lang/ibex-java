package nl.thijsmolendijk.ibex.parse;

/**
 * Represents a location in a source file.
 *
 * Created by molenzwiebel on 19-12-15.
 */
public class SourceLocation {
    /*package-private*/ String filename;
    /*package-private*/ String source;
    /*package-private*/ int offset;

    public SourceLocation(String filename, String source, int offset) {
        this.filename = filename;
        this.source = source;
        this.offset = offset;
    }

    public SourceRange range(SourceLocation other) {
        return new SourceRange(filename, source, Math.min(offset, other.offset), Math.max(offset, other.offset));
    }

    public SourceRange range(int other) {
        return new SourceRange(filename, source, offset, offset + other);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SourceLocation that = (SourceLocation) o;
        return offset == that.offset && !(source != null ? !source.equals(that.source) : that.source != null);
    }

    @Override
    public int hashCode() {
        int result = source != null ? source.hashCode() : 0;
        result = 31 * result + offset;
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SourceLocation{");
        sb.append("filename='").append(filename).append('\'');
        sb.append(", offset=").append(offset);
        sb.append('}');
        return sb.toString();
    }
}
