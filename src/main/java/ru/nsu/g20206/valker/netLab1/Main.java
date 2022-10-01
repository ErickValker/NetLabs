package ru.nsu.g20206.valker.netLab1;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class Main {

    final static int DEFAULT_PORT = 8000;
    final static int DEFAULT_DISCONNECT_TIME = 5000;
    final static int DEFAULT_COOLDOWN_SEND_TIME = 1000;
    final static int DEFAULT_RECEIVE_TIMEOUT = 1000;


    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            throw new IllegalArgumentException("IPv4 or IPv6 address expected");
        }
        String multicastIP = args[0];
        InetAddress multicastGroup = InetAddress.getByName(multicastIP);
        ConnectionMonitor connectionMonitor = new ConnectionMonitor(DEFAULT_DISCONNECT_TIME);
        try (Sender sender = new Sender(multicastGroup, DEFAULT_PORT, DEFAULT_COOLDOWN_SEND_TIME);
             Receiver receiver = new Receiver(multicastGroup, DEFAULT_PORT, DEFAULT_RECEIVE_TIMEOUT)) {
            while (true) {
                sender.sendMessage("Grisha privet");
                try {
                    receiver.receive();
                    String receivedAddress = receiver.getLastReceivedAddress();
                    boolean wasConnected = connectionMonitor.putAddress(receivedAddress);
                    if (!wasConnected) {
                        System.out.println(receivedAddress + " connected");
                    }
                } catch (SocketTimeoutException ignored) {
                } finally {
                    connectionMonitor.getDisconnected().forEach(
                            (ipAddress) -> System.out.println(ipAddress + " disconnected")

                    );
                }
            }
        }
    }
}
