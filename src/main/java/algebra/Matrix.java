package algebra;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Matrix {
    public static final int SEQUENTIAL_THRESHOLD = 1000;
    private  int rows;
    private int columns;
    private List<Double> values;
    public Matrix(int rows, int columns) {
        this(rows, columns, new ArrayList<>(rows * columns));
    }

    public Matrix(int rows, int columns, List<Double> values) {
        this.rows = rows;
        this.columns = columns;
        this.values = values;
    }

    public int getSize() {
        return rows * columns;
    }

    public List<Double> getValues() {
        return values;
    }
    
    private int getIndex(int row, int column) {
        assert  row < rows && column < columns;
        return row * columns + column;
    }
    
    public Double getElement(int row, int column) {
        return values.get(getIndex(row, column));
    }
    
    public void setElement(int row, int column, Double value) {
        this.values.set(getIndex(row, column), value);
    }
    
    public Matrix multiplyBasic(Matrix b) {
        assert this.columns == b.rows;
        
        Matrix c = Matrix.zeroes(this.rows, b.columns);
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < b.columns; j++) {
                for (int k = 0; k < this.columns; k++) {
                    c.setElement(i,j, c.getElement(i,j) + this.getElement(i, k) * b.getElement(k, j));
                }
            }
        }
        return c;
    }

    public Matrix multiplyParallelChunked(Matrix b, int nChunks) {
        assert this.columns == b.rows;
        int rowChunkSize = this.rows / nChunks + 1;
        int iterations = this.rows / rowChunkSize ;

        ExecutorService executor = Executors.newFixedThreadPool(nChunks);
        Matrix c = Matrix.zeroes(this.rows, b.columns);
        for (int i = 0; i < iterations; i++) {
            int chunkStartRow = i * rowChunkSize;
            int chunkEndRow = chunkStartRow + rowChunkSize;
            if (chunkEndRow > this.rows) chunkEndRow = this.rows;

            MatrixMultiplicationTask task = new MatrixMultiplicationTask(chunkStartRow, chunkEndRow, 0, b.columns, this, b, c);
            executor.submit(task);
        }
        executor.shutdown();
        while (!executor.isTerminated()) {

        }
        return c;
    }

    public Matrix addBasic(Matrix b) {
        assert this.columns == b.columns && this.rows == b.rows;
        Matrix c = new Matrix(this.rows, b.columns);
        for(int i = 0; i < getSize(); i++) {
            c.values.add(i, this.values.get(i) + b.values.get(i));
        }
        return c;
    }

    public Matrix addParallelStream(Matrix b) {
        assert this.columns == b.columns && this.rows == b.rows;

        List<Double> values = IntStream.range(0, this.getSize())
                .parallel()
                .mapToDouble(i -> this.values.get(i) + b.values.get(i))
                .boxed()
                .collect(Collectors.toList());

        return new Matrix(this.rows, this.columns, values);
    }

    public Matrix addParallelRecursive(Matrix b) {
        assert this.columns == b.columns && this.rows == b.rows;

        int sequentialThreshold = this.getSize() / Runtime.getRuntime().availableProcessors();

        RecursiveMatrixSum sum = new RecursiveMatrixSum(0, this.getSize(), 2 * sequentialThreshold, this.values, b.values, new ArrayList<>(Collections.nCopies(this.getSize(), 0.0)));
        sum.compute();
        return new Matrix(this.rows, this.columns, sum.getOutput());
    }

    public Matrix addParallelChunked(Matrix b) {
        assert this.columns == b.columns && this.rows == b.rows;
        int chunkSize = (this.getSize() + 1) / Runtime.getRuntime().availableProcessors();
        int threads = this.getSize() / chunkSize;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        Matrix c = Matrix.zeroes(this.rows, this.columns);
        for (int i = 0; i < threads; i++) {
            int start = i * chunkSize;
            int end = Math.min(start + chunkSize, this.getSize());
            Thread thread = new Thread(() -> {
                for (int ii = start; ii < end; ii++) {
                   c.values.set(ii, this.values.get(ii) + b.values.get(ii));
                }
            });
            executor.submit(thread);
        }
        try {
            executor.awaitTermination(1, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
        }
        return c;
    }

    public static Matrix zeroes(int rows, int columns) {
        return new Matrix(rows, columns, new ArrayList<>(Collections.nCopies(rows * columns, 0.0)));
    }
    public static Matrix getRandom(int rows, int columns) {
        Random rand = new Random();
        List<Double> values = IntStream.range(0, rows * columns)
                .parallel()
                .mapToDouble(i -> rand.nextDouble())
                .boxed()
                .collect(Collectors.toList());

        return new Matrix(rows, columns, values);
    }

    public static boolean equals(Matrix a, Matrix b){
        if (a == b) {
            return true;
        } else if (a == null || b == null) {
            return false;
        } else if (a.rows != b.rows || a.columns != b.columns) {
            return false;
        } else {
            return IntStream.range(0, a.columns)
                    .parallel()
                    .allMatch(i -> a.values.get(i).doubleValue() == b.values.get(i).doubleValue());
        }
    }

    private static class RecursiveMatrixSum extends RecursiveAction {

        private final int sequentialThreshold;
        private final int startInclusive;
        private final int endExclusive;
        private final List<Double> inputA;
        private final List<Double> inputB;
        private final List<Double> output;
        public RecursiveMatrixSum(int startInclusive, int endExclusive, int sequentialThreshold, List<Double> inputA, List<Double> inputB, List<Double> output) {
            this.startInclusive = startInclusive;
            this.endExclusive = endExclusive;
            this.sequentialThreshold = sequentialThreshold;
            this.inputA = inputA;
            this.inputB = inputB;
            this.output = output;
        }

        @Override
        protected void compute() {
            if (endExclusive - startInclusive <= sequentialThreshold) {
                for (int i = startInclusive; i < endExclusive; i++) {
                    output.set(i, inputA.get(i) + inputB.get(i));
                }
            } else {
                int midIndex = (startInclusive + sequentialThreshold) ;
                RecursiveMatrixSum left = new RecursiveMatrixSum(startInclusive, midIndex, sequentialThreshold, inputA, inputB, output);
                RecursiveMatrixSum right = new RecursiveMatrixSum(midIndex, endExclusive, sequentialThreshold, inputA, inputB, output);
                left.fork();
                right.compute();
                left.join();
            }
        }

        public List<Double> getOutput() {
            return output;
        }
    }

    private static class MatrixMultiplicationTask implements Runnable {

        private final int startRowInclusive;
        private final int endRowExclusive;
        private final int startColumnInclusive;
        private final int endColumnExclusive;
        private final Matrix inputA;
        private final Matrix inputB;
        private final Matrix output;

        private MatrixMultiplicationTask(int startRowInclusive, int endRowExclusive, int startColumnInclusive, int endColumnExclusive, Matrix inputA, Matrix inputB, Matrix output) {
            this.startRowInclusive = startRowInclusive;
            this.endRowExclusive = endRowExclusive;
            this.startColumnInclusive = startColumnInclusive;
            this.endColumnExclusive = endColumnExclusive;
            this.inputA = inputA;
            this.inputB = inputB;
            this.output = output;
        }

        @Override
        public void run() {
            for (int i = startRowInclusive; i < endRowExclusive; i++) {
                for (int j = startColumnInclusive; j < endColumnExclusive; j++) {
                    for (int k = 0; k < inputA.columns; k++) {
                        output.setElement(i, j, output.getElement(i, j) + inputA.getElement(i , k) * inputB.getElement(k, j));
                    }
                }
            }
        }
    }
}
