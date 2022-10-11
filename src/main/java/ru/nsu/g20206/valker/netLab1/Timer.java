package ru.nsu.g20206.valker.netLab1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Timer implements Runnable {
    private static Logger log = LoggerFactory.getLogger(Timer.class);

    private long startTime;
    private long checkpointTime;
    private long readBytesTotalCount;
    private long checkpointBytesCount;
    private int currentSpeed;
    private int sessionSpeed;
    private long readBytesCount;
    private int clientId;

    Timer(int clientId) {
        this.checkpointTime = 0;
        this.checkpointBytesCount = 0;
        this.readBytesTotalCount = 0;
        this.startTime = System.currentTimeMillis();
        this.checkpointTime = this.startTime;
        this.readBytesCount = 0;
        this.clientId = clientId;
    }

    @Override
    public void run() {
        checkSpeed();
        printSpeed();
    }

    private void checkSpeed() {
        readBytesTotalCount = readBytesCount;
        long currentTime = System.currentTimeMillis();
        calculateSpeed(currentTime);
    }

    private void calculateSpeed(long currentTime) {
        currentSpeed = (int) ((readBytesTotalCount - checkpointBytesCount) / (currentTime - checkpointTime));
        sessionSpeed = (int) (readBytesTotalCount / (currentTime - startTime));

        this.checkpointTime = currentTime;
        this.checkpointBytesCount = readBytesTotalCount;
    }

    private void printSpeed() {
        log.info("\n''''''''''''''''''''''''''''''''''''''''\"" +
                "\n Client(" + clientId + ") Current Speed:\t" + currentSpeed + " KB/Sec" +
                "\n Client(" + clientId + ") Session Speed:\t" + sessionSpeed + " KB/Sec" +
                "\n''''''''''''''''''''''''''''''''''''''''");
    }

    public void addReadBytesCount(long readBytesCount) {
        this.readBytesCount += readBytesCount;
    }
}