/**
 * Test the ThreadPool implementation.
 */
public class Main {
    public static void main(String[] args) throws Exception {

        ThreadPool pool = new ThreadPool(3, 10);

        // Submit 15 tasks
        for (int i = 1; i <= 15; i++) {
            int jobId = i;
            pool.submit(() -> {
                System.out.println("Running job " + jobId +
                        " on " + Thread.currentThread().getName());
                try { Thread.sleep(400); } catch (Exception ignored) {}
            });
        }

        // Let workers finish some tasks
        Thread.sleep(2000);

        System.out.println("=== Graceful shutdown ===");
        pool.shutdown();

        // Wait for workers to stop
        pool.awaitTermination();

        System.out.println("=== All workers exited ===");
    }
}
