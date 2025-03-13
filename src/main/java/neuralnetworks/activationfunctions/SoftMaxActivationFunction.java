package neuralnetworks.activationfunctions;

import algebra.Matrix;

import java.util.HashMap;
import java.util.Map;

public class SoftMaxActivationFunction implements ActivationFunction {
    private static final Map<Matrix, Matrix> cache = new HashMap<>();

    @Override
    public Matrix apply(Matrix x) {
        if (!cache.containsKey(x)) {
            Matrix t = Matrix.zeroes(x.getRows(), x.getColumns());
            for (int i = 0; i < x.getSize(); i++) {
                t.getValues().set(i, Math.exp(x.getValues().get(i)));
            }
            Matrix sums = t.sumCols();
            for (int i = 0; i < x.getRows(); i++) {
                for (int j = 0; j < x.getColumns(); j++) {
                    t.setElement(i, j, t.getElement(i, j) / sums.getElement(0, j));
                }
            }
            cache.put(x, t);
        }
        return cache.get(x);
    }

    @Override
    public Matrix getDerivative(Matrix x) {
        Matrix applied = this.apply(x);
        Matrix out = Matrix.zeroes(x.getRows(), x.getColumns());
        int col = 0;
        for (Matrix column: applied.asColumnList()) {
            Matrix jc = this.computeJacobian(column);
            jc = jc.sumCols();
            for (int i = 0; i < jc.getColumns(); i++) {
                out.setElement(i, col, jc.getElement(0, i));
            }
            col++;
        }
        return out;
    }

    private Matrix computeJacobian(Matrix column) {
        Matrix jc = Matrix.zeroes(column.getRows(), column.getRows());
        for (int i = 0; i < jc.getRows(); i++) {
            for (int j = 0; j < jc.getColumns(); j++) {
                if (i == j) {
                    double el = column.getElement(i, 0);
                    jc.setElement(i, j, el * (1 - el));
                } else {
                    jc.setElement(i, j, -column.getElement(i, 0) * column.getElement(j, 0));
                }
            }
        }
        return jc;
    }
}
