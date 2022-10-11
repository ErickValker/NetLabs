package ru.nsu.g20206.valker.netLab1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static Logger log = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[ConstClass.PORT_VAL_FOR_SERVER]))) {
            log.info("Server started\n");

            ExecutorService threadPool = Executors.newCachedThreadPool();
            int clientId = 0;

            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                threadPool.submit(new ClientManager(socket, clientId++));
            }

            ThreadManager.removeAll();
            threadPool.shutdown();
            log.info("Server closed");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
