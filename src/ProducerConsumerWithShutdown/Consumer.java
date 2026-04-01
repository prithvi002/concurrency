public class Consumer implements Runnable {
    private final ProducerConsumerWithShutdown<Integer> pc;
    private volatile boolean running = true;

    public Consumer(ProducerConsumerWithShutdown<Integer> pc) {
        this.pc = pc;
    }

    public void stopRunning() {
        running = false;
    }

    @Override
    public void run() {
        try {
            while (running) {
                pc.consume();
                Thread.sleep(600);  // simulate processing
            }
        } catch (InterruptedException e) {
            // Shutdown wakeup
        }
        System.out.println("Consumer stopped.");
    }
}
