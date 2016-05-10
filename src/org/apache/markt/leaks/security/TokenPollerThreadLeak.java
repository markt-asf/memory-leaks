package org.apache.markt.leaks.security;

import org.apache.markt.leaks.LeakBase;

/**
 * Java 5
 *   - leaks
 * Java 6
 *   - leaks
 * Java 7
 *  - leaks
 * Java 8
 *  - leaks
 */
public class TokenPollerThreadLeak extends LeakBase {

    public static void main(String[] args) {
        TokenPollerThreadLeak tokenPollerThreadLeak = new TokenPollerThreadLeak();
        tokenPollerThreadLeak.doLeakTest();
    }


    @Override
    protected void createLeakingObjects() {
        // D:\repos\jdk9dev\jdk\src\jdk.crypto.pkcs11\share\classes\sun\security\pkcs11\SunPKCS11.java
        // Triggering depends on hardware I don't have
    }


    @Override
    protected void cleanUpLeakingObjects() {
        // None
    }
}