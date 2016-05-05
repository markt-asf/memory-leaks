package org.apache.markt.leaks.awt;

import org.apache.markt.leaks.LeakBase;

/**
 * Java 5 - leaks
 * Java 6 - leaks
 * Java 7
 *  - update <=51 - leaks
 *  - update >=55 - no leak
 * Java 8
 *  - update 00   - leaks
 *  - update >=05 - no leak
 */
public class AwtThreadLeak extends LeakBase {

    public static void main(String[] args) {
        AwtThreadLeak awtThreadLeak = new AwtThreadLeak();
        awtThreadLeak.doLeakTest();
    }


    @Override
    protected void createLeakingObjects() {
        java.awt.Toolkit.getDefaultToolkit();
    }


    @Override
    protected void cleanUpLeakingObjects() {
        // None
    }
}