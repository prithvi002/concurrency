public class Solution {

    private final Object lock = new Object();

    public void methodA() {
        System.out.println(Thread.currentThread().getName() + " trying to enter methodA");
        synchronized (lock) {
            System.out.println(Thread.currentThread().getName() + " entered methodA");
            sleep(5000); // hold the lock for 5 seconds
            System.out.println(Thread.currentThread().getName() + " leaving methodA");
        }
    }

    public void methodB() {
        System.out.println(Thread.currentThread().getName() + " trying to enter methodB");
        synchronized (lock) {
            System.out.println(Thread.currentThread().getName() + " entered methodB");
            sleep(5000);
            System.out.println(Thread.currentThread().getName() + " leaving methodB");
        }
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (Exception ignored) {}
    }

    public static void main(String[] args) {

        Solution obj = new Solution();

        Thread t1 = new Thread(obj::methodA, "T1");
        Thread t2 = new Thread(obj::methodB, "T2");

        t1.start();
        t2.start();
    }
}
