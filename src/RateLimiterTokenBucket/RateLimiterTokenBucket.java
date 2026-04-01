public class RateLimiterTokenBucket {

    private final int capacity;       // Max possible tokens
    private final double refillRate;  // Tokens added per second

    private double tokens;            // Current token count
    private long lastRefillTimestamp; // Last refill time (ms)

    public RateLimiterTokenBucket(int capacity, double refillRatePerSecond) {
        this.capacity = capacity;
        this.refillRate = refillRatePerSecond;
        this.tokens = capacity;               // Start full
        this.lastRefillTimestamp = System.currentTimeMillis();
    }

    /**
     * Multi-threaded safe rate limiter.
     * Returns true if request is allowed, false if rate limit exceeded.
     */
    public synchronized boolean allowRequest() {

        long now = System.currentTimeMillis();

        // Step 1: compute how many seconds passed.
        double secondsPassed = (now - lastRefillTimestamp) / 1000.0;

        // Step 2: compute tokens to add based on elapsed time.
        double tokensToAdd = secondsPassed * refillRate;

        // Step 3: refill lazily when needed.
        if (tokensToAdd > 0) {
            tokens = Math.min(capacity, tokens + tokensToAdd);
            lastRefillTimestamp = now;
        }

        // Step 4: allow request ONLY if we have at least 1 token.
        if (tokens >= 1) {
            tokens -= 1;
            return true;
        }

        return false; // Rate limit exceeded
    }
}
