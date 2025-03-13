package multithreadapp;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class MultiThreadedApp {
    private static final Runnable[] threads = new Runnable[18];
    private static final Map<Long, BankAccount> bankAccounts = new HashMap<>();
    private static final Map<Long, ThreadSafeBankAccount> threadSafeBankAccounts = new HashMap<>();
    private static final Random random = new Random();

    static {
        long id = 0;
        for (int i = 0; i < 10; i++) {
            double balance = random.nextDouble() * 1000;
            BankAccount account = new BankAccount(balance);
            bankAccounts.put(id, account);
            ThreadSafeBankAccount safeBankAccount = new ThreadSafeBankAccount(balance);
            threadSafeBankAccounts.put(id, safeBankAccount);
            id++;
        }
    }

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(threads.length);
        System.out.println("Unsafe ------------------");
        for (int i = 0; i < threads.length; i++) {
            Thread thread = new Thread(() -> {
                ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
                long id = threadLocalRandom.nextLong(10);
                BankAccount bankAccount =  bankAccounts.get(id);
                System.out.println("Account: " + id + " Has balance: " + bankAccount.deposit(100));
            });
            executor.submit(thread);
        }
        try {
            executor.awaitTermination(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        }

        System.out.println("Safe ------------------");
        for (int i = 0; i < threads.length; i++) {
            Thread thread = new Thread(() -> {
                ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
                long id = threadLocalRandom.nextLong(10);
                ThreadSafeBankAccount bankAccount =  threadSafeBankAccounts.get(id);
                while (!bankAccount.lock.tryLock()) {
                }
                try {
                    System.out.println("Account: " + id + " Has balance: " + bankAccount.deposit(100));
                } finally {
                    bankAccount.lock.unlock();
                }
            });
            executor.submit(thread);
        }
        try {
            executor.awaitTermination(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        }
    }
}
