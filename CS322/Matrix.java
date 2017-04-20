/* 
 * Jacob Peterson
 * Parallelize matrix multiplication
 * CS 322
 * Description: This program times the multiplication of a 4x4 matrix, LEFT, by a 4x1000
 * matrix, RIGHT.  The result is a 4x1000 matrix.  If the answer is
 * correct, then the time is reported.
 */

class Matrix {

  public static void main(String argv[]) throws InterruptedException {
    //Get the number of threads from the command line
    int NOTH = Integer.parseInt(argv[0]);
    int[][] result = new int[4][1000];
    long start = System.nanoTime();

    //CO [t = 0 to 4]
    //Create the amount of threads given in the args
    //Split the 1000 columns among the threads
    Thread[] thds = new Thread[NOTH];
    for (int t = 0; t < NOTH; ++t) {
        final int T = t;
            //Create a runnable class that creates and runs threads 
			thds[t] = new Thread (
                 new Runnable() {
                      public void run() {
       			 		   for (int i = 0; i < 4; ++i) {
								//Split the columns over the amount of threads
                        	    //Never touch same data
								for(int j = (T*1000)/NOTH; j < ((T+1)*1000)/NOTH; ++j) {
                        			result[i][j] = 0;
                                	for(int k = 0; k < 4; ++k) {
                                        result[i][j] += LEFT[i][k]*RIGHT[k][j];
                                	}
                                }
                       		}
                	   }
    			  }
             );
         thds[t].start();    //Start the threads
     }
     //OC
     //Combine the threads
     for(int j = 0; j < NOTH; ++j) {
         thds[j].join();
     }

     long end = System.nanoTime();

     if(verify(result)) {
      	System.out.printf("time = %d\n", end - start);
      	System.out.printf("Good job!\n");
     }
  }

