package ru.nsu.g20206.valker.netLab1;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            throw new IllegalArgumentException("IPv4 or IPv6 address expected");
        }
        String multicastIP = args[0];
        InetAddress multicastGroup = InetAddress.getByName(multicastIP);
        ConnectionMonitor connectionMonitor = new ConnectionMonitor(ConstClass.DEFAULT_DISCONNECT_TIME);
        Thread exitThread = new Thread(new TerminalThread());
        exitThread.start();
        try (Sender sender = new Sender(multicastGroup, ConstClass.DEFAULT_PORT, ConstClass.DEFAULT_COOLDOWN_SEND_TIME);
             Receiver receiver = new Receiver(multicastGroup, ConstClass.DEFAULT_PORT, ConstClass.DEFAULT_RECEIVE_TIMEOUT)) {
            while (true) {
                sender.sendMessage("Grisha privet");
                try {
                    receiver.receive();
                    String receivedAddress = receiver.getLastReceivedAddress();
                    boolean wasConnected = connectionMonitor.putAddress(receivedAddress);
                    if (!wasConnected) {
                        System.out.println(receivedAddress + " connected " + "(Runned apps count: " + connectionMonitor.getConnectedIpCount() + ")");
                    }
                } catch (SocketTimeoutException ignored) {
                } finally {
                    for (String ipAddress : connectionMonitor.getDisconnected()) {
                        System.out.println(ipAddress + " disconnected " + "(Runned apps count: " + connectionMonitor.getConnectedIpCount() + ")");
                    }
                }
            }
        }
    }
}
