package task2;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public class NumberProcessor extends Thread implements Terminable {
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

