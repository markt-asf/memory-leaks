package org.apache.markt.leaks.rmi;

import java.lang.reflect.Method;

import org.apache.markt.leaks.LeakBase;

/**
 * Java 5 - leaks
 * Java 6 - leaks
 * Java 7 - leaks
 * Java 8 - leaks
 * Java 9 - Fixed
 *
 * Note: One way to trigger this via public APIs is by creating and starting a
 *       RMIConnectorServer instance. However, that creates a fair amount of
 *       noise that makes it difficult to isolate this issue. Therefore this
 *       test uses reflection to trigger the specific problem.
 *
 * http://bugs.java.com/bugdatabase/view_bug.do?bug_id=JDK-8157570
 */
public class GcThreadLeak extends LeakBase {

    public static void main(String[] args) {
        GcThreadLeak gcThreadLeak = new GcThreadLeak();
        gcThreadLeak.doLeakTest();
    }


    @Override
    protected void createLeakingObjects() {
        try {
            Class<?> clazz;
            try {
                clazz = Class.forName("sun.misc.GC");
            } catch (ClassNotFoundException cnfe) {
                // Java 9? Try the new name
                // Note: The JVM needs to be started with
                //       -XaddExports:java.rmi/sun.rmi.transport=ALL-UNNAMED
                clazz = Class.forName("sun.rmi.transport.GC");
            }
            Method method = clazz.getDeclaredMethod(
                    "requestLatency",
                    new Class[] {long.class});
            method.setAccessible(true);
            method.invoke(null, Long.valueOf(Long.MAX_VALUE - 1));
            Thread.currentThread().setContextClassLoader(GcThreadLeak.class.getClassLoader());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void cleanUpLeakingObjects() {
        // Comment out the clean-up to enable simple testing for the fix in new
        // releases.
        /*
        Thread[] threads = getThreads();

        for (Thread thread : threads) {
            if (thread != null) {
                ClassLoader threadCCL = thread.getContextClassLoader();
                if (threadCCL != null && threadCCL == getModuleClassLoader()) {
                    thread.setContextClassLoader(null);
                }
            }
        }
        */
    }


    private Thread[] getThreads() {
        // Get the current thread group
        ThreadGroup tg = Thread.currentThread().getThreadGroup();
        // Find the root thread group
        try {
            while (tg.getParent() != null) {
                tg = tg.getParent();
            }
        } catch (SecurityException se) {
            se.printStackTrace();
        }

        int threadCountGuess = tg.activeCount() + 50;
        Thread[] threads = new Thread[threadCountGuess];
        int threadCountActual = tg.enumerate(threads);
        // Make sure we don't miss any threads
        while (threadCountActual == threadCountGuess) {
            threadCountGuess *=2;
            threads = new Thread[threadCountGuess];
            // Note tg.enumerate(Thread[]) silently ignores any threads that
            // can't fit into the array
            threadCountActual = tg.enumerate(threads);
        }

        return threads;
    }
}