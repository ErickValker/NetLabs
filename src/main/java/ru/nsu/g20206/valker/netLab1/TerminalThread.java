package ru.nsu.g20206.valker.netLab1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TerminalThread implements Runnable {

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                String input = reader.readLine();
                if (ConstClass.EXIT_CODE.equals(input)) {
                    System.exit(ConstClass.PROGRAM_TERMINATED_SUCCESSFULLY);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
