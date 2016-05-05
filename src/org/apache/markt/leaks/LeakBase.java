package org.apache.markt.leaks;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Base class for memory leak tests.
 */
public abstract class LeakBase {

    private static final ClassLoader ORIGINAL_CLASS_LOADER =
            Thread.currentThread().getContextClassLoader();

    private WeakReference<ClassLoader> moduleClassLoaderRef;


    protected void doLeakTest() {
        // Switch TCCL
        start();

        // Create Object(s) that trigger the leak
        createLeakingObjects();

        // Clean up the leaked objects
        cleanUpLeakingObjects();

        // Restore TCCL
        stop();

        // Check for leaks
        int count = 0;
        while (count < 10 && leakCheck()) {
            // Trigger GC
            System.gc();
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            count++;
        }
        if (count == 0) {
            System.out.println("No calls to GC were required. Is the test broken?");
        } else if (count == 1) {
            System.out.println("One call to GC was required, as expected.");
        } else {
            System.out.println("There were " + count +
                    " calls to GC. Ideally, only one should be required.");
        }

        if (leakCheck()) {
            System.out.println("Leak");
        } else {
            System.out.println("No leak");
        }
    }


    private void start() {
        ClassLoader moduleClassLoader = new URLClassLoader(new URL[] {}, ORIGINAL_CLASS_LOADER);

        Thread.currentThread().setContextClassLoader(moduleClassLoader);
        moduleClassLoaderRef = new WeakReference<ClassLoader>(moduleClassLoader);
    }


    private void stop() {
        Thread.currentThread().setContextClassLoader(ORIGINAL_CLASS_LOADER);
    }


    private boolean leakCheck() {
        return moduleClassLoaderRef.get() != null;
    }


    protected abstract void createLeakingObjects();

    protected abstract void cleanUpLeakingObjects();
}
