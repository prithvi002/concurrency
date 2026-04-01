public class SimpleFutureDemo {

    public static void main(String[] args) throws InterruptedException {
        SimpleFuture<String> future = new SimpleFuture<>();

        // Worker thread: does some work, then sets result
        Thread worker = new Thread(() -> {
            try {
                System.out.println("Worker: doing some work...");
                Thread.sleep(1000); // simulate work
                future.setResult("Work result from worker thread");
                System.out.println("Worker: result set");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        worker.start();

        // Main thread: waits for the result
        System.out.println("Main: waiting for result...");
        String value = future.get(); // blocks until worker calls setResult
        System.out.println("Main: got result -> " + value);
    }
}
