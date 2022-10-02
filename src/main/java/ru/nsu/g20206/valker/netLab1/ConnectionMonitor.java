package ru.nsu.g20206.valker.netLab1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConnectionMonitor {
    private long disconnectTimeout;
    Map<String, Long> connectedIP = new HashMap<>();

    public int getConnectedIpCount() {
        return connectedIP.size();
    }

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
        for (String str : disconnectedIP) {
            connectedIP.remove(str);
        }
        return disconnectedIP;
    }
}
