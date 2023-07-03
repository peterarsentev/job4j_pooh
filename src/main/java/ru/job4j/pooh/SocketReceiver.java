package ru.job4j.pooh;

import java.io.PrintWriter;

public class SocketReceiver implements Receiver {
    private final String name;
    private final PrintWriter out;

    public SocketReceiver(String name, PrintWriter out) {
        this.name = name;
        this.out = out;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public void receive(String text) {
        out.println(text);
        out.flush();
    }
}
