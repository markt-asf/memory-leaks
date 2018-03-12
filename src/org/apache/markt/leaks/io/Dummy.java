package org.apache.markt.leaks.io;

import java.io.Serializable;

public class Dummy implements Serializable {

    private static final long serialVersionUID = 1L;

    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
