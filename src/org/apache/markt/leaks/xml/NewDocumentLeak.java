package org.apache.markt.leaks.xml;

import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.markt.leaks.LeakBase;
import org.w3c.dom.Document;
import org.w3c.dom.ls.DOMImplementationLS;

/**
 * The new document leak is fixed in Java 7 onwards:
 * http://bugs.java.com/bugdatabase/view_bug.do?bug_id=6916498
 *
 * The other two leaks are not yet fixed:
 * https://bz.apache.org/bugzilla/show_bug.cgi?id=58486
 * https://bugs.openjdk.java.net/browse/JDK-8146961
 *
 * Note: Some profilers (YourKit using YourKit snapshot format) and Eclipse MAT
 *       are unable to identify the root cause of this memory leak. This is
 *       because they use the JVMTI API to enumerate the objects on the heap
 *       and, thanks to https://bugs.openjdk.java.net/browse/JDK-4496456 the
 *       backtrace field that holds the problematic reference is explicitly
 *       excluded. If the HPROF memory snapshot format is used with YourKit it
 *       is possible to trace the references to the root of the memory leak.
 *
 * TODO: The code below requires modification to demonstrate the leak. This is
 *       because the leak will only appear if a class loaded by the module
 *       class loader is in the stack trace of the RuntimeException when it is
 *       created (as happens when similar code is executed from a JSP or
 *       Servlet). Currently, this is not the case so the leak does not appear.
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