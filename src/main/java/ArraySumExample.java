import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * <h1>Array Sum using java fork join framework</h1>
 * The Array sum using java fork  program implements an application that
 * sum the numbers in array where each number is divided by 3 to increase computation.
 * <p>
 * The purpose of this project is for parallel programming tut, such that java fork join framework exploits
 * the multi core processor where it divide the array (task) into sub-array (sub task) and distribute
 * this task across the cores of processor with the help of work stealing algorithm that discussed early
 * in the section, where we are expecting to solve computation extensive problem in fast way on multi core rather than running on one core
 *</p>
 * @author  Mahmoud Matrawy
 * @version 1.0
 * @since   2018/2/20
 */

public class ArraySumExample extends RecursiveAction {
    double [] arr;
    int low;
    int high;
    double ans = 0;
    static int SIZE = 60_000_000; //size of the array
    static int SeqentialThreshold = SIZE/500;
    ArraySumExample (double[] arr,int low,int high)
    {
        this.arr = arr;
        this.low = low;
        this.high = high;
    }

    /**
     * This is the compute method, it's an interface that you must implement
     * where in this task the decomposition of task and computation of it take place
     * also in this function fork and join call's take place
     * @return Nothing (in case of RecursiveTask there is an return).
     */
    protected void compute()
    {
        // we check size of array(size of task) according to sequential threshold
        // if the condition is true then we do the work directly
        if (high - low <= SeqentialThreshold)
        {
            for (int i=low;i<high;i++)
            {
                ans = ans + arr[i]/3;
            }
        }
        else
        {
            int midPoint = (low+high)/2; //find the midpoint of array
            ArraySumExample left = new ArraySumExample(arr,low,midPoint);
            ArraySumExample right = new ArraySumExample(arr,midPoint,high);

            left.fork(); //fork system call to schedule the subtask(put it in the work queue of current thread)
            right.compute(); // the current thread actually explore the right part
            left.join(); //when the right part is done, wait until the left part
            ans = left.ans + right.ans;  //here we aggregate the sum

            //please check again the section ppt slides if found this function is complex to understand.
            //if you want further understanding please check the fork join white paper(research paper)
        }
    }


    /**
     * This is the seqArraySum method where we do the sequential solve for sum problem
     * where we do here the all computation on the same core
     * @param arr the array we want to compute it's elements
     * @return totallTime it return how much time the traditional way of solving this problem take time.
     */
    public static double seqArraySum(double[] arr)
    {
        long startTime = System.nanoTime();
        double sum = 0;
        for (int i =0;i<arr.length;i++)
        {
            sum += arr[i]/3;
        }
        long totallTime = System.nanoTime()-startTime;
        printResult("Seq",totallTime,sum);
        return totallTime;
    }

    /**
     * This is the parArraySum method where we do the parallel way to solve array sum problem
     * where we do here is we call the fork join frame work for parallel computing
     * @param arr the array we want to compute it's elements
     * @return totallTime it return how much time the parallel computing way of solving this problem take time.
     */
    public static double parArraySum(double[] arr)
    {
        long startTime = System.nanoTime();
        ArraySumExample parallel = new ArraySumExample(arr,0,arr.length);
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(parallel);
        double sum = parallel.ans;
        long totallTime = System.nanoTime() - startTime;
        printResult("Parll",totallTime,sum);
        return totallTime;
    }

    /**
     * This is the printResult method where we print data to console in a good formatted way
     * %8.3f which exsists in printf, the 8 in 8.3f refer to 8 slots for entire number, the .3 in 8.3f refer
     * to number of decimal allowed after the dot, the f in 8.3f refer to float, it's a way to formate the putput
     * @param name which indicate which one is calling this function, the parallel or Sequential sum
     * @param totallTime the time it taken to do the sum operation
     * @param sum the resulting sum
     * @return none
     */
    public  static void printResult(String name,long totallTime,double sum)
    {
        System.out.printf("  %s completed in %8.3f milliseconds with sum = %8.5f \n",name,totallTime/1e6,sum);
    }

    /**
     * This is the randomArray method where we create random array of numbers
     * @return array it return the random array created
     */
    public static double[] randomArray()
    {
        double[] array = new double[SIZE];
        Random random = new Random();
        for (int i=0;i<array.length;i++)
        {
            array[i] = random.nextInt(100);
        }
        return array;
    }


    public static void main(String[] args)
    {
        System.out.println(Runtime.getRuntime().availableProcessors()/2); //output number of threads available
        double [] arr = randomArray();

        double sumSeq = 0;
        double sumParr = 0;
        for (int i=0;i<5;i++) //here we run both parallel and seqential way for 5 times, and at end take the average of the running time
        {
            sumSeq += seqArraySum(arr);
            sumParr += parArraySum(arr);
            System.out.println("------------------------------------------------------------------");
        }
        //calculate the average
        sumSeq = sumSeq / 5;
        sumParr = sumParr /5;

        //speed up define how much the computation is accelerated using the parallel way (multicore), remember we will reach
        // ideal parallelism becasuse we have other programs working with us ^_^ ^_^.
        System.out.println("Speed up  = sequential runtime / parallel runtime");
        System.out.println("Speed up: "+sumSeq/sumParr);

    }

}
