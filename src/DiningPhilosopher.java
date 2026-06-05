import java.util.Random;
import java.util.concurrent.Semaphore;

//5 philosophers in a round table
// philosopher - contemplate , eat
//  5 forks between them
// each philosopher has to eat without deadlock

//philosopher 
class DiningPhilosopher {
    private final Semaphore[] forks = new Semaphore[5];
    private final Semaphore maxDiners = new Semaphore(4);
    private final Random rand= new Random(System.currentTimeMillis());
    public DiningPhilosopher()
    {
        for(int i = 0; i < 5; i++)
        {
            forks[i] = new Semaphore(1);
        }
    }

    //action eat (eating +done) 
    //contemplate (make it random)

    private void contemplate(int id) throws InterruptedException
    {
        System.out.println("Philosopher: " + id + " is contemplating. Thread: " + Thread.currentThread().getName());
        Thread.sleep(rand.nextInt(50));
    }

    private void eat(int id) throws InterruptedException
    {
        //assume 0th person has0 and 4th fork around
        //0 on left and 4 on right
        boolean enterDining = false;
        boolean leftAcquired = false;
        boolean rightAcquired = false;
        int leftFork = id % forks.length;
        int rightFork = (id + 4) % forks.length;
        try{
            maxDiners.acquire();
            enterDining = true;
            forks[leftFork].acquire();
            leftAcquired = true;
            forks[rightFork].acquire();
            rightAcquired = true;
            System.out.println("Philosopher: " + id + " is eating. Thread: " + Thread.currentThread().getName());
            Thread.sleep(rand.nextInt(50));
        }

        finally {
                
            if(leftAcquired)
            {
                forks[leftFork].release();
            }
            if(rightAcquired)
            {
                forks[rightFork].release();
            }

            if(enterDining)
            {
                maxDiners.release();
            }
        }
    }

    public void lifeCycleOfPhilosophers(int id)
    {
        try {
            while(!Thread.currentThread().isInterrupted())
            {
                contemplate(id);
                eat(id);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }

    public static void runTest() throws InterruptedException
    {
        DiningPhilosopher diningPhil = new DiningPhilosopher();
        Thread[] philosophers = new Thread[5];
        for(int i = 0; i < 5; i++)
        {
            int id = i;
            philosophers[i] = new Thread(() -> {
                diningPhil.lifeCycleOfPhilosophers(id);
            });
        }

        for(int i = 0; i < 5; i++)
        {
            philosophers[i].start();
        }

        Thread.sleep(15000);
        
        for(int i = 0; i < 5; i++)
        {
            philosophers[i].interrupt();
        }

        for(int i = 0; i < 5; i++)
        {
            philosophers[i].join();
        }
        System.out.println("Simulation finished.");

    }

    public static void main(String[] args)
    {
        try{
            runTest();
        }
        catch(InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }



}