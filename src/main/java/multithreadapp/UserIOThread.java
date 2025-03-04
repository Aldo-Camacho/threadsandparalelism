package multithreadapp;

import java.util.Scanner;

public class UserIOThread implements Runnable{
    private final Scanner scanner = new Scanner(System.in);
    private final String options = "Options:\n" +
            "1.- Input a string\n" +
            "2.- Calculator\n" +
            "3.- Exit\n";
    private boolean close = false;
    @Override
    public void run() {
        while (!close) {
            System.out.println(options);
           int userIn = scanner.nextInt();
           switch (userIn) {
               case 1:
                   continue;
               case 2:
                   continue;
               case 3:
                   close = true;
                   continue;
               default:
                   continue;
           }
        }
    }
}
