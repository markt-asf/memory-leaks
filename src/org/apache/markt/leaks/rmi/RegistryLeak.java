package org.apache.markt.leaks.rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.apache.markt.leaks.LeakBase;

/**
 * Demonstrates the correct way for a web application created RMI registry to be
 * closed down, thereby avoiding a memory leak.
 * <p>
 * TODO: Figure out how to identify a web application created registry so it
 *       can be shut down by the container if the web application fails to do
 *       so. We need:
 *       <ul>
 *       <li>The current registry list</li>
 *       <li>A way to determine TCCL for each registry</li>
 *       </ul>
 */
public class RegistryLeak extends LeakBase {

    public static void main(String[] args) {
        RegistryLeak registryLeak = new RegistryLeak();
        registryLeak.doLeakTest();
    }


    private Registry registry;


    @Override
    protected void createLeakingObjects() {
        try {
            registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void cleanUpLeakingObjects() {
        try {
            UnicastRemoteObject.unexportObject(registry, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}