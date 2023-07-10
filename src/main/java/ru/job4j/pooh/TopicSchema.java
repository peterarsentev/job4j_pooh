package ru.job4j.pooh;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class TopicSchema implements Schema {

    private final CopyOnWriteArrayList<Receiver> receivers = new CopyOnWriteArrayList<>();
    private final BlockingQueue<Message> queue = new LinkedBlockingQueue<>();
    private final Condition condition = new Condition();

    @Override
    public void addReceiver(Receiver receiver) {
        receivers.add(receiver);
    }

    @Override
    public void publish(Message message) {
        queue.add(message);
        condition.on();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            do {
                while (!queue.isEmpty()) {
                    var message = queue.poll();
                    for (var receiver : receivers) {
                        if (message.name().equals(receiver.name())) {
                            receiver.receive(message.text());
                        }
                    }
                }
                condition.off();
            } while (condition.check());
            try {
                condition.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
