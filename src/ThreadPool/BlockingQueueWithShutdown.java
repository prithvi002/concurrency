import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Thread-safe blocking queue used by the ThreadPool.
 * Workers "take" tasks from here. submit() uses "put".
 * Uses ReentrantLock + Conditions for clean blocking behavior.
 * 
 * Fields:
 * queue
 * capacity
 * volatile isShutdown = false
 * lock(ReentrantLock:true for fair ordering)
 * 
 * Methods:
 * Constructor
 * take
 * put
 * shutdown
 */
public class BlockingQueueWithShutdown<T> {

    private final Queue<T> queue = new LinkedList<>();
    private final int capacity;

    // One lock to protect queue operations
    private final ReentrantLock lock = new ReentrantLock(true);

    // Separate waiting rooms:
    private final Condition notFull = lock.newCondition();  // producers wait here
    private final Condition notEmpty = lock.newCondition(); // workers wait here

    private volatile boolean shutdown = false;

    public BlockingQueueWithShutdown(int capacity) {
        this.capacity = capacity;
    }

    /**
     * Add item to queue. Blocks if full.
     */
    public void put(T item) throws InterruptedException {
        lock.lock();
        try {
            if (shutdown)
                throw new InterruptedException("Queue is shutting down");

            // Block if queue is full
            while (queue.size() == capacity && !shutdown) {
                notFull.await(); // releases lock → sleeps → re-acquires lock on wake
            }

            if (shutdown)
                throw new InterruptedException("Queue is shutting down");

            queue.add(item);

            // Wake one waiting worker
            notEmpty.signal();

        } finally {
            lock.unlock();
        }
    }

    /**
     * Remove and return next item. Blocks if empty.
     * Returns null when shutting down + queue empty → tells worker to exit.
     */
    public T take() throws InterruptedException {
        lock.lock();
        try {
            while (queue.isEmpty() && !shutdown) {
                notEmpty.await();  // worker waits here
            }

            // During shutdown: if empty → worker should exit
            if (shutdown && queue.isEmpty()) {
                return null;
            }

            T item = queue.remove();
            notFull.signal(); // wake any blocked submit() calls
            return item;

        } finally {
            lock.unlock();
        }
    }

    /**
     * Wake all threads waiting in await().
     * Workers will exit after finishing remaining tasks.
     */
    public void shutdown() {
        lock.lock();
        try {
            shutdown = true;

            // wake all producers + consumers
            notFull.signalAll();
            notEmpty.signalAll();

        } finally {
            lock.unlock();
        }
    }
}
