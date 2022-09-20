package ru.nsu.g20206.valker.netlab1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConnectionMonitor {
    private long disconnectTimeout;
    Map<String, Long> connectedIP = new HashMap<>();

    public ConnectionMonitor(long disconnectTimeout) {
        this.disconnectTimeout = disconnectTimeout;
    }

    public boolean putAddress(String ipAddress) {
        boolean wasConnected = connectedIP.containsKey(ipAddress);
        connectedIP.put(ipAddress, System.currentTimeMillis());
        return wasConnected;
    }

    public List<String> getDisconnected() {
        List<String> disconnectedIP = new ArrayList<>();
        long currentTime = System.currentTimeMillis();
        connectedIP.forEach((address, lastConnection) -> {
            long timePassed = currentTime - lastConnection;
            if (timePassed > disconnectTimeout) {
                disconnectedIP.add(address);
            }
        });
        disconnectedIP.forEach((value) -> connectedIP.remove(value));
        return disconnectedIP;
    }
}
