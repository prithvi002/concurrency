import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class CyclicBarrier {
    private final int parties;
    private int count;
    private int generation;

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition trip = lock.newCondition();

    public CyclicBarrier(int parties) {
        if (parties <= 0) {
            throw new IllegalArgumentException("parties must be > 0");
        }
        this.parties = parties;
        this.count = parties;
        this.generation = 0;
    }

    public void await() throws InterruptedException {
        lock.lock();
        try {
            int gen = generation;

            count--;

            if (count == 0) {
                // Last thread to arrive: advance generation and reset.
                generation++;
                count = parties;
                trip.signalAll();
                return;
            }

            // Other threads wait until generation changes.
            while (gen == generation) {
                trip.await(); // can throw InterruptedException
            }
        } finally {
            lock.unlock();
        }
    }

    public int getParties() {
        return parties;
    }
}
