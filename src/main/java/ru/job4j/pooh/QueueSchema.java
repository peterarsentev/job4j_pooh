package ru.job4j.pooh;

import java.util.concurrent.*;

public class QueueSchema implements Schema {
    private final CopyOnWriteArrayList<Receiver> receivers = new CopyOnWriteArrayList<>();
    private final ConcurrentHashMap<String, BlockingQueue<String>> data = new ConcurrentHashMap<>();
    private final Condition condition = new Condition();

    @Override
    public void addReceiver(Receiver receiver) {
        receivers.add(receiver);
        condition.on();
    }

    @Override
    public void publish(Message message) {
        data.putIfAbsent(message.name(), new LinkedBlockingQueue<>());
        data.get(message.name()).add(message.text());
        condition.on();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            do {
                for (var receiver : receivers) {
                    var queue = data.get(receiver.name());
                    var message = queue.poll();
                    while (message != null) {
                        receiver.receive(
                                message
                        );
                        message = queue.poll();
                    }
                }
                condition.off();
            } while(condition.check());
            try {
                condition.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
