package org.apache.markt.leaks.rmi;

import java.io.Serializable;

public class ChatImpl implements Chat, Serializable {

    private static final long serialVersionUID = 1L;

    public String start() {
        return "hello";
    }
}
