package task2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;

public class FizzBuzzMultithreading {
    private final int n;
    private BlockingQueue<String> queue;
    private ExecutorService executorService;
    private List<NumberProcessor> processorThreads;
    private MessagePrinter messagePrinter;

    public FizzBuzzMultithreading(int n) {
        this.n = n;
        queue = new SynchronousQueue<>();
        executorService = Executors.newFixedThreadPool(4);
        processorThreads = new ArrayList<>();

        // create threads
        processorThreads.add(new NumberProcessor(i -> (i % 3) == 0 ? "fizz" : ""));
        processorThreads.add(new NumberProcessor(i -> (i % 5) == 0 ? "buzz" : ""));
        processorThreads.add(new NumberProcessor(i -> (i % 3) != 0 && (i % 5) != 0 ? i.toString() : ""));
        messagePrinter = new MessagePrinter(queue);
    }

    public void start() throws InterruptedException {
        runThreads();

        for (int i = 1; i <= n; i++) {
            processNumber(i);
            waitForProcessing();
            putResultsToQueue();
            addDelimiter(i);
        }

        shutdownThreads();
    }

    private void processNumber(int i) {
        for (NumberProcessor np : processorThreads) {
            np.setNumber(i);
        }
    }

    private void waitForProcessing() {
        boolean allTested = false;
        while (!allTested) {
            allTested = processorThreads.stream()
                    .allMatch(NumberProcessor::isProcessed);
        }
    }

    private void putResultsToQueue() throws InterruptedException {
        for (NumberProcessor np : processorThreads) {
            queue.put(np.getResult());
        }
    }

    private void addDelimiter(int i) throws InterruptedException {
        if (i != n) {
            queue.put(", ");
        }
    }

    private void runThreads() {
        executorService.execute(messagePrinter);
        for (Thread t : processorThreads) {
            executorService.execute(t);
        }
    }

    private void shutdownThreads() {
        for (NumberProcessor np : processorThreads) {
            np.terminate();
        }
        messagePrinter.terminate();
        executorService.shutdown();

        System.exit(0);
    }
}

