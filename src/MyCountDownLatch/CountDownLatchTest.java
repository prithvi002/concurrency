public class CountDownLatchTest {

    public static void main(String[] args) throws Exception {
        int workers = 3;
        MyCountDownLatch latch = new MyCountDownLatch(workers);

        Runnable task = () -> {
            String name = Thread.currentThread().getName();
            try {
                System.out.println(name + " doing work...");
                Thread.sleep(1000);  // <-- will get interrupted here if we call interrupt()

                System.out.println(name + " finished, calling countDown()");
                latch.countDown();

            } catch (InterruptedException e) {
                StackTraceElement ste = e.getStackTrace()[0];
                System.out.println(
                    name + " was interrupted at " +
                    ste.getFileName() + ":" + ste.getLineNumber()
                );
            }
        };

        Thread t1 = new Thread(task, "Worker-1");
        Thread t2 = new Thread(task, "Worker-2");
        Thread t3 = new Thread(task, "Worker-3");

        t1.start();
        t2.start();
        t3.start();

        // Let them run for a bit
        Thread.sleep(2000);

        // 🔥 INTERRUPT WORKER-2 HERE
        System.out.println("Interrupting Worker-2 now!");
        t2.interrupt();

        System.out.println("Main waiting on latch...");
        latch.await();

        System.out.println("All workers done, main continues.");
    }
}
