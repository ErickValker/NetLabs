package ru.nsu.g20206.valker.netLab1;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;

public class Receiver implements AutoCloseable {
    private final int DEFAULT_BUFFER_SIZE = 256;
    private final MulticastSocket socket;
    private byte[] lastMessage;
    private String lastReceivedAddress;

    public Receiver(InetAddress group, int port, int timeout) throws IOException {
        socket = new MulticastSocket(port);
        if (timeout < 0) {
            throw new IllegalArgumentException("Time must be positive");
        }
        socket.setSoTimeout(timeout);
        socket.joinGroup(group);
    }

    @Override
    public void close() {
        if (!socket.isClosed()) {
            socket.close();
        }
    }

    public void receive() throws IOException {
        receive(DEFAULT_BUFFER_SIZE);
    }

    public void receive(int bufferSize) throws IOException {
        byte[] buffer = new byte[bufferSize];
        DatagramPacket packet = new DatagramPacket(buffer, bufferSize);
        try {
            socket.receive(packet);
            lastMessage = packet.getData();
            lastReceivedAddress = packet.getSocketAddress().toString();
        } catch (SocketTimeoutException ste) {
            lastMessage = null;
            lastReceivedAddress = null;
            throw ste;
        }
    }

    public String getLastReceivedAddress() {
        return lastReceivedAddress;
    }
}
