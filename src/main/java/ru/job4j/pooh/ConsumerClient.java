package ru.job4j.pooh;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ConsumerClient {
    public static void main(String[] args) throws Exception {
        var socket = new Socket("127.0.0.1", 9000);
        try (var out = new PrintWriter(socket.getOutputStream());
             var input = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out.println("intro;queue;weather");
            out.flush();
            while (true) {
                var text = input.readLine();
                System.out.println(text);
            }
        }
    }
}
