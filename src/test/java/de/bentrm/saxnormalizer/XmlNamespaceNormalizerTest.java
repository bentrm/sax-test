package de.bentrm.saxnormalizer;

import org.jdom2.JDOMException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.Diff;

import javax.xml.XMLConstants;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class XmlNamespaceNormalizerTest {

    private final String NS_URI = "http://www.w3.org/1999/xhtml";

    private final Map<String, String> NS_CONTEXT = Map.of(
            XMLConstants.DEFAULT_NS_PREFIX, NS_URI,
            "xhtml", NS_URI,
            "xbody", NS_URI,
            "junit", "https://junit.org"
    );

    @Test
    public void normalizeDefaultNamespace() throws JDOMException, IOException {
        // given
        InputStream inputStream = getResourceAsInputStream("no-prefix.xml");
        InputStream controlStream = getResourceAsInputStream("no-prefix.xml");

        // when
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        XmlNamespaceNormalizer.normalizeDefaultNamespace(inputStream, outputStream, "http://www.w3.org/1999/xhtml");

        // then
        ByteArrayInputStream testStream = new ByteArrayInputStream(outputStream.toByteArray());
        Diff diff = DiffBuilder
                .compare(Input.from(controlStream))
                .withTest(Input.from(testStream))
                .withNamespaceContext(NS_CONTEXT)
                .ignoreWhitespace()
                .build();

        assertFalse(diff.hasDifferences(), diff.toString());
    }

    @Test
    public void normalizeDefaultNamespace_onePrefix_isRemoved() throws JDOMException, IOException {
        // given
        InputStream inputStream = getResourceAsInputStream("one-prefix.xml");
        InputStream controlStream = getResourceAsInputStream("one-prefix.xml");

        // when
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        XmlNamespaceNormalizer.normalizeDefaultNamespace(inputStream, outputStream, "http://www.w3.org/1999/xhtml");
        String test = outputStream.toString(StandardCharsets.UTF_8);

        // then
        Diff diff = DiffBuilder
                .compare(Input.from(controlStream))
                .withTest(Input.from(test))
                .withNamespaceContext(NS_CONTEXT)
                .ignoreWhitespace()
                .checkForSimilar() // ignore different prefixes
                .build();

        assertFalse(diff.hasDifferences(), diff.toString());
    }

    @Test
    public void normalizeDefaultNamespace_multiplePrefixes_areRemoved() throws JDOMException, IOException {
        // given
        InputStream inputStream = getResourceAsInputStream("multiple-prefixes.xml");
        InputStream controlStream = getResourceAsInputStream("multiple-prefixes.xml");

        // when
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        XmlNamespaceNormalizer.normalizeDefaultNamespace(inputStream, outputStream, "http://www.w3.org/1999/xhtml#");
        String test = outputStream.toString(StandardCharsets.UTF_8);

        System.out.println(test);

        // then
        Diff diff = DiffBuilder
                .compare(Input.from(controlStream))
                .withTest(Input.from(test))
                .withNamespaceContext(NS_CONTEXT)
                .ignoreWhitespace()
                .checkForSimilar() // ignore different prefixes
                .build();

        assertFalse(diff.hasDifferences(), diff.toString());
    }

    @Test
    public void normalizeDefaultNamespace_multiplePrefixesAndNamespaces_obsoletePrefixesAreRemovedAndNamespacesArePreserved() throws JDOMException, IOException {
        // given
        InputStream inputStream = getResourceAsInputStream("multiple-prefixes-and-namespaces.xml");
        InputStream controlStream = getResourceAsInputStream("multiple-prefixes-and-namespaces.xml");

        // when
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        XmlNamespaceNormalizer.normalizeDefaultNamespace(inputStream, outputStream, "http://www.w3.org/1999/xhtml");
        String test = outputStream.toString(StandardCharsets.UTF_8);

        System.out.println(test);

        // then
        Diff diff = DiffBuilder
                .compare(Input.from(controlStream))
                .withTest(Input.from(test))
                .withNamespaceContext(NS_CONTEXT)
                .ignoreWhitespace()
                .checkForSimilar() // ignore different prefixes
                .build();

        assertFalse(diff.hasDifferences(), diff.toString());
    }

    public InputStream getResourceAsInputStream(String resourcePath) {
        return getClass().getClassLoader().getResourceAsStream(resourcePath);
    }

}
