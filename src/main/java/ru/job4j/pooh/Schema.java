package ru.job4j.pooh;

public interface Schema extends Runnable {
    void addReceiver(Receiver receiver);

    void publish(Message message);
}
