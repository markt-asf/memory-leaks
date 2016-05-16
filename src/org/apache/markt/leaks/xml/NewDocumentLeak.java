package org.apache.markt.leaks.xml;

import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.markt.leaks.LeakBase;
import org.w3c.dom.Document;
import org.w3c.dom.ls.DOMImplementationLS;

/**
 * The new document leak is fixed in Java 7 onwards:
 * http://bugs.java.com/bugdatabase/view_bug.do?bug_id=6916498
 *
 * The Serializer and Normalizer leaks are very odd. Run the code in a Servlet
 * and a memory leak occurs. Run it in this stand-along test harness and no leak
 * occurs. Further, the GC roots for these leaks are not visible to the
 * profiler.
 * https://bz.apache.org/bugzilla/show_bug.cgi?id=58486
 * https://bugs.openjdk.java.net/browse/JDK-8146961
 *
 * Java 5
 *   - leaks
 * Java 6
 *   - leaks
 * Java 7
 *   - leaks
 * Java 8
 *   - leaks
 */
public class NewDocumentLeak extends LeakBase {

    public static void main(String[] args) {
        NewDocumentLeak newDocumentLeak = new NewDocumentLeak();
        newDocumentLeak.doLeakTest();
    }


    @Override
    protected void createLeakingObjects() {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            // Serializer
            document.createElement("test");
            DOMImplementationLS implementation = (DOMImplementationLS)document.getImplementation();
            implementation.createLSSerializer();
            // or
            // Normalizer
            // document.normalizeDocument();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void cleanUpLeakingObjects() {
        // None
    }
}