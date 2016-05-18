package org.apache.markt.leaks;

import java.net.URL;
import java.net.URLClassLoader;
import java.security.ProtectionDomain;

/**
 * Need to be able to call defineClass to demonstrate some of the leaks. The
 * sole purpose of this class is to create a ClassLoader with a public method
 * that allows that.
 */
public class RelaxedClassLoader extends URLClassLoader {

    public RelaxedClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public Class<?> defineClass0(String name,
            byte[] b, int off, int len,
            ProtectionDomain pd) {
        return super.defineClass(name, b, off, len, pd);
    }
}
