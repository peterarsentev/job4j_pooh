package ru.job4j.pooh;

public class Condition {
    private volatile boolean flag = false;

    synchronized void on() {
        flag = true;
        notify();
    }

    synchronized void off() {
        flag = false;
        notify();
    }

    synchronized void await() throws InterruptedException {
        while (!flag) {
            wait();
        }
    }

    boolean check() {
        return flag;
    }
}
