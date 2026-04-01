import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ProducerConsumerWithShutdown<T> {

    private final Queue<T> buffer = new LinkedList<>();
    private final int capacity;

    // Fair lock = FIFO ordering
    private final ReentrantLock lock = new ReentrantLock(true);

    // Separate waiting rooms
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    // Shutdown flag for the queue itself
    private volatile boolean shutdown = false;

    public ProducerConsumerWithShutdown(int capacity) {
        this.capacity = capacity;
    }


    // =====================================================
    // PRODUCE (blocks if full)
    // =====================================================
    public void produce(T item) throws InterruptedException {
        lock.lock();
        try {
            // If shutting down → stop immediately
            if (shutdown) throw new InterruptedException("Queue shutting down");

            // If full, go wait in notFull condition queue
            while (buffer.size() == capacity && !shutdown) {
                System.out.println("Producer waiting → FULL");
                notFull.await(); // releases lock while waiting
            }

            // If woke up because shutdown
            if (shutdown) throw new InterruptedException("Queue shutting down");

            buffer.add(item);
            System.out.println("Produced: " + item);

            // Wake ONE consumer waiting for notEmpty
            notEmpty.signal();

        } finally {
            lock.unlock();
        }
    }


    // =====================================================
    // CONSUME (blocks if empty)
    // =====================================================
    public T consume() throws InterruptedException {
        lock.lock();
        try {
            // If shutting down → stop immediately
            if (shutdown) throw new InterruptedException("Queue shutting down");

            // If empty, consumer waits
            while (buffer.isEmpty() && !shutdown) {
                System.out.println("Consumer waiting → EMPTY");
                notEmpty.await(); // releases lock while waiting
            }

            if (shutdown) throw new InterruptedException("Queue shutting down");

            T item = buffer.remove();
            System.out.println("Consumed: " + item);

            // Wake ONE producer waiting on notFull
            notFull.signal();

            return item;

        } finally {
            lock.unlock();
        }
    }


    // =====================================================
    // SHUTDOWN — wakes ALL threads waiting on await()
    // =====================================================
    public void shutdown() {
        lock.lock();
        try {
            shutdown = true;

            // Wake ALL producers & consumers blocked on await()
            notFull.signalAll();
            notEmpty.signalAll();

        } finally {
            lock.unlock();
        }
    }
}
