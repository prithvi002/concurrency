public class MyCountDownLatch {

    // Number of events/tasks that must complete before threads can proceed
    private int count;

    // Single monitor object for synchronization
    private final Object lock = new Object();

    public MyCountDownLatch(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("count cannot be negative");
        }
        this.count = count;
    }

    /**
     * Wait until count reaches 0.
     * If count is already 0, returns immediately.
     */
    public void await() throws InterruptedException {
        synchronized (lock) {
            // While there are still pending events, wait
            while (count > 0) {
                lock.wait();  // releases lock, sleeps, re-acquires on wake
            }
            // when count == 0, return
        }
    }

    /**
     * Decrement count by 1.
     * When count reaches 0, wake up all waiting threads.
     */
    public void countDown() {
        synchronized (lock) {
            if (count == 0) {
                // extra countDowns are no-op (like java.util.concurrent.CountDownLatch)
                return;
            }

            count--;

            // If this was the last one → wake all awaiters
            if (count == 0) {
                lock.notifyAll();
            }
        }
    }

    /**
     * Optional helper to inspect current count (not required, but handy for debugging).
     */
    public int getCount() {
        synchronized (lock) {
            return count;
        }
    }
}
