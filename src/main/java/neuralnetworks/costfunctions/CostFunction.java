package neuralnetworks.costfunctions;

import algebra.Matrix;

public interface CostFunction {
    double computeCost(Matrix actual, Matrix expected);
    Matrix computeDerivative(Matrix actual, Matrix expected);
}
