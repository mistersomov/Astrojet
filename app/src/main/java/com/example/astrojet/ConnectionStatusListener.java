package com.example.astrojet;

public interface ConnectionStatusListener {

    void connect();
    void connectError();
    void disconnect();
}
