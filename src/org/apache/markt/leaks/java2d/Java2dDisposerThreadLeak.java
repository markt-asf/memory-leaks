package org.apache.markt.leaks.java2d;

import org.apache.markt.leaks.LeakBase;

/**
 * Java 5
 *  - update 22 - leaks
 * Java 6
 *  - update =<00 - leaks
 *    ??? when was this fixed ???
 *  - update >=45 - no leak
 * Java 7 - no leak
 * Java 8 - no leak
 */
public class Java2dDisposerThreadLeak extends LeakBase {

    public static void main(String[] args) {
        Java2dDisposerThreadLeak java2dDisposerThreadLeak = new Java2dDisposerThreadLeak();
        java2dDisposerThreadLeak.doLeakTest();
    }


    @Override
    protected void createLeakingObjects() {
        try {
            Class.forName("sun.java2d.Disposer");
        } catch (ClassNotFoundException e) {
            RuntimeException r = new RuntimeException();
            r.initCause(e);
            throw r;
        }
    }


    @Override
    protected void cleanUpLeakingObjects() {
        // None
    }
}