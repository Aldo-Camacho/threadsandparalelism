package neuralnetworks.costfunctions;

import algebra.Matrix;

public class BinaryLogCostFunction implements CostFunction {
    @Override
    public double computeCost(Matrix actual, Matrix expected) {
        double m = actual.getColumns();
        Matrix yPart = expected.elementWiseMult(actual.elementWiseLog());
        Matrix yMinusPart = (expected.multiply(-1).add(1)).elementWiseMult((actual.multiply(-1).add(1)).elementWiseLog());
        Matrix cost = yPart.add(yMinusPart);
        return - cost.sum() / m;
    }

    @Override
    public Matrix computeDerivative(Matrix actual, Matrix expected) {
        return (expected.multiply(-1).add(1).elementWiseDiv(actual.multiply(-1).add(1))).add(expected.elementWiseDiv(actual).multiply(-1));
    }
}
