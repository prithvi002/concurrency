public class SimpleFuture<T> {

    private T result;
    private volatile boolean isDone = false;
    private final Object lock = new Object();
    // Called by the worker thread to complete the future
    public void setResult(T value) {
      synchronized(lock){
                if (isDone) {
            // For simplicity, ignore or throw; here I'll just ignore second calls.
            return;
        }
        this.result = value;
        this.isDone = true;
        // Wake up any threads waiting in get()
        lock.notifyAll();
      } 
    }

    // Called by the main thread (or any consumer) to wait for the result
    public T get() throws InterruptedException {
      synchronized(lock){
        while (!isDone) {
            // Wait until someone calls setResult()
            System.out.println("get Method: Waiting for result...");
            lock.wait();
        }
        return result;
      }
    }
}
