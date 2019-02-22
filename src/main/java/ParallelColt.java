import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.colt.matrix.tdouble.algo.DenseDoubleAlgebra;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix2D;

import java.util.Random;

public class ParallelColt {
    public static void main(String[] args) {
        //DoubleMatrix2D X = DoubleFactory2D.dense.random(5000, 5000);
        //DoubleMatrix2D Y = DoubleFactory2D.dense.random(5000, 5000);
        int row = 2000;
        int col = 2000;
        int val_1;
        int val_2;

        //Declaring both array's
        DoubleMatrix2D xParallel = new DenseDoubleMatrix2D(row,col);
        DoubleMatrix2D yParallel = new DenseDoubleMatrix2D(row,col);
        DoubleMatrix2D ZParallel = new DenseDoubleMatrix2D(row,col);

        int[][] xSequential = new int[row][col];
        int[][] ySequential = new int[row][col];
        int[][] zSequential = new int[row][col];

        // filling up the array randomly
        Random random = new Random();

        for (int i = 0; i < row; i++)
            for (int j = 0; j < row; j++) {
                val_1 = random.nextInt(1000); //random int from 1 -> 999
                val_2 = random.nextInt(1000);

                xSequential[i][j] = val_1;
                ySequential[i][j] = val_2;

                xParallel.set(i,j,val_1);
                yParallel.set(i,j,val_2);
            }
        System.out.println("---------------------------------------");
        System.out.println("Two Array of dimension: "+row+" * "+col);
        System.out.println("---------------------------------------");

        //calcutate runtime for Colt Lib
        long startTime = System.nanoTime(); //start time

        DenseDoubleAlgebra algebra = new DenseDoubleAlgebra();
        ZParallel = algebra.mult(xParallel, yParallel);

        long timeInNano = System.nanoTime() - startTime; //endtime

        System.out.printf(" Parallel one completed in %8.3f \n", timeInNano / 1e6);



        startTime = System.nanoTime();

        for (int i = 0; i <row; i++) {
            for (int j = 0; j < col; j++) {
                for (int k = 0; k < row; k++) {
                    zSequential[i][j] += xSequential[i][k] * ySequential[k][j];
                }
            }
        }
        timeInNano = System.nanoTime() - startTime;
        System.out.printf(" Sequential One completed in %8.3f \n", timeInNano / 1e6);
    }
}
