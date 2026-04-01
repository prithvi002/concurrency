// Implement a Read–Write lock where multiple readers can enter but writers need
// exclusive access. Use synchronized and wait/notify.
public class ReadWriteLock {

    private int activeReaders = 0;     // readers inside
    private int waitingWriters = 0;    // writers waiting
    private boolean writerActive = false;

    private final Object lock = new Object();

    // ================================
    // READ LOCK
    // ================================
    public void lockRead() throws InterruptedException {
        synchronized (lock) {

            // If a writer is active OR a writer is waiting → block readers
            while (writerActive || waitingWriters > 0) {
                lock.wait();
            }

            activeReaders++;
        }
    }

    public void unlockRead() {
        synchronized (lock) {
            activeReaders--;

            // If no readers left → wake writers
            if (activeReaders == 0) {
                lock.notifyAll();
            }
        }
    }

    // ================================
    // WRITE LOCK
    // ================================
    public void lockWrite() throws InterruptedException {
        synchronized (lock) {
            waitingWriters++;

            // If writer active OR readers present → wait
            while (writerActive || activeReaders > 0) {
                lock.wait();
            }

            waitingWriters--;
            writerActive = true;
        }
    }

    public void unlockWrite() {
        synchronized (lock) {
            writerActive = false;

            // Writers get priority
            lock.notifyAll();
        }
    }
}
