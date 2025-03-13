package neuralnetworks.activationfunctions;

import algebra.Matrix;

public interface ActivationFunction {
    Matrix apply(Matrix x);
    Matrix getDerivative(Matrix x);
}
