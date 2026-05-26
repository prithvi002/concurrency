import java.util.LinkedList;
import java.util.Queue;

public class BoundedBlockingQueue<T> {

    private final int capacity;
    private final Queue<T> buffer;
    private final Object lock = new Object();

    public BoundedBlockingQueue(int cap) {
        this.capacity = cap;
        this.buffer = new LinkedList<>();
    }
    
    public void enqueue(T item) throws InterruptedException {
        synchronized(lock) {
            while (buffer.size() == capacity) {
                System.out.println("Queue full → producer waiting");
                lock.wait();
            }

            buffer.add(item);
            System.out.println("Enqueued: " + item);

            lock.notifyAll(); // signal consumers
        }
    }

    public T dequeue() throws InterruptedException {
        synchronized(lock) {
            while (buffer.isEmpty()) {
                System.out.println("Queue empty → consumer waiting");
                lock.wait();
            }

            T item = buffer.remove();
            System.out.println("Dequeued: " + item);

            lock.notifyAll(); // signal producers
            return item;
        }
    }
}
