import java.util.HashSet;
import java.util.Set;

public class TokenBucketFilterUsingDaemonThread {

    public static void main(String[] args) throws InterruptedException {

        Set<Thread> allThreads = new HashSet<>();

        TokenBucketFilter tokenBucketFilter =
                TokenBucketFilterFactory.makeTokenBucketFilter(1);

        // create 10 threads requesting tokens
        for (int i = 0; i < 10; i++) {

            Thread thread = new Thread(() -> {
                try {
                    tokenBucketFilter.getToken();
                } catch (InterruptedException e) {
                    System.out.println("Problem occurred");
                }
            });

            thread.setName("Thread_" + (i + 1));
            allThreads.add(thread);
        }

        // start all threads
        for (Thread t : allThreads) {
            t.start();
        }

        // wait for all threads to complete
        for (Thread t : allThreads) {
            t.join();
        }
    }
}

// abstract API exposed to users
abstract class TokenBucketFilter {

    abstract void getToken() throws InterruptedException;
}

// factory responsible for object creation + daemon thread startup
class TokenBucketFilterFactory {

    // hidden implementation
    private static class MultithreadedTokenBucketFilter
            extends TokenBucketFilter {

        private long possibleTokens = 0;

        private final int MAX_TOKENS;

        private static final int ONE_SECOND = 1000;

        private MultithreadedTokenBucketFilter(int maxTokens) {
            this.MAX_TOKENS = maxTokens;
        }

        // background daemon thread continuously adds tokens
        private void daemonThread() {

            while (true) {

                synchronized (this) {

                    // add token if bucket not full
                    if (possibleTokens < MAX_TOKENS) {
                        possibleTokens++;
                    }

                    // wake one waiting thread
                    this.notify();
                }

                try {
                    Thread.sleep(ONE_SECOND);
                } catch (InterruptedException e) {
                    // ignore interruption
                }
            }
        }

        @Override
        void getToken() throws InterruptedException {

            synchronized (this) {

                // wait until token becomes available
                while (possibleTokens == 0) {
                    this.wait();
                }

                // consume token
                possibleTokens--;
            }

            System.out.println(
                    "Granting " +
                    Thread.currentThread().getName() +
                    " token at " +
                    System.currentTimeMillis() / 1000
            );
        }
    }

    // factory method
    public static TokenBucketFilter makeTokenBucketFilter(int capacity) {

        MultithreadedTokenBucketFilter tokenBucketFilter =
                new MultithreadedTokenBucketFilter(capacity);

        // start daemon thread AFTER object fully constructed
        Thread daemonThread = new Thread(() -> {
            tokenBucketFilter.daemonThread();
        });

        daemonThread.setDaemon(true);

        daemonThread.start();

        return tokenBucketFilter;
    }
}