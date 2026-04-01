public class ProducerConsumerMain {

    public static void main(String[] args) {

        // Create shared buffer with capacity 3
        ProducerConsumerWithLock<Integer> pc = new ProducerConsumerWithLock<>(3);


        // ------------------------------------------------------------
        // PRODUCER THREAD
        // Keeps producing numbers forever
        // ------------------------------------------------------------
        Thread producer = new Thread(() -> {
            int v = 1;
            try {
                while (true) {
                    pc.produce(v++);      // may block if buffer full
                    Thread.sleep(300);    // simulate work
                }
            } catch (Exception ignored) {}
        });


        // ------------------------------------------------------------
        // CONSUMER THREAD
        // Keeps consuming forever
        // ------------------------------------------------------------
        Thread consumer = new Thread(() -> {
            try {
                while (true) {
                    pc.consume();        // may block if buffer empty
                    Thread.sleep(600);   // simulate processing delay
                }
            } catch (Exception ignored) {}
        });


        // Start both threads
        producer.start();
        consumer.start();
    }
}
