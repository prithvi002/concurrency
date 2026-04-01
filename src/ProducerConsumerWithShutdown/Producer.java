public class Producer implements Runnable {
    private final ProducerConsumerWithShutdown<Integer> pc;
    private volatile boolean running = true;
    private final int startValue;

    public Producer(ProducerConsumerWithShutdown<Integer> pc, int startValue) {
        this.pc = pc;
        this.startValue = startValue;
    }

    public void stopRunning() {
        running = false;
    }

    @Override
    public void run() {
        int val = startValue;
        try {
            while (running) {
                pc.produce(val++);
                Thread.sleep(300);  // simulate work
            }
        } catch (InterruptedException e) {
            // Thread was interrupted during shutdown
        }
        System.out.println("Producer stopped.");
    }
}
