package multithreadapp;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadSafeBankAccount extends BankAccount {
    public final Lock lock = new ReentrantLock();

    public ThreadSafeBankAccount(double balance) {
        super(balance);
    }
}
