import java.util.LinkedList;
import java.util.Queue;

/**
 * A bounded blocking queue that also supports graceful shutdown.
 * Producers and consumers block when full/empty,
 * and shutdown() wakes them up so they don't get stuck forever.
 */
public class BlockingQueueWithShutdown<T> {

    private final int capacity;          // Max items queue can hold
    private final Queue<T> buffer;       // Internal storage for items
    private final Object lock = new Object();   // Single lock for all sync

    // NEW: indicates if shutdown has been requested
    private volatile boolean isShutdown = false;

    public BlockingQueueWithShutdown(int cap) {
        this.capacity = cap;
        this.buffer = new LinkedList<>();
    }

    /**
     * Initiate shutdown of the queue.
     * - Set shutdown flag
     * - Wake up ALL threads so they can exit instead of being stuck in wait()
     */
    public void shutdown() {
        synchronized (lock) {
            isShutdown = true;       // tell everyone we are shutting down
            lock.notifyAll();        // wake all producers & consumers
        }
    }


    /**
     * Producer adds an item.
     * Blocks if the queue is full.
     * During shutdown, throws InterruptedException.
     */
    public void enqueue(T item) throws InterruptedException {
        synchronized (lock) {

            // If queue is full, producer must wait.
            // BUT if shutdown happens, stop waiting.
            while (buffer.size() == capacity && !isShutdown) {
                System.out.println("Queue full → producer waiting");
                lock.wait();         // releases lock and sleeps
            }

            // After waking up, check if shutdown occurred
            if (isShutdown) {
                System.out.println("enqueue aborted due to shutdown");
                throw new InterruptedException("Queue shutting down");
            }

            // Safe to add the item
            buffer.add(item);
            System.out.println("Enqueued: " + item);

            // Wake up consumers that might be waiting on empty
            lock.notifyAll();
        }
    }


    /**
     * Consumer removes an item.
     * Blocks if the queue is empty.
     * During shutdown, stops if empty.
     */
    public T dequeue() throws InterruptedException {
        synchronized (lock) {

            // If empty, wait unless shutting down
            while (buffer.isEmpty() && !isShutdown) {
                System.out.println("Queue empty → consumer waiting");
                lock.wait();
            }

            // If shutting down AND empty, no more work will arrive
            if (isShutdown && buffer.isEmpty()) {
                System.out.println("dequeue aborted due to shutdown");
                throw new InterruptedException("Queue shutting down");
            }

            // Safe to remove item
            T item = buffer.remove();
            System.out.println("Dequeued: " + item);

            // Wake up producers waiting on full
            lock.notifyAll();
            return item;
        }
    }
}
