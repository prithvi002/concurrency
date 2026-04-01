public class App {

    public static void main(String[] args) {

        BoundedBlockingQueue<Integer> queue = new BoundedBlockingQueue<>(3);

        // Producer 1
        Thread   = new Thread(() -> {
            int value = 1;
            try {
                while (true) {
                    queue.enqueue(value++);
                    Thread.sleep(300);
                }
            } catch (InterruptedException e) { }
        });

        // Producer 2
        Thread producer2 = new Thread(() -> {
            int value = 100;
            try {
                while (true) {
                    queue.enqueue(value++);
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) { }
        });

        // Consumer 1
        Thread consumer1 = new Thread(() -> {
            try {
                while (true) {
                    queue.dequeue();
                    Thread.sleep(600);
                }
            } catch (InterruptedException e) { }
        });

        // Consumer 2
        Thread consumer2 = new Thread(() -> {
            try {
                while (true) {
                    queue.dequeue();
                    Thread.sleep(700);
                }
            } catch (InterruptedException e) { }
        });

        producer1.start();
        producer2.start();
        consumer1.start();
        consumer2.start();
    }
}
