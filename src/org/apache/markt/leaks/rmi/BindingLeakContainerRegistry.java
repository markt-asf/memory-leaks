package org.apache.markt.leaks.rmi;

import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.apache.markt.leaks.LeakBase;

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
}