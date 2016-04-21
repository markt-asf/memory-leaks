package org.apache.markt.leaks.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Chat extends Remote {

    String start() throws RemoteException;
}
