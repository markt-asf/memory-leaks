package org.apache.markt.leaks.net;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;

import sun.net.www.protocol.file.FileURLConnection;

public class TestJava9CacheFix {

    private static final String TEST_JAR_PATH = "res/trivial.jar";
    
    public static void main(String[] args) throws IOException {
    
        File jarFile = new File(TEST_JAR_PATH);
        URL jarFileURL = jarFile.toURI().toURL();

        String jarEntryUrlString = "jar:" + jarFileURL.toString() + "!/org/apache/markt/leaks/test.txt";
        URL jarEntryUrl = new URL(jarEntryUrlString);

        // Disable caching by default for JAR URLs
        URLConnection.setDefaultUseCaches("JAR", false);

        // Check caching for a URL type other than JAR
        // File is the simplest
        FileURLConnection fileUrlConn = (FileURLConnection) jarFileURL.openConnection();
        System.out.println(fileUrlConn.getUseCaches());
        fileUrlConn.connect();
        System.out.println(fileUrlConn.getUseCaches());
        fileUrlConn.getInputStream().close();
        
        // Check JARs
        JarURLConnection jarEntryUrlConn = (JarURLConnection) jarEntryUrl.openConnection();
        System.out.println(jarEntryUrlConn.getUseCaches());
        jarEntryUrlConn.connect();
        System.out.println(jarEntryUrlConn.getUseCaches());
        jarEntryUrlConn.getInputStream().close();
    }
}
