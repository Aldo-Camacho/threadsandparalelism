package neuralnetworks.activationfunctions;

import algebra.Matrix;

public class ReLuActivationFunction implements ActivationFunction{
    @Override
    public Matrix apply(Matrix x) {
        Matrix out = Matrix.zeroes(x.getRows(), x.getColumns());
        for (int i = 0; i < x.getSize(); i++) {
            out.getValues().set(i, Math.max(x.getValues().get(i), 0.0));
        }
        return out;
    }

    @Override
    public Matrix getDerivative(Matrix x) {
        Matrix out = Matrix.zeroes(x.getRows(), x.getColumns());
        for (int i = 0; i < x.getSize(); i++) {
            double current = x.getValues().get(i);
            out.getValues().set(i, current > 0.0 ? 1.0 : 0.0);
        }
        return out;
    }
}
