package ru.job4j.pooh;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.*;

class QueueSchemaTest {

    @Test
    public void whenTwoReceivers() throws InterruptedException {
        var queue = new QueueSchema();
        var result = new CopyOnWriteArrayList<String>();
        var count = new CountDownLatch(1);
        queue.addReceiver(new TextReceiver(new CountDownLatch(0), "cars", result));
        queue.addReceiver(new TextReceiver(count, "weather", result));
        queue.publish(new Message("weather", "18"));
        var thread = new Thread(queue);
        thread.start();
        count.await();
        thread.interrupt();
        assertThat(result).contains("18");
    }


    @Test
    public void whenSingleReceiver() throws InterruptedException {
        var queue = new QueueSchema();
        var result = new CopyOnWriteArrayList<String>();
        var count = new CountDownLatch(1);
        queue.addReceiver(new TextReceiver(count, "weather", result));
        queue.publish(new Message("weather", "18"));
        var thread = new Thread(queue);
        thread.start();
        count.await();
        thread.interrupt();
        assertThat(result).contains("18");
    }

    @Test
    public void whenWithoutMessage() throws InterruptedException {
        var queue = new QueueSchema();
        var result = new CopyOnWriteArrayList<String>();
        queue.addReceiver(new TextReceiver(new CountDownLatch(1), "weather", result));
        var thread = new Thread(queue);
        thread.start();
        thread.interrupt();
        thread.join();
        assertThat(result).isEmpty();
    }

    @Test
    public void whenReceiverOnlyByWeather() throws InterruptedException {
        var queue = new QueueSchema();
        var result = new CopyOnWriteArrayList<String>();
        var count = new CountDownLatch(1);
        queue.addReceiver(new TextReceiver(count, "weather", result));
        queue.publish(new Message("weather", "18"));
        queue.publish(new Message("city", "Moskow"));
        var thread = new Thread(queue);
        thread.start();
        count.await();
        thread.interrupt();
        assertThat(result).contains("18");
    }

    @Test
    @Timeout(1)
    public void whenReceiverMultiMessageByWeather() throws InterruptedException {
        var queue = new QueueSchema();
        var result = new CopyOnWriteArrayList<String>();
        var count = new CountDownLatch(2);
        queue.addReceiver(new TextReceiver(count, "weather", result));
        queue.publish(new Message("weather", "18"));
        queue.publish(new Message("weather", "20"));
        var thread = new Thread(queue);
        thread.start();
        count.await();
        thread.interrupt();
        assertThat(result).contains("18", "20");
    }

    @Test
    public void whenMultiReceivers() throws InterruptedException {
        var queue = new QueueSchema();
        var result = new CopyOnWriteArrayList<String>();
        var count = new CountDownLatch(2);
        queue.publish(new Message("weather", "23"));
        queue.publish(new Message("weather", "20"));
        queue.publish(new Message("city", "11"));
        queue.addReceiver(new TextReceiver(count, "weather", result));
        queue.addReceiver(new TextReceiver(count, "weather", result));
        var thread = new Thread(queue);
        thread.start();
        count.await();
        thread.interrupt();
        assertThat(result).containsOnly("23", "20");
    }

    @Test
    public void whenLoadBalance() throws InterruptedException {
        var queue = new QueueSchema();
        var firstOut = new CopyOnWriteArrayList<String>();
        var secondOut = new CopyOnWriteArrayList<String>();
        var count = new CountDownLatch(2);
        queue.publish(new Message("weather", "23"));
        queue.publish(new Message("weather", "20"));
        queue.publish(new Message("city", "11"));
        queue.addReceiver(new TextReceiver(count, "weather", firstOut));
        queue.addReceiver(new TextReceiver(count, "weather", secondOut));
        var thread = new Thread(queue);
        thread.start();
        count.await();
        thread.interrupt();
        assertThat(firstOut.size()).isEqualTo(1);
        assertThat(firstOut.iterator().next()).isIn("23", "20");
        assertThat(secondOut.size()).isEqualTo(1);
        assertThat(secondOut.iterator().next()).isIn("23", "20");
    }
}