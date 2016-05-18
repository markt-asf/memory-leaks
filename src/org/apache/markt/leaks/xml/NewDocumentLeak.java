package org.apache.markt.leaks.xml;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

import org.apache.markt.leaks.LeakBase;
import org.apache.markt.leaks.RelaxedClassLoader;

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
        // To get the leak we need to trigger initialisation from a class loaded
        // by the module class loader. That will pin the module class loader in
        // memory via the backtrace field of the static exceptions.

        // Get the class loader
        RelaxedClassLoader cl = (RelaxedClassLoader) Thread.currentThread().getContextClassLoader();

        // Load the bytes for the class that triggers initialisation
        byte[] classBytes = new byte[2048];
        int offset = 0;
        InputStream is = null;
        try {
            is = cl.getResourceAsStream("org/apache/markt/leaks/xml/StaticExceptionLeak.class");
            int read = is.read(classBytes, offset, classBytes.length-offset);
            while (read > -1) {
                offset += read;
                if (offset == classBytes.length) {
                    // Buffer full - double size
                    byte[] tmp = new byte[classBytes.length * 2];
                    System.arraycopy(classBytes, 0, tmp, 0, classBytes.length);
                    classBytes = tmp;
                }
                read = is.read(classBytes, offset, classBytes.length-offset);
            }

            Class<?> lpClass = cl.defineClass0("org.apache.markt.leaks.xml.StaticExceptionLeak",
                    classBytes, 0, offset, this.getClass().getProtectionDomain());
            Object obj = lpClass.newInstance();

            Method m = obj.getClass().getMethod("leak");
            m.invoke(obj);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
    }


    @Override
    protected void cleanUpLeakingObjects() {
        // None
    }
}