public class MySemaphore {

    private int permits;         // how many threads can enter
    private final Object lock = new Object();

    public MySemaphore(int initialPermits) {
        this.permits = initialPermits;
    }

    // ============================
    //     acquire() — blocking
    // ============================
    public void acquire() throws InterruptedException {
        synchronized (lock) {

            // Wait until we have at least 1 permit
            while (permits == 0) {
                lock.wait();         // releases lock and blocks
            }

            permits--;                // consume one permit
        }
    }

    // ============================
    //     release() — unblock waiter
    // ============================
    public void release() {
        synchronized (lock) {
            permits++;                // add permit back

            lock.notify();            // wake ONE waiting thread
        }
    }
}
