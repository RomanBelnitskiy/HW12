import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public class Task2 {
    public static void main(String[] args) {
        FizzBuzzMultithreading fbm = new FizzBuzzMultithreading(100);
        try {
            fbm.start();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

class FizzBuzzMultithreading {
    private final int n;
    private BlockingQueue<String> queue;


    FizzBuzzMultithreading(int n) {
        this.n = n;
        queue = new SynchronousQueue<>();
    }

    public void start() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        // create threads
        List<NumberProcessor> processorThreads = new ArrayList<>();
        processorThreads.add(new NumberProcessor(fizz()));
        processorThreads.add(new NumberProcessor(buzz()));
        processorThreads.add(new NumberProcessor(notFizzBuzz()));
        MessagePrinter messagePrinter = new MessagePrinter(queue);

        // run threads
        executorService.execute(messagePrinter);
        for (Thread t : processorThreads) {
            executorService.execute(t);
        }

        for (int i = 1; i <= n; i++) {
            // process new number
            for (NumberProcessor np : processorThreads) {
                np.setNumber(i);
            }

            // wait for processing
            boolean allTested = false;
            while (!allTested) {
                allTested = processorThreads.stream()
                        .allMatch(NumberProcessor::isProcessed);
            }

            // put results to queue
            for (NumberProcessor np : processorThreads) {
                queue.put(np.getResult());
            }


            if (i != n) {
                queue.put(", ");
            }
        }

        // stop all threads
        for (NumberProcessor np : processorThreads) {
            np.terminate();
        }
        messagePrinter.terminate();
        executorService.shutdown();

        System.exit(0);
    }

    private static Function<Integer, String> fizz() {
        return i -> (i % 3) == 0 ? "fizz" : "";
    }

    private static Function<Integer, String> buzz() {
        return i -> (i % 5) == 0 ? "buzz" : "";
    }

    private static Function<Integer, String> notFizzBuzz() {
        return i -> (i % 3) != 0 && (i % 5) != 0 ? i.toString() : "";
    }
}

class NumberProcessor extends Thread implements Terminable {
    private final Function<Integer, String> function;
    private int n;
    private String result;
    private AtomicBoolean processed;
    private AtomicBoolean terminated;

    public NumberProcessor(Function<Integer, String> function) {
        this.function = function;
        result = "";
        processed = new AtomicBoolean(true);
        terminated = new AtomicBoolean(false);
    }

    public void setNumber(int n) {
        this.n = n;
        processed.set(false);
    }

    public boolean isProcessed() {
        return processed.get();
    }

    public String getResult() {
        return result;
    }

    @Override
    public void run() {
        while (!terminated.get()) {
            if (processed.get()) {
                continue;
            }

            result = function.apply(n);
            processed.set(true);
        }
    }

    @Override
    public void terminate() {
        terminated.set(true);
    }
}

class MessagePrinter extends Thread implements Terminable {
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

interface Terminable {
    void terminate();
}