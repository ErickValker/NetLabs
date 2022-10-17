package ru.nsu.g20206.valker.netLab1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static ru.nsu.g20206.valker.netLab1.ThreadManager.*;

public class ClientManager implements Runnable {
    private static Logger log = LoggerFactory.getLogger(ClientManager.class);

    private Socket clientSocket;
    private final byte[] buf;
    private final int clientId;
    private long fileSize = 0;

    public ClientManager(Socket client, int id) {
        this.clientId = id;
        clientSocket = client;
        buf = new byte[ConstClass.CHUNK_LENGTH];
    }

    private String getTotalData() {
        return getReceivedBytes(clientId) + "/" + fileSize;
    }

    @Override
    public void run() {
        try {
            File directory = new File("uploads/");
            if (!(directory.exists())) {
                directory.mkdir();
            }

            DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());

            String fileName = inputStream.readUTF();
            fileSize = inputStream.readLong();

            log.info("Client" + "(" + clientId + ")" + " connected");

            String newFileName = fileName;
            int copyCount = 0;
            while (new File("uploads/" + newFileName).exists()) {
                copyCount++;
                newFileName = ("(" + copyCount + ")").concat(fileName);
            }

            FileOutputStream fos = new FileOutputStream("uploads/" + newFileName);

            addNewClient(clientId);

            ScheduledExecutorService scheduledThreatPool = Executors.newScheduledThreadPool(1);
            Timer timer = new Timer(clientId);
            scheduledThreatPool.scheduleAtFixedRate(timer, 2, 3, TimeUnit.SECONDS);

            int readBytes;
            while (getReceivedBytes(clientId) < fileSize && (readBytes = inputStream.read(buf)) != -1) {
                addBytes(clientId, readBytes);
                fos.write(buf, 0, readBytes);
                timer.addReadBytesCount(readBytes);
            }

            scheduledThreatPool.shutdown();

            log.info("Client(" + clientId + ") " + getTotalData() + " bytes received");
            if (getReceivedBytes(clientId) == fileSize) {
                outputStream.writeUTF("File uploaded successfully\n" + getTotalData() + " bytes sent");
            } else {
                outputStream.writeUTF("File didn't upload\n" + getTotalData() + " bytes sent");
            }

            removeClient(clientId);
            fos.close();
            inputStream.close();
            outputStream.close();
            clientSocket.close();

        } catch (Exception e) {
            log.info("Client(" + clientId + ")" + " File upload error");
            e.printStackTrace();
        }
        log.info("Client" + "(" + clientId + ")" + " disconnected");
    }
}