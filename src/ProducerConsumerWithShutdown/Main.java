public class Main {

    public static void main(String[] args) throws Exception {

        ProducerConsumerWithShutdown<Integer> queue =
                new ProducerConsumerWithShutdown<>(3);

        // Create threads
        Producer p1 = new Producer(queue, 1);
        Producer p2 = new Producer(queue, 100);
        Consumer c1 = new Consumer(queue);
        Consumer c2 = new Consumer(queue);

        Thread t1 = new Thread(p1);
        Thread t2 = new Thread(p2);
        Thread t3 = new Thread(c1);
        Thread t4 = new Thread(c2);

        // Start them
        t1.start();
        t2.start();
        t3.start();
        t4.start();

        // Let the system run for a while
        Thread.sleep(4000);

        System.out.println("=== INITIATING SHUTDOWN ===");

        // Stop the loops
        p1.stopRunning();
        p2.stopRunning();
        c1.stopRunning();
        c2.stopRunning();

        // Stop queue blocking
        queue.shutdown();

        // Interrupt threads sleeping inside await()
        t1.interrupt();
        t2.interrupt();
        t3.interrupt();
        t4.interrupt();

        // Wait for all to finish
        t1.join();
        t2.join();
        t3.join();
        t4.join();

        System.out.println("=== ALL THREADS EXITED CLEANLY ===");
    }
}
