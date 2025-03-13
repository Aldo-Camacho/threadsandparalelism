package neuralnetworks.costfunctions;

import algebra.Matrix;

public class MultyClassLogCostFunction implements CostFunction {

    @Override
    public double computeCost(Matrix actual, Matrix expected) {
        double m = actual.getColumns();
        double cost = 0;
        for (int i = 0; i < actual.getRows(); i++) {
            for (int j = 0; j < m; j++) {
                cost += expected.getElement(i, j) * Math.log(actual.getElement(i, j));
            }
        }
        return - cost / m;
    }

    @Override
    public Matrix computeDerivative(Matrix actual, Matrix expected) {
        return actual.add(expected.multiply(-1));
    }
}
