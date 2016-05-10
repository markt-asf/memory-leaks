package org.apache.markt.leaks.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.markt.leaks.LeakBase;
import org.w3c.dom.Document;

/**
 * Java 5
 *   - TBD
 * Java 6
 *   - TBD
 * Java 7
 *   - TBD
 * Java 8
 *   - TBD
 */
public class NewDocumentLeak extends LeakBase {

    public static void main(String[] args) {
        NewDocumentLeak newDocumentLeak = new NewDocumentLeak();
        newDocumentLeak.doLeakTest();
    }


    @Override
    protected void createLeakingObjects() {
        // Nov/Dec 2009. 6.0.x - r892620 6.0.21
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void cleanUpLeakingObjects() {
        // None
    }
}