package neuralnetworks.activationfunctions;

import algebra.Matrix;

import java.util.HashMap;
import java.util.Map;

public class HyperbolicTangentActivationFunction implements ActivationFunction {
    private static final Map<Matrix, Matrix> cache = new HashMap<>();

    @Override
    public Matrix apply(Matrix x) {
        if (!cache.containsKey(x)) {
            Matrix out = Matrix.zeroes(x.getRows(), x.getColumns());
            for (int i = 0; i < x.getSize(); i++) {
                double ePlus = Math.exp(x.getValues().get(i));
                double eMinus = Math.exp(-x.getValues().get(i));
                out.getValues().set(i, (ePlus - eMinus) / (ePlus + eMinus));
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
            out.getValues().set(i, (1 - applied * applied));
        }
        return out;
    }
}
