package org.apache.markt.leaks.rmi;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLClassLoader;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class BindingNoLeak {

    public static void main(String[] args) {

        BindingNoLeak bindingNoLeak = new BindingNoLeak();

        // Registry creation has a known leak (see RegistryLeak) so create it
        // under what is effectively the container class loader
        try {
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Switch TCCL
        bindingNoLeak.start();

        // Register new object in RMI
        bindingNoLeak.register();

        // Deregister object
        bindingNoLeak.deregister();

        // Restore TCCL
        bindingNoLeak.stop();

        // Check for leaks
        int count = 0;
        while (count < 10 && bindingNoLeak.leakCheck()) {
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

        if (bindingNoLeak.leakCheck()) {
            System.out.println("Leak");
        } else {
            System.out.println("No leak");
        }
    }


    private static final ClassLoader ORIGINAL_CLASS_LOADER =
            Thread.currentThread().getContextClassLoader();
    private static final String NAME = "Chat";

    private WeakReference<ClassLoader> moduleClassLoaderRef;
    private Remote remoteObject;

    private void start() {
        ClassLoader moduleClassLoader = new URLClassLoader(new URL[] {}, ORIGINAL_CLASS_LOADER);

        Thread.currentThread().setContextClassLoader(moduleClassLoader);
        moduleClassLoaderRef = new WeakReference<>(moduleClassLoader);
    }


    private void register() {
        remoteObject = new ChatImpl();
        try {
            Chat stub = (Chat) UnicastRemoteObject.exportObject(remoteObject, 10180);
            LocateRegistry.getRegistry().bind(NAME, stub);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void deregister() {
        try {
            LocateRegistry.getRegistry().unbind(NAME);
            UnicastRemoteObject.unexportObject(remoteObject, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        remoteObject = null;
    }


    private void stop() {
        Thread.currentThread().setContextClassLoader(ORIGINAL_CLASS_LOADER);
    }


    private boolean leakCheck() {
        return moduleClassLoaderRef.get() != null;
    }
}