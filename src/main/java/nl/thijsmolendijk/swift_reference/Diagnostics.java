package nl.thijsmolendijk.swift_reference;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by molenzwiebel on 16-12-15.
 */
public class Diagnostics {
    public enum MessageKind {
        NOTE,
        WARNING,
        ERROR;
    }

    /**
     * Prints a debugging message informing about an error to stderr.
     * @param kind the kind of message to send
     * @param message the message contents
     * @param source the location where the error occured
     * @param ranges possible ranges that should be indicated in the source
     */
    public static void printMessage(MessageKind kind, String message, SourceLocation source, SourceRange... ranges) {
        getMessage(source, kind, message, Arrays.asList(ranges)).print(System.err);
    }

    private static Diagnostic getMessage(SourceLocation loc, MessageKind kind, String message, List<SourceRange> ranges) {
        int lineStart = loc.offset;
        while (lineStart != 0 && loc.source.charAt(lineStart - 1) != '\n' && loc.source.charAt(lineStart - 1) != '\r') {
            lineStart--;
        }

        int lineEnd = loc.offset;
        while (lineEnd != loc.source.length() && loc.source.charAt(lineEnd) != '\n' && loc.source.charAt(lineEnd) != '\r') {
            lineEnd++;
        }

        String line = loc.source.substring(lineStart, lineEnd);
        ArrayList<SourceRange> fixedRanges = new ArrayList<>();
        for (SourceRange r : ranges) {
            if (r.start < lineStart || r.end > lineEnd) continue;

            if (r.start < lineStart) r.start = lineStart;
            if (r.end > lineEnd) r.end = lineEnd;

            r.start = r.start - lineStart;
            r.end = r.end - lineStart;

            fixedRanges.add(r);
        }

        int lineNo = 1;
        int ptr = 0;
        while (ptr != lineStart) {
            if (loc.source.charAt(ptr) == '\n') lineNo++;
            ptr++;
        }

        return new Diagnostic(loc.source, loc.filename, loc, lineNo, loc.offset - lineStart, kind, message, line, fixedRanges);
    }

    private static final class Diagnostic {
        private String source;
        private String filename;
        private SourceLocation location;
        private int lineNumber;
        private int columnNumber;
        private MessageKind kind;
        private String message;
        private String line;
        private List<SourceRange> ranges;

        public Diagnostic() {
            this(MessageKind.ERROR, null, null);
        }

        public Diagnostic(MessageKind kind, String filename, String message) {
            this(null, filename, null, -1, -1, kind, null, message, Collections.<SourceRange>emptyList());
        }

        public Diagnostic(String source, String filename, SourceLocation location, int lineNumber, int columnNumber, MessageKind kind, String message, String line, List<SourceRange> ranges) {
            this.source = source;
            this.filename = filename;
            this.location = location;
            this.lineNumber = lineNumber;
            this.columnNumber = columnNumber;
            this.kind = kind;
            this.message = message;
            this.line = line;
            this.ranges = ranges;
        }

        public void print(PrintStream out) {
            // Print filename and location.
            if (filename != null && !filename.isEmpty()) {
                out.print(filename.equals("-") ? "<stdin>" : filename);

                if (lineNumber != -1) {
                    out.print(":");
                    out.print(lineNumber);
                    if (columnNumber != -1) {
                        out.print(":");
                        out.print(columnNumber);
                    }
                }

                out.print(": ");
            }

            // Print kind.
            switch (kind) {
                case ERROR:
                    out.print("error: ");
                    break;
                case WARNING:
                    out.print("warning: ");
                    break;
                case NOTE:
                    out.print("note: ");
                    break;
            }

            // Print message
            if (message != null) {
                out.print(message);
            }
            out.println();

            if (lineNumber == -1 || columnNumber == -1) // If we have no location information for the source extract.
                return;

            // Print range underlines.
            int numColumns = line.length();
            List<String> caretLine = IntStream.range(0, numColumns + 1).mapToObj(x -> " ").collect(Collectors.toList());

            for (SourceRange range : ranges) {
                for (int i = range.start; i < range.end; i++) {
                    caretLine.set(i, "~");
                }
            }

            // Write caret
            if (columnNumber <= numColumns)
                caretLine.set(columnNumber, "^");
            else
                caretLine.set(numColumns, "^");

            // Clean trailing whitespace
            String l = caretLine.get(numColumns);
            int i = numColumns;
            while (l.equals(" ")) {
                caretLine.remove(i);
                l = caretLine.get(--i);
            }

            // Print source line, handle tabs correctly.
            int col = 0;
            for (char c : line.toCharArray()) {
                if (c != '\t') {
                    out.print(c);
                    col++;
                } else {
                    do {
                        out.print(' ');
                        col++;
                    } while ((col % 8) != 0);
                }
            }
            out.print('\n');

            out.print(caretLine.stream().collect(Collectors.joining()));
            out.print('\n');
        }
    }

    public static final class SourceLocation {
        private String filename;
        private String source;
        private int offset;

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

    public static final class SourceRange {
        private String filename;
        private String source;
        private int start;
        private int end;

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
}
