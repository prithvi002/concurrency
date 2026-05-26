package combinedPrep;

public class BoundedQueue {
  private int[] arr;
  private int head;
  private int tail;
  private int size;
  private int capacity;

  public BoundedQueue(int capacity){
    this.capacity = capacity;
    this.head = 0;
    this.tail = 0;
    this.size= 0;
    arr = new int[capacity];
  }


  public synchronized void enque(int val) throws InterruptedException
  {
    while(size == capacity)
    {
      wait();
    }

    if(tail == capacity)
    {
      tail = 0;
    }
    arr[tail] = val;
    tail++;
    size++;
    notifyAll();
  }

  public synchronized int dequeue() throws InterruptedException
  {
    while(size == 0)
    {
      wait();
    }

    if(head == capacity)
    {
      head = 0;
    }
    int val = arr[head];
    arr[head] = 0;//in case of objects null
    head++;
    size--;
    notifyAll();
    return val;
  }
}
