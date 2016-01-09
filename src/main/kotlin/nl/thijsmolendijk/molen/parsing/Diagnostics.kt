package nl.thijsmolendijk.molen.parsing

import java.io.PrintStream
import java.util.*

/**
 * Utility class for displaying messages about malformed code.

 * Created by molenzwiebel on 19-12-15.
 */
object Diagnostics {
    enum class MessageKind {
        NOTE,
        WARNING,
        ERROR
    }

    /**
     * Prints a debugging message informing about an error to stderr.
     * @param kind the kind of message to send
     * @param message the message contents
     * @param source the location where the error occured
     * @param ranges possible ranges that should be indicated in the source
     */
    fun printMessage(kind: MessageKind, message: String, source: SourceLocation, vararg ranges: SourceRange) {
        getMessage(source, kind, message, Arrays.asList(*ranges)).print(System.err)
    }

    /**
     * Simple helper function that generates a Diagnostic for the specified arguments.
     */
    private fun getMessage(loc: SourceLocation, kind: MessageKind, message: String, ranges: List<SourceRange>): Diagnostic {
        var lineStart = loc.offset
        while (lineStart != 0 && loc.source[lineStart - 1] != '\n' && loc.source[lineStart - 1] != '\r') {
            lineStart--
        }

        var lineEnd = loc.offset
        while (lineEnd != loc.source.length && loc.source[lineEnd] != '\n' && loc.source[lineEnd] != '\r') {
            lineEnd++
        }

        val line = loc.source.substring(lineStart, lineEnd)
        val fixedRanges = ArrayList<SourceRange>()
        for (r in ranges) {
            if (r.start < lineStart || r.end > lineEnd) continue

            if (r.start < lineStart) r.start = lineStart
            if (r.end > lineEnd) r.end = lineEnd

            r.start = r.start - lineStart
            r.end = r.end - lineStart

            fixedRanges.add(r)
        }

        var lineNo = 1
        var ptr = 0
        while (ptr != lineStart) {
            if (loc.source[ptr] == '\n') lineNo++
            ptr++
        }

        return Diagnostic(loc.source, loc.filename, loc, lineNo, loc.offset - lineStart, kind, message, line, fixedRanges)
    }

    /**
     * Simple class that holds information about a diagnostic that has yet to be displayed.
     */
    private class Diagnostic(private val source: String?, private val filename: String?, private val location: SourceLocation?, private val lineNumber: Int, private val columnNumber: Int, private val kind: MessageKind, private val message: String?, private val line: String, private val ranges: List<SourceRange>) {
        fun print(out: PrintStream) {
            // Print filename and location.
            if (filename != null && !filename.isEmpty()) {
                out.print(if (filename == "-") "<stdin>" else filename)

                if (lineNumber != -1) {
                    out.print(":")
                    out.print(lineNumber)
                    if (columnNumber != -1) {
                        out.print(":")
                        out.print(columnNumber)
                    }
                }

                out.print(": ")
            }

            // Print kind.
            when (kind) {
                MessageKind.ERROR -> out.print("error: ")
                MessageKind.WARNING -> out.print("warning: ")
                MessageKind.NOTE -> out.print("note: ")
            }

            // Print message
            if (message != null) {
                out.print(message)
            }
            out.println()

            if (lineNumber == -1 || columnNumber == -1)
            // If we have no location information for the source extract.
                return

            // Print range underlines.
            val numColumns = line.length
            val caretLine = ArrayList(" ".repeat(numColumns + 1).split(""))

            for (range in ranges) {
                for (i in range.start..range.end - 1) {
                    caretLine.set(i, "~")
                }
            }

            // Write caret
            if (columnNumber <= numColumns)
                caretLine[columnNumber] = "^"
            else
                caretLine[numColumns] = "^"

            // Clean trailing whitespace
            var l = caretLine[numColumns]
            var i = numColumns
            while (l == " ") {
                caretLine.removeAt(i)
                l = caretLine[--i]
            }

            // Print source line, handle tabs correctly.
            var col = 0
            for (c in line.toCharArray()) {
                if (c != '\t') {
                    out.print(c)
                    col++
                } else {
                    do {
                        out.print(' ')
                        col++
                    } while ((col % 8) != 0)
                }
            }
            out.print('\n')

            // Print caret.
            out.print(caretLine.joinToString(""))
            out.print('\n')
        }
    }
}