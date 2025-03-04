package multithreadapp;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CalculatorThread implements Runnable {
    ReentrantReadWriteLock queueLock = new ReentrantReadWriteLock();
    Queue<Operation> operations = new ConcurrentLinkedQueue<>();
    public List<Operation> solved = new ArrayList<>();

    @Override
    public void run() {
        while (true) {
            if (!operations.isEmpty()) {
                Operation current = operations.poll();
                current.solve();
                solved.add(current);
            }
        }
    }

    public static final class Operation {
        public enum Type {
            SUM,
            MULT,
            DIV
        }

        Double a;
        Double b;
        Double solution;
        Type type;

        public void solve() {
            switch (type) {
                case SUM:
                    solution = a + b;
                    return;
                case DIV:
                    solution = a / b;
                    return;
                case MULT:
                    solution = a * b;
                    return;
                default:
            }
        }
    }
}
