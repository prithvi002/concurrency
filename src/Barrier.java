
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Barrier {
  //barrier all should proceed at once 
  private int count = 0;
  private final int totalThreads;
  private final ReentrantLock lock = new ReentrantLock();
  private final Condition condition = lock.newCondition();
  private int exitingCount = 0;
  public Barrier(int total)
  {
    this.totalThreads = total;
  }


  public void await() throws InterruptedException
  {
    lock.lock();
    try {
      while(count == totalThreads)
      {
        condition.await();
      }
      count++;

      if(count == totalThreads)
      {
        condition.signalAll();
        exitingCount = 0;
      }
      else{
        while(count < totalThreads)
        {
          condition.await();
        }
      }

      exitingCount++;

      if(exitingCount == totalThreads)
      {
        count = 0;
        exitingCount = 0;
        condition.signalAll(); // allow next batch to enter
      }

        
    } finally {
        lock.unlock();
    }
  }


public static void runTest() throws InterruptedException {
        final Barrier barrier = new Barrier(3);

        Thread p1 = new Thread(new Runnable() {
            public void run() {
                try {
                    System.out.println("Thread 1");
                    barrier.await();
                    System.out.println("Thread 1");
                    barrier.await();
                    System.out.println("Thread 1");
                    barrier.await();
                } catch (InterruptedException ie) {
                }
            }
        });

        Thread p2 = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(500);
                    System.out.println("Thread 2");
                    barrier.await();
                    Thread.sleep(500);
                    System.out.println("Thread 2");
                    barrier.await();
                    Thread.sleep(500);
                    System.out.println("Thread 2");
                    barrier.await();
                } catch (InterruptedException ie) {
                }
            }
        });

        Thread p3 = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(1500);
                    System.out.println("Thread 3");
                    barrier.await();
                    Thread.sleep(1500);
                    System.out.println("Thread 3");
                    barrier.await();
                    Thread.sleep(1500);
                    System.out.println("Thread 3");
                    barrier.await();
                } catch (InterruptedException ie) {
                }
            }
        });

        p1.start();
        p2.start();
        p3.start();

        p1.join();
        p2.join();
        p3.join();
    }

    public static void main( String args[] ) throws Exception{
        Barrier.runTest();
    }
}
