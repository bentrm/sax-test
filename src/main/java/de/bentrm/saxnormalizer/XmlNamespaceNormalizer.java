package de.bentrm.saxnormalizer;

import org.jdom2.*;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.List;

public class XmlNamespaceNormalizer {

    public static void normalizeDefaultNamespace(InputStream input, OutputStream output, String targetNamespaceUri)
            throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(input);

        Element root = document.getRootElement();
        Namespace namespace = root.getNamespace();
        String rootNamespaceUri = namespace.getURI();

        if (!rootNamespaceUri.contains(targetNamespaceUri)) {
            throw new IllegalArgumentException("Root element uses unexpected namespace URI.");
        }

        Namespace targetNamespace = Namespace.getNamespace(rootNamespaceUri);
        processElement(root, targetNamespace);

        XMLOutputter xmlOutput = new XMLOutputter();
        xmlOutput.setFormat(Format.getPrettyFormat());
        xmlOutput.output(document, output);
    }

    private static void processElement(Element element, Namespace targetNamespace) {
        if (element.getNamespace().equals(targetNamespace)) {
            // Namespace equality is checked by URI alone.
            // Settings the namespace declaration explicitly removes any unnecessary prefix declarations.
            element.setNamespace(targetNamespace);
        }

        List<Element> children = element.getChildren();
        children.forEach(child -> processElement(child, targetNamespace));
    }
}
