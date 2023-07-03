package ru.job4j.pooh;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

public class TextReceiver implements Receiver {
    private final CountDownLatch count;
    private final CopyOnWriteArrayList<String> out;
    private final String name;

    public TextReceiver(CountDownLatch count, String name, CopyOnWriteArrayList<String> out) {
        this.count = count;
        this.out = out;
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public void receive(String text) {
        out.add(text);
        count.countDown();
    }
}
