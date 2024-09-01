import org.apache.tools.ant.filters.BaseFilterReader
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.xml.sax.helpers.XMLReaderFactory
import org.xml.sax.InputSource

import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource
import javax.xml.transform.sax.SAXSource

import java.io.StringReader
import java.io.StringWriter

class XsltFileFilter extends BaseFilterReader {
    File transformation
    StringReader transformedData

    XsltFileFilter(Reader reader) {
        super(reader)
    }
    void setTemplate(String template) {
        transformation = new File(template)
        println 'string template'
    }

    void setTemplate(File template) {
        transformation = template
        println 'file template'
    }

    int read() {
        return getTransformed().read()
    }

    StringReader getTransformed() {
        if (transformedData == null) {
            println "getTrans"
            transformedData = xsltTransform()
        }
        return transformedData
    }

    StringReader xsltTransform() {
        println "transforming with template: ${transformation}"
        def factory = TransformerFactory.newDefaultInstance()
        def templateSrc = new StreamSource(transformation.newInputStream())
        templateSrc.setSystemId(transformation.getPath())
        def transformer = factory.newTransformer(templateSrc)
        def reader = XMLReaderFactory.createXMLReader();
        reader.setFeature("http://apache.org/xml/features/" +
                           "nonvalidating/load-external-dtd",
                           false);
        def writer = new StringWriter()
        transformer.transform(new SAXSource(reader, new InputSource(this.in)), new StreamResult(writer))
        return new StringReader(writer.toString())
    }
}
