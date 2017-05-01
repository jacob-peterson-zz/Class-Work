/* Author: Jacob Peterson
 * q4 - Bounded buffer with semaphores
 * CS322 - HW2
 * Description: Allows multiple threads to push and pop data onto a buffer.
 */
import java.util.concurrent.Semaphore;

// Bounded Buffer
class BBuffer {
  final int SIZE=8;
  int[] buffer;
  int fst;
  int lst;
  private Semaphore count = new Semaphore(SIZE, true); //Has a permit for each spot in the buffer
  private Semaphore mutex = new Semaphore(1);

  // Default constructor
  public BBuffer () {
    buffer = new int[SIZE];
    fst = 0;
    lst = 0;
  }

  // Push
  // Add datum to the buffer.
  public void push (int datum) throws InterruptedException {
    while(count.availablePermits() == 0){} //Wait until there are availablePermits
      mutex.acquire(); //Mutual exclusion
      buffer[lst] = datum;
      count.acquire(); //Take one of the buffer permits
      lst = (lst + 1) % SIZE;
      mutex.release();
    }

    // Pop
    // Remove the datum from the buffer.
    public int pop() throws InterruptedException {
      int datum = 0;
      while(count.availablePermits() == 8){} //Wait until there is something on the buffer
        mutex.acquire(); //Mutual exclusion
        datum = buffer[fst];
        fst = (fst + 1) % SIZE;
        count.release(); // Release the buffer permit
        mutex.release();
        return datum;
      }
    }
