public class SemaphoreTest {

    public static void main(String[] args) {
        MySemaphore sem = new MySemaphore(1);

        Runnable task = () -> {
            String name = Thread.currentThread().getName();
            try {
                System.out.println(name + " waiting for permit...");
                sem.acquire();
                System.out.println(name + " acquired permit!");

                Thread.sleep(1000);  // simulate work

            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println(name + " releasing permit");
                sem.release();
            }
        };

        new Thread(task, "T1").start();
        new Thread(task, "T2").start();
        new Thread(task, "T3").start();
    }
}