  /* 
   * Verify that the result matrix is the correct answer.
   */
	private static boolean verify(int[][] result) {
    	boolean error = false;
    	for(int i = 0; !error && i < 4; ++i) {
      		for(int j = 0; !error && j < 1000; ++j)i {
        		if(ANSWER[i][j] != result[i][j]) {
          			System.err.printf("error at [%d,%d]\n", i, j);
          			error = true;
        		}
      		}
    	}
    	return !error;
  	}

  
  /* 
   * Initialize Matrices
   * Most 3D rendering uses a 4x4 matrix transformation matrix.  LEFT is a simple
   * translation matrix.   The translation matrix defines camera location, zoom,
   * etc.
   */
  private static final int[][] LEFT = new int [][]{{2, 3,  4, 0},
                                                   {1, -1, 0, 0},
                                                   {3, 2,  1, 0},
                                                   {0, 0,  0, 1}};
  /* The RIGHT matrix is really a collection of column vectors.  Each column
   * defines one point in the scene.
   * NOTE: this array is 4x1000
   */
  private static final int[][] RIGHT = new int[][] {
    {-3, 5,  -4, 5, 4,  -6, 2, -6, -7, 1, 7,  4,  8,  3,  0,  -1, 10, 4,  -3, -4, 10, -2, 2,  9,  -4, -4, 8,  -3, -3, 1,  -2, -8, -8, -8, 9,  5,  -1, -2, 4,  1,  -4, 1,  6,  3,  -5, -8, 3,  -4, 1,  1,  1,  7,  4,  -5, 3,  0,  4,  -6, 8,  10, 2,  9,  3,  -6, 6,  7,  2, -3, 7, 1,  1,  -7, 8,  3,  -3, -4, -3, -2, 7,  -5, 6,  1,  -8, 9, 4,  -2, 9,  -6, -4, -4, 9,  -6, 4,  2,  -1, -1, 8,  3,  1,  -1, -4, 7,  -6, -2, 10, 10, -8, 5,  6,  7,  1, -7, -7, 7, -2, 5,  9,  -3, -5, 9,  7,  10, 1,  -7, 1,  8,  -5, 1,  -7, -3, 1,  8,  1,  8,  10, 3,  10, 6,  5,  -6, -6, 4, 0,  6,  0, 8,  -4, 7,  10, 4,  6,  -6, 5,  8,  -7, 4,  -6, 8,  -3, 9,  -5, -6, 0,  2,  5,  -3, 6,  3,  3,  7,  -6, 8,  3,  1, 4,  -1, 4,  6,  7, -5, -4, 4,  -2, 6,  -7, 2,  1,  -6, -7, -2, -8, -8, 1, 3,  5,  1,  8,  4,  -3, -4, 8, -8, -8, 1,  -6, -8, 3,  -2, 6,  -8, -8, 1, 2,  10, 2,  8,  1, 9,  5, 2,  -4, -1, 1,  9,  -4, -4, 3,  -5, -4, -7, -4, -7, -2, 8,  1,  7, 1,  -2, -4, -5, 0,  3,  0,  -3, -4, 7,  5,  7,  -4, 0,  10, 7,  -8, 5,  -8, 0,  0,  -7, -7, -5, -2, -6, -6, 0,  -8, -3, 1,  -8, -3, -4, 5,  9, -7, -8, 1,  4,  -8, 8,  -7, -7, 1,  -7, -4, 1,  -1, 5,  -5, 9,  -6, 2,  -5, 2,  0,  -3, 8,  -2, 4,  9,  -3, -4, -2, 1,  -2, 2,  2,  8, -4, 6,  4,  2, 1, 8,  3,  2,  -6, 5,  9,  1, 1,  5,  -4, -1, -8, -1, -1, -1, -3, -4, 9,  -7, -8, 10, -7, 4,  1,  -3, 4,  3,  10, 8,  -2, -1, 0,  0,  1,  -3, 2,  -8, 0,  0,  10, 4, 2,  -1, 0,  -2, -2, -2, 0,  -1, 4,  1,  2,  -2, -2, 1,  6,  1,  -1, 1, 1,  -1, -5, 8,  1,  -6, 6, 8, -3, -1, -3, 0,  -2, 5,  0,  1,  -3, 6,  2,  8,  -3, 7,  -4, 1,  -1, -5, -2, 9, -4, -7, 2,  3,  -6, -3, 5, 7,  10, -4, 5,  1,  2, 6,  -2, 1,  -3, 3,  8,  8,  7,  1,  -7, 0,  8,  -4, 6,  -5, 3, 7,  2,  -7, -1, -2, 2,  3,  1, -1, 8,  0,  2,  -1, 0,  -8, 9, -6, 4,  2, -8, -4, -3, 7,  -5, 8,  -8, 9,  9,  6,  1,  4, 6,  10, 5,  -3, -5, 1,  7,  5,  -6, 4,  5,  1,  7, -6, 8,  -8, 2,  1,  6,  9,  5,  -2, -3, -4, 7,  -7, 1, -7, -4, 0,  -8, -4, -6, -6, 3,  1, 2,  1,  0,  -2, 9, 10, -4, -7, -1, -6, 3, 5, 0,  3,  6,  9,  6,  8,  -1, -2, 3,  -8, 9, -5, -6, 7,  2,  -5, 4,  -4, -1, -6, 5,  7,  4,  5,  9,  -6, 1,  -5, 0, 5,  8,  -8, 5,  -2, 10, 2,  -5, 2, 10, -5, 6,  7,  3,  5,  -8, 7,  -1, 2,  3,  3,  9,  -1, -8, 9,  5,  -7, -8, 1,  -1, 5,  -1, 9,  9,  -7, 1,  -4, 1,  4,  2,  1,  0,  9,  -7, 5, 2, 1,  2, 6,  -1, -4, 9,  -1, 7,  0,  1,  0,  -6, -2, 3, 7,  7,  9,  5, 9,  -1, 1,  3,  1,  2,  6,  7,  3,  2,  3, -7, 6, 9,  -4, 4,  3,  -2, -2, 10, 0,  5,  9,  1,  1,  1,  3,  3, 9,  10, -7, 4,  7,  0,  6, -3, 9,  -7, -3, 8,  5,  1, 0,  8,  -1, 0,  2,  6,  -1, 0,  -8, -1, -7, -2, 7,  -1, -2, 10, 2,  -4, -6, 8,  5,  -8, 2,  1,  3,  -5, -1, 6,  -3, 1, 9,  -5, 3, -5, -6, -4, -5, 5,  2,  0,  0, 2,  3,  4,  -2, 2,  -1, -6, -4, -5, 10, -7, 10, 8,  10, -3, 6,  2,  0,  -5, 2, 0,  -3, 1, 2,  1,  5, -5, 0,  2,  -7, -8, 1, -1, 6,  0,  -5, 0,  5,  7,  -5, 6, -4, -8, -7, -7, -5, 1, -8, 3,  -2, 1, 4,  7,  2, 7,  4, 9,  1,  2,  -2, 1, -8, 8, -2, 10, 3,  6,  8,  6,  -7, 10, -7, -3, 10, 7,  8,  -2, 5, 1,  5, -4, 6,  3, -6, 1, 7,  -4, 1, -3, -8, -4, -2, -4, -6, 8,  1,  -8, 2,  -1, -3, -3, 10, 1, 9,  -6, -4, 2,  5,  -3, 7,  5,  1,  3,  0,  6,  -3, -8, 1,  2,  -5, -8, 7,  5,  7,  -7, 3,  1,  8,  -7, -6, 1,  -4, -3, 7,  -7, -8, -6, -5, 9,  4,  -8, -1, 6,  0,  7,  -4, 2,  -8, 4,  9,  2,  10, 9,  8,  7,  3,  -8, 9,  -1, 1, -2, 5,  1,  1,  -3, 4,  3,  -5, -8, 10, 7,  -8, 5,  7,  3,  2,  3,  -8, 1,  -6, 7, 5,  10, 8,  1,  1,  -6, -1, -5, 5,  4,  -1, 10, 1,  1,  -7, 7,  -3, -5, 4,  -6, 1,  4,  -6, 8,  -1, 7,  -3, 8,  8,  8,  -4, 4,  -7, -6, 3,  -7, -6, 4,  7,  5,  -8, 5,  -5, 10, -8, -6, 3,  5,  1,  6, 1,  8,  -1, 5,  -4, 2,  0,  1,  0,  1,  -4, 7,  7,  0,  -6, 1,  10, 10, 8,  -8, 10, 6,  7,  -8, 1, 4,  4,  3,  2,  -1, -8, -1, -6, 4,  7,  -3, 10, 0,  -3, 6, -3, 3,  8,  1,  2,  -7, 10, 5,  10, -4, 10, -6, 1,  -6, -5, -5, 4,  10, 5,  3,  -6, -2, 8,  -6, -1, 1,  -3, 1, 10, 10, 0,  3,  -6, -2, 5,  7,  -1, -8, 4,  -2, -2, 0,  1,  -1, 2, -4, -4},
