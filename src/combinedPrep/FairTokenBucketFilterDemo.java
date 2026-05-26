import java.util.LinkedList;
import java.util.Queue;

public class FairTokenBucketFilterDemo {

    static class TokenBucketFilter {

        // max bucket capacity
        private final int MAX_TOKENS;

        // current available tokens
        private long possibleTokens = 0;

        // last accounted refill timestamp
        private long lastRefillTime = System.currentTimeMillis();

        // FIFO waiting queue
        private final Queue<Thread> waitingQueue =
                new LinkedList<>();

        public TokenBucketFilter(int maxTokens) {
            this.MAX_TOKENS = maxTokens;
        }

        public synchronized void getToken()
                throws InterruptedException {

            Thread currentThread =
                    Thread.currentThread();

            // join FIFO queue
            waitingQueue.add(currentThread);

            while (true) {

                long now =
                        System.currentTimeMillis();

                long elapsed =
                        now - lastRefillTime;

                // generate tokens lazily
                long newTokens = elapsed / 1000;

                // only update if tokens generated
                if (newTokens > 0) {

                    possibleTokens =
                            Math.min(
                                    MAX_TOKENS,
                                    possibleTokens + newTokens
                            );

                    // preserve remaining milliseconds
                    lastRefillTime +=
                            newTokens * 1000;
                }

                // only FIFO head can consume token
                if (waitingQueue.peek() ==
                        currentThread
                        && possibleTokens > 0) {

                    possibleTokens--;

                    waitingQueue.poll();

                    System.out.println(
                            "Granted token to "
                                    + currentThread.getName()
                                    + " at second "
                                    + (System.currentTimeMillis()
                                    / 1000)
                    );

                    notifyAll();

                    return;
                }

                // wait until token may become available
                wait(1000);
            }
        }
    }

    public static void main(String[] args)
            throws Exception {

        TokenBucketFilter tokenBucket =
                new TokenBucketFilter(3);

        // allow bucket to fill initially
        Thread.sleep(5000);

        // create 10 threads
        for (int i = 1; i <= 10; i++) {

            Thread t = new Thread(() -> {

                try {
                    tokenBucket.getToken();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            });

            t.setName("Thread-" + i);

            t.start();
        }
    }
}