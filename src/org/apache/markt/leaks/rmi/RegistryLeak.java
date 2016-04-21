package org.apache.markt.leaks.rmi;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLClassLoader;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RegistryLeak {

    public static void main(String[] args) {

        RegistryLeak registryLeak = new RegistryLeak();

        // Switch TCCL
        registryLeak.start();

        // Create RMI registry
        registryLeak.register();

        // Clean-up registry
        registryLeak.deregister();

        // Restore TCCL
        registryLeak.stop();

        // Trigger GC
        System.gc();

        // Check for leak
        registryLeak.leakCheck();
    }

    private static final ClassLoader ORIGINAL_CLASS_LOADER =
            Thread.currentThread().getContextClassLoader();

    private WeakReference<ClassLoader> moduleClassLoaderRef;


    private void start() {
        ClassLoader moduleClassLoader = new URLClassLoader(new URL[] {}, ORIGINAL_CLASS_LOADER);

        Thread.currentThread().setContextClassLoader(moduleClassLoader);
        moduleClassLoaderRef = new WeakReference<>(moduleClassLoader);
    }


    private void register() {
        try {
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void deregister() {
        // NO-OP. NO API available.
    }


    private void stop() {
        Thread.currentThread().setContextClassLoader(ORIGINAL_CLASS_LOADER);
    }


    private void leakCheck() {
        if (moduleClassLoaderRef.get() == null) {
            System.out.println("No leak");
        } else {
            System.out.println("Leak");
        }
    }
}