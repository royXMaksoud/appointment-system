import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class test_bcrypt {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Test password "123"
        String hash = "$2a$10$cG1DuKU7s7RgPdEQjp7NOuwMGYT/uGYfKBEgTpWYtPVDEy5aQ2R4W";
        String password = "123";

        boolean matches = encoder.matches(password, hash);
        System.out.println("Password '123' matches hash: " + matches);

        // Also test encoding "123" fresh
        String freshHash = encoder.encode(password);
        System.out.println("Fresh hash for '123': " + freshHash);

        // Test roy
        String royHash = "$2a$10$5lKTt5W1tIv4srqyPKZDC.VobIc.8GMAlQ.1m6LcxzWE6dMpOtrtW";
        boolean royMatches = encoder.matches("roy", royHash);
        System.out.println("Password 'roy' matches roy hash: " + royMatches);
    }
}
