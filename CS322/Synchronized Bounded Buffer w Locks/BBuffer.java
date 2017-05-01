/* Author: Jacob Peterson
 * Synchronized bounded buffer
 * CS 322
 * Description: Use primitive locks to implement await symantics for push and pop.
 * This causes push and pop to be mutually exclusive.
 */

class BBuffer {
  final int SIZE=8;
  int[] buffer;
  int fst;
  int lst;
  int used;

  // Default constructor
  public BBuffer () {
    buffer = new int[SIZE];
    fst = 0;
    lst = 0;
    used = 0;
  }

  volatile boolean lock[] = {false, false};
  volatile int turn;

  //If the other method isn't going and the current function is unlocked then take the lock.
  void csEnter(int proc){
    int other = 1 - proc;
    lock[proc] = true;
    turn = other;
    while(lock[other] && turn == other){}
  }

  //When finished release the lock
  void csExit(int proc){
    lock[proc] = false;
  }

  // Push
  // Add datum to the buffer.
  public void push (int datum) {
    boolean success = false;
    while(!success) {
      csEnter(0);
      if(used < SIZE) { //Buffer won't overlap
        buffer[lst] = datum;
        lst = (lst + 1) % SIZE; //Buffer wraps around
        used++;
        success = true;
      }
      csExit(0);
    }
  }
  // Pop
  // Remove the datum from the buffer.
  public int pop() {
    int datum = 0;
    boolean success = false;
    while(!success) {
      csEnter(1);
      if(used > 0) {
        datum = buffer[fst];
        fst = (fst + 1) % SIZE; //Buffer wraps around
        used--;
        success = true;
      }
      csExit(1);
    }
    return datum;
  }
}
