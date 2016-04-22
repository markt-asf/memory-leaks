package org.apache.markt.leaks.rmi;

import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.apache.markt.leaks.LeakBase;

/**
 * Demonstrates that a web application that uses a container provided RMI
 * registry will not trigger a memory leak but will require additional GC calls
 * to clean up.
 *
 * TODO: How to force a clean-up when the original remote object is not
 *       available?
 *       How to clean up the reference that currently requires the additional
 *       GC?
 */
public class BindingLeakContainerRegistry extends LeakBase {

    public static void main(String[] args) {
        BindingLeakContainerRegistry bindingLeak = new BindingLeakContainerRegistry();
        try {
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        bindingLeak.doLeakTest();
    }


    private static final String NAME = "Chat";

    private Remote remoteObject;


    @Override
    protected void createLeakingObjects() {
        try {
            remoteObject = new ChatImpl();
            Chat stub = (Chat) UnicastRemoteObject.exportObject(remoteObject, 0);
            LocateRegistry.getRegistry().bind(NAME, stub);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void cleanUpLeakingObjects() {
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
}