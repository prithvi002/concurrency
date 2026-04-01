
public class Main {
    static ReadWriteLock rw = new ReadWriteLock();
    static int data = 0;

    public static void main(String[] args) {
        // Start readers
        for (int i = 0; i < 3; i++) {
            int id = i;
            new Thread(() -> {
                try {
                    while (true) {
                        rw.lockRead();
                        System.out.println("Reader " + id + " read = " + data);
                        rw.unlockRead();

                        Thread.sleep(200);
                    }
                } catch (Exception e) {}
            }).start();
        }

        // Start one writer
        new Thread(() -> {
            try {
                while (true) {
                    rw.lockWrite();
                    data++;
                    System.out.println("Writer wrote = " + data);
                    rw.unlockWrite();

                    Thread.sleep(500);
                }
            } catch (Exception e) {}
        }).start();
    }
}
