import org.apache.tools.ant.filters.BaseFilterReader
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging

import java.util.regex.Pattern

class InstXmlFileFilter extends BaseFilterReader {
    StringReader substituted;

    InstXmlFileFilter(Reader reader) {
        super(reader)
    }

    int read() {
        if (substituted == null) {
            substituted = substitute()
        }
        return substituted.read()
    }

    StringReader substitute() {
        def writer = new StringWriter()
        def pattern = Pattern.compile('\\$<([^>]*)>')
        def line = this.in.readLine()
        while (line != null) {
            def match = pattern.matcher(line)
            def replacedLine = ""
            def position = 0
            for (; match.find(position); position=match.end()) {
                replacedLine = "${replacedLine}${line.substring(position, match.start())}\$[${match.group(1)}]"
            }
            if (replacedLine) {
                println line
                line = "${replacedLine}${line.substring(position)}"
                println line
            }
            writer.write("${line}\n")
            line = this.in.readLine()
        }
        return new StringReader(writer.toString())
    }
}
