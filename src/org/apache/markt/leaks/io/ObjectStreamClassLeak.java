package org.apache.markt.leaks.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import org.apache.markt.leaks.LeakBase;
import org.apache.markt.leaks.RelaxedClassLoader;

/**
 * All JREs leak
 */
public class ObjectStreamClassLeak extends LeakBase {

    public static void main(String[] args) {
        ObjectStreamClassLeak objectStreamClassLeak = new ObjectStreamClassLeak();
        objectStreamClassLeak.doLeakTest();
    }


    @Override
    protected void createLeakingObjects() {

        // The class that is going to trigger the leak when serialized.
        Class<?> clazz = null;

        // clazz needs to be loaded by the module class loader.
        // Get the class loader
        RelaxedClassLoader cl = (RelaxedClassLoader) Thread.currentThread().getContextClassLoader();

        // Load the bytes for the class that will be serialized to trigger the
        // leak
        byte[] classBytes = new byte[2048];
        int offset = 0;
        InputStream is = null;
        try {
            is = cl.getResourceAsStream("org/apache/markt/leaks/io/Dummy.class");
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

            clazz = cl.defineClass0("org.apache.markt.leaks.io.Dummy", classBytes, 0, offset,
                    this.getClass().getProtectionDomain());
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

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(clazz);
            oos.flush();
        } catch (IOException e) {
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