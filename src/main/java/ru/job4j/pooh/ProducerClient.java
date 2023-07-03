package ru.job4j.pooh;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ProducerClient {
    public static void main(String[] args) throws Exception {
        var socket = new Socket("127.0.0.1", 9000);
        var cli = new Scanner(System.in);
        try (var out = new PrintWriter(socket.getOutputStream());
             var input = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            int count = 0;
            while (true) {
                out.println("queue;weather;text " + count++);
                out.flush();
            }
        }
    }
}
