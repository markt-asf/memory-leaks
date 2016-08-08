package org.apache.markt.leaks.net;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class JarUrlConnectionLeak {

    private static final String TEST_JAR_PATH = "res/trivial.jar";

    public static void main(String[] args) throws IOException {
        JarUrlConnectionLeak leak = new JarUrlConnectionLeak();
        leak.doLeakTest();
    }


    private void doLeakTest() throws IOException {
        // Copy JAR
        File testJarSourceFile = new File(TEST_JAR_PATH);
        if (!testJarSourceFile.isFile()) {
            throw new IllegalStateException("Missing test JAR");
        }

        File tmpJar = File.createTempFile("testJar", ".jar");

        Files.copy(testJarSourceFile.toPath(), tmpJar.toPath(),
                StandardCopyOption.REPLACE_EXISTING);

        System.out.println(tmpJar.getAbsolutePath());

        // Open connection and use it
        createLeak(tmpJar);

        // Try and delete copy
        boolean deleteResult = tmpJar.delete();

        if (!deleteResult) {
            // - Windows will fail
            System.out.println("FAIL: [" + tmpJar.getAbsolutePath() + "] could not be deleted");
        }

        // - TODO: Other OS's check with lsof
    }

    private void createLeak(File file) throws IOException {
        String jarUrlString = "jar:" + file.toURI().toURL().toString() + "!/org/apache/markt/leaks/test.txt";
        URL jarUrl = new URL(jarUrlString);
        JarURLConnection jarUrlConn = (JarURLConnection) jarUrl.openConnection();
        // Uncomment this line to fix the leak
        //jarUrlConn.setUseCaches(false);
        jarUrlConn.connect();
        jarUrlConn.getInputStream().close();
    }
}
