package org.apache.markt.leaks.rmi;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLClassLoader;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class BindingLeak {

    public static void main(String[] args) {

        BindingLeak bindingLeak = new BindingLeak();

        // Registry creation has a known leak (see RegistryLeak) so create it
        // under what is effectively the container class loader
        try {
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Setting this to true triggers a leak
        boolean register = false;

        // Switch TCCL
        bindingLeak.start();

        // Register new object in RMI
        bindingLeak.register(register);

        // Deregister object
        bindingLeak.deregister(register);

        // Restore TCCL
        bindingLeak.stop();

        // Trigger GC
        System.gc();

        // Check for leak
        bindingLeak.leakCheck();
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


    private void register(boolean register) {
        remoteObject = new ChatImpl();
        try {
            Chat stub = (Chat) UnicastRemoteObject.exportObject(remoteObject, 10180);
            if (register) {
                LocateRegistry.getRegistry().bind(NAME, stub);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void deregister(boolean register) {
        try {
            if (register) {
                LocateRegistry.getRegistry().unbind(NAME);
            }
            UnicastRemoteObject.unexportObject(remoteObject, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        remoteObject = null;
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