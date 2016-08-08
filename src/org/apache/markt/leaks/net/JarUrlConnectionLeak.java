package org.apache.markt.leaks.net;

public class JarUrlConnectionLeak {

    public static void main(String[] args) {
        JarUrlConnectionLeak leak = new JarUrlConnectionLeak();
        leak.doLeakTest();
    }


    private void doLeakTest() {
        // Copy JAR

        // Open connection

        // Close connection

        // Try and delete copy
        // - Windows will file
        // - Other OS's check with lsof
    }
}
