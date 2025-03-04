import algebra.Matrix;

import static org.junit.jupiter.api.Assertions.*;

class MatrixTest {

    @org.junit.jupiter.api.Test
    void benchmarkAddOneMillElements() {
        int rows = 1000;
        int cols = 1000;
        Matrix a = Matrix.getRandom(rows, cols);
        Matrix b = Matrix.getRandom(rows, cols);

        long start = System.currentTimeMillis();
        Matrix c = a.addBasic(b);
        long end = System.currentTimeMillis();
        System.out.println("Execution time for sequential algorithm: " + (end - start));
        start = System.currentTimeMillis();
        Matrix d = a.addParallelStream(b);
        end = System.currentTimeMillis();
        System.out.println("Execution time for parallel stream algorithm: " + (end - start));
        start = System.currentTimeMillis();
        Matrix e = a.addParallelRecursive(b);
        end = System.currentTimeMillis();
        System.out.println("Execution time for parallel recursive algorithm: " + (end - start));
        start = System.currentTimeMillis();
        Matrix f = a.addParallelChunked(b);
        end = System.currentTimeMillis();
        System.out.println("Execution time for parallel chunked algorithm: " + (end - start));
        assertTrue(Matrix.equals(c, d));
        assertTrue(Matrix.equals(c, e));
        assertTrue(Matrix.equals(c, f));
    }

    @org.junit.jupiter.api.Test
    void benchmarkMultiplyOneMillElementsOptimal() {
        int nChunks = Runtime.getRuntime().availableProcessors();
        int rows = 1000;
        int columns = 1000;

        Matrix a = Matrix.getRandom(rows, columns);
        Matrix b = Matrix.getRandom(columns, rows);

        long start = System.currentTimeMillis();
        Matrix c = a.multiplyBasic(b);
        long end = System.currentTimeMillis();
        System.out.println("Execution time for sequential algorithm: " + (end - start));
        start = System.currentTimeMillis();
        Matrix d = a.multiplyParallelChunked(b, nChunks);
        end = System.currentTimeMillis();
        System.out.println("Execution time for parallel chunked algorithm: " + (end - start));
        assertTrue(Matrix.equals(c, d));

        start = System.currentTimeMillis();
        Matrix e = b.multiplyBasic(a);
        end = System.currentTimeMillis();
        System.out.println("Execution time for sequential algorithm: " + (end - start));
        start = System.currentTimeMillis();
        Matrix f = b.multiplyParallelChunked(a, nChunks);
        end = System.currentTimeMillis();
        System.out.println("Execution time for parallel chunked algorithm: " + (end - start));
        assertTrue(Matrix.equals(e, f));
    }

    @org.junit.jupiter.api.Test
    void benchmarkMultiplyOneMillElements100Threads() {
        int nChunks = 100;
        int rows = 1000;
        int columns = 1000;

        Matrix a = Matrix.getRandom(rows, columns);
        Matrix b = Matrix.getRandom(columns, rows);

        long start = System.currentTimeMillis();
        Matrix c = a.multiplyBasic(b);
        long end = System.currentTimeMillis();
        System.out.println("Execution time for sequential algorithm: " + (end - start));
        start = System.currentTimeMillis();
        Matrix d = a.multiplyParallelChunked(b, nChunks);
        end = System.currentTimeMillis();
        System.out.println("Execution time for parallel chunked algorithm: " + (end - start));
        assertTrue(Matrix.equals(c, d));

        start = System.currentTimeMillis();
        Matrix e = b.multiplyBasic(a);
        end = System.currentTimeMillis();
        System.out.println("Execution time for sequential algorithm: " + (end - start));
        start = System.currentTimeMillis();
        Matrix f = b.multiplyParallelChunked(a, nChunks);
        end = System.currentTimeMillis();
        System.out.println("Execution time for parallel chunked algorithm: " + (end - start));
        assertTrue(Matrix.equals(e, f));
    }

    @org.junit.jupiter.api.Test
    void benchmarkMultiplyOneMillElements4Threads() {
        int nChunks = 4;
        int rows = 1000;
        int columns = 1000;

        Matrix a = Matrix.getRandom(rows, columns);
        Matrix b = Matrix.getRandom(columns, rows);

        long start = System.currentTimeMillis();
        Matrix c = a.multiplyBasic(b);
        long end = System.currentTimeMillis();
        System.out.println("Execution time for sequential algorithm: " + (end - start));
        start = System.currentTimeMillis();
        Matrix d = a.multiplyParallelChunked(b, nChunks);
        end = System.currentTimeMillis();
        System.out.println("Execution time for parallel chunked algorithm: " + (end - start));
        assertTrue(Matrix.equals(c, d));

        start = System.currentTimeMillis();
        Matrix e = b.multiplyBasic(a);
        end = System.currentTimeMillis();
        System.out.println("Execution time for sequential algorithm: " + (end - start));
        start = System.currentTimeMillis();
        Matrix f = b.multiplyParallelChunked(a, nChunks);
        end = System.currentTimeMillis();
        System.out.println("Execution time for parallel chunked algorithm: " + (end - start));
        assertTrue(Matrix.equals(e, f));
    }
}