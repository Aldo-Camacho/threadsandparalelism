package neuralnetworks.activationfunctions;

import algebra.Matrix;

import java.util.HashMap;
import java.util.Map;

public class SigmoidActivationFunction implements ActivationFunction {
    private static final Map<Matrix, Matrix> cache = new HashMap<>();

    @Override
    public Matrix apply(Matrix x) {
        if (! cache.containsKey(x)) {
            Matrix out = Matrix.zeroes(x.getRows(), x.getColumns());
            for (int i = 0; i < x.getSize(); i++) {
                out.getValues().set(i, 1 / (1 + Math.exp(-x.getValues().get(i))));
            }
            cache.put(x, out);
        }
        return cache.get(x);
    }

    @Override
    public Matrix getDerivative(Matrix x) {
        Matrix out = apply(x);
        for (int i = 0; i < out.getSize(); i++) {
            double applied = out.getValues().get(i);
            out.getValues().set(i, applied * (1 - applied));
        }
        return out;
    }
}
