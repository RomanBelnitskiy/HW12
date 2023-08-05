package task2;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class MessagePrinter extends Thread implements Terminable {
    private final BlockingQueue<String> queue;
    private AtomicBoolean terminated;

    public MessagePrinter(BlockingQueue<String> queue) {
        this.queue = Objects.requireNonNull(queue);
        terminated = new AtomicBoolean(false);
    }

    @Override
    public void run() {
        while (!terminated.get() || !queue.isEmpty()) {
            try {
                System.out.print(queue.take());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void terminate() {
        terminated.set(true);
    }
}

