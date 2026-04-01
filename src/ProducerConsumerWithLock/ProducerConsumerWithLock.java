import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ProducerConsumerWithLock<T> {

    // Shared buffer between producers & consumers
    private final Queue<T> buffer = new LinkedList<>();

    // Maximum number of items allowed
    private final int capacity;

    // Fair lock: true = FIFO lock acquisition → avoids starvation
    private final ReentrantLock lock = new ReentrantLock(true);

    // Condition for "buffer is NOT full" → used by producers
    private final Condition lockFull = lock.newCondition();

    // Condition for "buffer is NOT empty" → used by consumers
    private final Condition lockEmpty = lock.newCondition();

    public ProducerConsumerWithLock(int capacity) {
        this.capacity = capacity;
    }


    // ============================================================
    // PRODUCE (add item to buffer)
    // ============================================================
    public void produce(T item) throws InterruptedException {

        lock.lock();   // 🔒 Acquire the lock before touching buffer
        try {

            // Buffer full → producer must WAIT
            while (buffer.size() == capacity) {
                System.out.println("Producer waiting → buffer FULL");

                // await():
                // 1. Releases lock
                // 2. Sleeps
                // 3. Wakes when signaled
                lockFull.await();
            }

            // Safe to add item now
            buffer.add(item);
            System.out.println("Produced: " + item);

            // Signal ONE waiting consumer (lockEmpty condition)
            lockEmpty.signal();

        } finally {
            lock.unlock();  // 🔓 Always release lock
        }
    }


    // ============================================================
    // CONSUME (remove item from buffer)
    // ============================================================
    public T consume() throws InterruptedException {

        lock.lock();   // 🔒 Acquire the lock
        try {

            // Buffer empty → consumer must WAIT
            while (buffer.isEmpty()) {
                System.out.println("Consumer waiting → buffer EMPTY");

                // await releases lock & puts thread to sleep
                lockEmpty.await();
            }

            // Safe to consume
            T item = buffer.remove();
            System.out.println("Consumed: " + item);

            // Signal ONE waiting producer (lockFull condition)
            lockFull.signal();

            return item;

        } finally {
            lock.unlock();  // 🔓 Release lock
        }
    }
}
