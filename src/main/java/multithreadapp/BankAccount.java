package multithreadapp;

public class BankAccount {
    private double balance = 0;

    public BankAccount(double balance) {
        this.balance = balance;
    }

    public double withdraw(double quantity) {
        this.balance -= quantity;
        return this.balance;
    }

    public double deposit(double quantity) {
        this.balance += quantity;
        return this.balance;
    }
}
