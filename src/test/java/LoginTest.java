import logins.Login;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class LoginTest {

    @Test
    public void testRaceCondition() throws InterruptedException {
        int numberOfThreads = 100;
        List<String> tokens = new ArrayList<>();
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < numberOfThreads; i++) {
            int finalI = i;
            Thread t = new Thread(() -> {
                Login.login("user" + finalI,"password123");
                tokens.add(Login.token);
            });
            threads.add(t);
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }
        Set<String> uniqueTokens = new HashSet<>();
        for (String token: tokens) {
            System.out.println(token);
            uniqueTokens.add(token);
        }
        Assertions.assertEquals(tokens.size(), uniqueTokens.size());
    }
}