package de.bentrm.saxnormalizer;

import org.jdom2.Namespace;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class SaxNormalizerApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void namespacesAreNormalizedAndComparable() throws URISyntaxException {
        URI compare = new URI("http://google.de").normalize();
        URI test = new URI("http://google.de#").normalize();
        assertEquals(compare, test);
    }

}
