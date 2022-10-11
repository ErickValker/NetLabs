package ru.nsu.g20206.valker.netLab1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

class Client {
    private static Logger log = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(args[ConstClass.IP_ADDRESS_FOR_CLIENT], Integer.parseInt(args[ConstClass.PORT_VAL_FOR_CLIENT]));
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

            File file = new File(args[ConstClass.FILE_INDEX]);
            FileInputStream fis = new FileInputStream(file);

            outputStream.writeUTF(file.getName());
            outputStream.writeLong(file.length());

            byte[] buf = new byte[ConstClass.CHUNK_LENGTH];
            int readBytes;

            while ((readBytes = fis.read(buf)) != -1) {
                outputStream.write(buf, 0, readBytes);
                outputStream.flush();
            }

            log.info(inputStream.readUTF());

            inputStream.close();
            outputStream.close();
            fis.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
