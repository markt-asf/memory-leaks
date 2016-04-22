package org.apache.markt.leaks.rmi;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLClassLoader;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class BindingLeakContainerRegistry {

    public static void main(String[] args) {

        BindingLeakContainerRegistry bindingLeak = new BindingLeakContainerRegistry();

        // Creating registry here effectively makes it a container created
        // registry
        try {
            // Equivalent to a web application creating an RMI registry.
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Switch TCCL
        bindingLeak.start();

        // Register new object in RMI
        bindingLeak.register();

        // Deregister object
        bindingLeak.deregister();

        // Restore TCCL
        bindingLeak.stop();

        // Check for leaks
        int count = 0;
        while (count < 10 && bindingLeak.leakCheck()) {
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

        if (bindingLeak.leakCheck()) {
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
        try {
            remoteObject = new ChatImpl();
            Chat stub = (Chat) UnicastRemoteObject.exportObject(remoteObject, 0);
            LocateRegistry.getRegistry().bind(NAME, stub);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void deregister() {
        // TODO: Figure out how to make this work without the reference to
        //       remote object.
        try {
            LocateRegistry.getRegistry().unbind(NAME);
            // Note the following call is not required to prevent a memory leak
            // but not making the call means an additional GC is required to
            // clean up.
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