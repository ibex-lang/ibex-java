package nl.thijsmolendijk.ibex.parse;

/**
 * Represents a range in a source file.
 *
 * Created by molenzwiebel on 19-12-15.
 */
public class SourceRange {
    /*package-private*/ String filename;
    /*package-private*/ String source;
    /*package-private*/ int start;
    /*package-private*/ int end;

    public SourceRange(String filename, String source, int start, int end) {
        this.filename = filename;
        this.source = source;
        this.start = start;
        this.end = end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SourceRange that = (SourceRange) o;
        return start == that.start && end == that.end && !(source != null ? !source.equals(that.source) : that.source != null);
    }

    @Override
    public int hashCode() {
        int result = source != null ? source.hashCode() : 0;
        result = 31 * result + start;
        result = 31 * result + end;
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SourceRange{");
        sb.append("filename='").append(filename).append('\'');
        sb.append(", start=").append(start);
        sb.append(", end=").append(end);
        sb.append('}');
        return sb.toString();
    }
}
