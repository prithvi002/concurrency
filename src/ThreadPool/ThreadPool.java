/**
 * A fixed-size thread pool:
 *   - Starts N worker threads
 *   - submit() adds jobs to queue
 *   - workers continuously take jobs and run them
 *   - shutdown() waits for tasks to finish
 *   - shutdownNow() interrupts workers immediately
 */
public class ThreadPool {

    private final Worker[] workers;
    private final Thread[] threads;
    private final BlockingQueueWithShutdown<Runnable> jobQueue;

    private volatile boolean isShutdown = false;

    public ThreadPool(int numThreads, int queueCapacity) {

        // Shared job queue
        this.jobQueue = new BlockingQueueWithShutdown<>(queueCapacity);

        this.workers = new Worker[numThreads];
        this.threads = new Thread[numThreads];

        // Start worker threads
        for (int i = 0; i < numThreads; i++) {
            workers[i] = new Worker(jobQueue);
            threads[i] = new Thread(workers[i], "Worker-" + i);
            threads[i].start();
        }
    }

    /**
     * Submit a task to be executed by the pool.
     */
    public void submit(Runnable task) throws InterruptedException {
        if (isShutdown) {
            throw new IllegalStateException("ThreadPool is shutting down");
        }
        jobQueue.put(task);
    }

    /**
     * Graceful shutdown: workers finish remaining tasks.
     */
    public void shutdown() {
        isShutdown = true;

        // tell queue to stop blocking
        jobQueue.shutdown();

        // stop workers AFTER queue drains
        for (Worker w : workers) {
            w.stopWorker();
        }
    }

    /**
     * Forced shutdown: interrupt all workers immediately.
     */
    public void shutdownNow() {
        isShutdown = true;
        jobQueue.shutdown();

        for (Worker w : workers) {
            w.stopWorker();
        }

        // interrupt sleeping workers
        for (Thread t : threads) {
            t.interrupt();
        }
    }

    /**
     * Block until all worker threads fully exit.
     */
    public void awaitTermination() throws InterruptedException {
        for (Thread t : threads) {
            t.join();
        }
    }
}
