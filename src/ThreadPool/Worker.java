/**
 * A worker thread that pulls Runnable tasks from the job queue and executes them.
 * It runs until:
 *   1) shutdown() is called, OR
 *   2) shutdownNow() interrupts it
 */
public class Worker implements Runnable {

    private final BlockingQueueWithShutdown<Runnable> jobQueue;
    private volatile boolean running = true;

    public Worker(BlockingQueueWithShutdown<Runnable> jobQueue) {
        this.jobQueue = jobQueue;
    }

    public void stopWorker() {
        running = false;
    }

    @Override
    public void run() {
        try {
            while (running) {

                // take() blocks if queue empty
                Runnable job = jobQueue.take();

                // If shutdown+queue empty → take() returns null → worker exits
                if (job == null) break;

                try {
                    job.run();   // Execute task outside lock → runs in parallel
                } catch (Exception ignored) {}
            }
        } catch (InterruptedException e) {
            // shutdownNow interrupts workers -> exit gracefully
        }

        System.out.println(Thread.currentThread().getName() + " exiting.");
    }
}

