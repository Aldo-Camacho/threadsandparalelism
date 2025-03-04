package logins;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadSafeLogin {
    private static final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long expirationTime = 21600000;
    public static String token;
    public static Lock lock = new ReentrantLock();
    public static void login(String username, String password) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);
        try {
            lock.lock();
            token = Jwts.builder()
                    .setSubject(username)
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(secretKey)
                    .compact();
        } finally {
            lock.unlock();
        }
    }
}
