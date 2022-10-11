package ru.nsu.g20206.valker.netLab1;

import java.util.HashMap;

public class ThreadManager {
    private final static HashMap<Integer, Long> threadManager = new HashMap<>();

    public static void addNewClient(Integer id) {
        synchronized (threadManager) {
            threadManager.put(id, 0L);
        }
    }

    public static long getReceivedBytes(int id) {
        synchronized (threadManager) {
            return threadManager.get(id);
        }
    }

    public static void addBytes(int id, int newBytes) {
        synchronized (threadManager) {
            threadManager.put(id, threadManager.get(id) + newBytes);
        }
    }

    public static void removeClient(int id) {
        synchronized (threadManager) {
            threadManager.remove(id);
        }
    }

    public static void removeAll() {
        synchronized (threadManager) {
            threadManager.clear();
        }
    }

}
