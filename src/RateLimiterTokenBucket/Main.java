public class Main {
    public static void main(String[] args) throws Exception {

        // capacity = 5 tokens, refill = 5 tokens per second
        RateLimiterTokenBucket limiter = new RateLimiterTokenBucket(5, 5);

        // Try 15 requests
        for (int i = 1; i <= 15; i++) {
            boolean allowed = limiter.allowRequest();
            System.out.println("Request " + i + " allowed = " + allowed);

            Thread.sleep(100); // 100ms delay between calls
        }
    }
}
