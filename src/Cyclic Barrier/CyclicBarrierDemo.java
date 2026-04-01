public class CyclicBarrierDemo {

    public static void main(String[] args) throws Exception {
        int parties = 3;
        int rounds = 5;

        CyclicBarrier barrier = new CyclicBarrier(parties);

        for (int i = 0; i < parties; i++) {
            final int id = i;

            Thread t = new Thread(() -> {
                try {
                    for (int r = 0; r < rounds; r++) {
                        System.out.println("Thread " + id + " reached barrier, round " + r);
                        barrier.await();
                        System.out.println("Thread " + id + " passed barrier, round " + r);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

            t.start();
        }
    }
}
