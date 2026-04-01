import java.util.ArrayDeque;
import java.util.Deque;

public class BlockingStack<T> {

    private final Deque<T> deque = new ArrayDeque<>();
    private final int capacity;
    private final Object lock = new Object();

    public BlockingStack(int capacity) {
        this.capacity = capacity;
    }

    // ============================
    // PUSH (blocks if full)
    // ============================
    public void push(T item) throws InterruptedException {
        synchronized (lock) {

            // Block while full
            while (deque.size() == capacity) {
                lock.wait();     // releases lock → sleeps → re-acquires on wake
            }

            deque.addLast(item);  // push to top

            // Wake a blocked pop() if any
            lock.notifyAll();
        }
    }

    // ============================
    // POP (blocks if empty)
    // ============================
    public T pop() throws InterruptedException {
        synchronized (lock) {

            // Block while empty
            while (deque.isEmpty()) {
                lock.wait();
            }

            T item = deque.removeLast(); // pop from top

            // Wake a blocked push() if any
            lock.notifyAll();

            return item;
        }
    }
}
