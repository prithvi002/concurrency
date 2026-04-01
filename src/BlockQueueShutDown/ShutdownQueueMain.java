public class ShutdownQueueMain {

    // -------------------------- //
    //         PRODUCER           //
    // -------------------------- //
    static class Producer implements Runnable {

        private volatile boolean running = true;   // flag to stop the loop
        private final BlockingQueueWithShutdown<Integer> queue;
        private int value;

        Producer(BlockingQueueWithShutdown<Integer> q, int start) {
            this.queue = q;
            this.value = start;
        }

        // Stop request for this producer
        public void stop() {
            running = false;
        }

        public void run() {
            try {
                // Producer loop runs until running == false
                while (running) {
                    queue.enqueue(value++);  // may block if full
                    Thread.sleep(300);       // simulate work
                }
            } catch (InterruptedException e) {
                System.out.println("Producer interrupted (shutdown).");
            }

            System.out.println("Producer exiting.");
        }
    }


    // -------------------------- //
    //         CONSUMER           //
    // -------------------------- //
    static class Consumer implements Runnable {

        private volatile boolean running = true;
        private final BlockingQueueWithShutdown<Integer> queue;

        Consumer(BlockingQueueWithShutdown<Integer> q) {
            this.queue = q;
        }

        public void stop() {
            running = false;
        }

        public void run() {
            try {
                // Consumer loop
                while (running) {
                    queue.dequeue();      // may block if empty
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                System.out.println("Consumer interrupted (shutdown).");
            }

            System.out.println("Consumer exiting.");
        }
    }


    // -------------------------- //
    //           MAIN             //
    // -------------------------- //
    public static void main(String[] args) throws Exception {

        BlockingQueueWithShutdown<Integer> queue =
                new BlockingQueueWithShutdown<>(3);

        // Create producers and consumers
        Producer p1 = new Producer(queue, 1);
        Producer p2 = new Producer(queue, 100);
        Consumer c1 = new Consumer(queue);
        Consumer c2 = new Consumer(queue);

        Thread t1 = new Thread(p1);
        Thread t2 = new Thread(p2);
        Thread t3 = new Thread(c1);
        Thread t4 = new Thread(c2);

        t1.start();
        t2.start();
        t3.start();
        t4.start();

        // Let system run for 5 seconds
        Thread.sleep(5000);

        System.out.println("=== Initiating SHUTDOWN ===");

        // Ask threads to stop looping
        p1.stop();
        p2.stop();
        c1.stop();
        c2.stop();

        // Shutdown queue (wake all blocked threads)
        queue.shutdown();

        // Wake consumers if blocked on dequeue
        t3.interrupt();
        t4.interrupt();

        // Wait for all threads to exit
        t1.join();
        t2.join();
        t3.join();
        t4.join();

        System.out.println("All threads exited cleanly.");
    }
}
