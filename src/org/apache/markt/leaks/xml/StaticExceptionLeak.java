package org.apache.markt.leaks.xml;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.ls.DOMImplementationLS;

/**
 * Triggers initialisation of the two locations known to create static
 * references to exceptions.
 *
 * https://bz.apache.org/bugzilla/show_bug.cgi?id=58486
 * https://bugs.openjdk.java.net/browse/JDK-8146961
 */
public class StaticExceptionLeak {

    public void leak() {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            // Serializer
            document.createElement("test");
            DOMImplementationLS implementation = (DOMImplementationLS)document.getImplementation();
            implementation.createLSSerializer();
            // Normalizer
            document.normalizeDocument();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
