package org.apache.markt.leaks.rmi;

import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.apache.markt.leaks.LeakBase;

/**
 * Demonstrates that a web application that creates an RMI registry can prevent
 * related memory leaks by ensuring that the registry is unexported. It is not
 * necessary to unbind or unexport objects in the registry. Unexporting just the
 * registry is sufficient.
 *
 * Note that there is an outstanding TODO for {@link RegistryLeak} to determine
 * how to identify a web application created RMI registry.
 */
public class BindingLeakApplicationRegistry extends LeakBase {

    public static void main(String[] args) {
        BindingLeakApplicationRegistry bindingLeak = new BindingLeakApplicationRegistry();
        bindingLeak.doLeakTest();
    }


    private static final String NAME = "Chat";

    private Remote remoteObject;
    private Registry registry;


    @Override
    protected void createLeakingObjects() {
        try {
            // Equivalent to a web application creating an RMI registry.
            registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            remoteObject = new ChatImpl();
            Chat stub = (Chat) UnicastRemoteObject.exportObject(remoteObject, 0);
            registry.bind(NAME, stub);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void cleanUpLeakingObjects() {
        try {
            // Note: The follow two calls can be skipped but that then requires
            //       an extra GC.
            registry.unbind(NAME);
            UnicastRemoteObject.unexportObject(remoteObject, true);
            // Correct way for web application to close down RMI registry
            UnicastRemoteObject.unexportObject(registry, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        remoteObject = null;
        registry = null;
    }
}