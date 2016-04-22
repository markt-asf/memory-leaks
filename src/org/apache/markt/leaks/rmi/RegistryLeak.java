package org.apache.markt.leaks.rmi;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLClassLoader;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

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

        // Check for leaks
        int count = 0;
        while (count < 10 && registryLeak.leakCheck()) {
            // Trigger GC
            System.gc();
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            count++;
        }
        System.out.println("There were " + count + " calls to GC");

        if (registryLeak.leakCheck()) {
            System.out.println("Leak");
        } else {
            System.out.println("No leak");
        }
    }

    private static final ClassLoader ORIGINAL_CLASS_LOADER =
            Thread.currentThread().getContextClassLoader();

    private WeakReference<ClassLoader> moduleClassLoaderRef;
    private Registry registry;


    private void start() {
        ClassLoader moduleClassLoader = new URLClassLoader(new URL[] {}, ORIGINAL_CLASS_LOADER);

        Thread.currentThread().setContextClassLoader(moduleClassLoader);
        moduleClassLoaderRef = new WeakReference<>(moduleClassLoader);
    }


    private void register() {
        try {
            registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void deregister() {
        // TODO: How to make this work without the reference to the registry?
        //       Need a registry list
        //       Need to be able to determine the TCCL used for each registry
        try {
            UnicastRemoteObject.unexportObject(registry, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void stop() {
        Thread.currentThread().setContextClassLoader(ORIGINAL_CLASS_LOADER);
    }


    private boolean leakCheck() {
        return moduleClassLoaderRef.get() != null;
    }
}