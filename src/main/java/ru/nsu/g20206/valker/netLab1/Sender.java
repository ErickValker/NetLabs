package ru.nsu.g20206.valker.netLab1;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Sender implements AutoCloseable {
    private final int port;
    private final long cooldownTime;
    private long lastSendUsingTime = 0;
    private final DatagramSocket socket;
    private final InetAddress group;

    public Sender(InetAddress group, int port, long cooldownTime) throws SocketException {
        this.group = group;
        this.port = port;
        socket = new DatagramSocket();
        if (cooldownTime < 0) {
            throw new IllegalArgumentException("Time must be positive");
        }
        this.cooldownTime = cooldownTime;
    }

    @Override
    public void close() {
        if (!socket.isClosed()) {
            socket.close();
        }
    }

    public void sendMessage(String message) throws IOException {
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, port);
        send(packet);
    }

    private void send(DatagramPacket packet) throws IOException {
        long currentTime = System.currentTimeMillis();
        long timePassed = currentTime - lastSendUsingTime;
        if (timePassed > cooldownTime) {
            lastSendUsingTime = currentTime;
            socket.send(packet);
        }
    }
}
