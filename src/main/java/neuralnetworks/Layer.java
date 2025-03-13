package neuralnetworks;

import algebra.Matrix;
import neuralnetworks.activationfunctions.ActivationFunction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Layer {

    private Matrix weights;
    private Matrix biases;
    private ActivationFunction activationFunction;

    public Layer(int inputSize, int neuronNumber, double scale, ActivationFunction activationFunction) {
        weights = Matrix.getRandom(neuronNumber, inputSize, scale);
        biases = Matrix.zeroes(neuronNumber, 1);
        this.activationFunction = activationFunction;
    }

    public Matrix predict(Matrix inputs) {
        Matrix z = weights.dot(inputs).add(biases);
        return activationFunction.apply(z);
    }

    public void forwardPropagate(int layerNumber, Matrix inputs, Map<String, Matrix> cache) {
        Matrix z = weights.dot(inputs).add(biases.copyColumn(inputs.getColumns()));
        cache.put("Z" + layerNumber, z);
        Matrix a = activationFunction.apply(z);
        cache.put("A" + layerNumber, a);
    }

    public void backPropagate(int layerNumber, Matrix dA, Map<String, Matrix> cache) {
        double m = dA.getColumns();
        Matrix dZ = dA.elementWiseMult(activationFunction.getDerivative(cache.get("Z" + layerNumber)));
        Matrix dW = dZ.dot(cache.get("A" + (layerNumber - 1)).transpose()).multiply(1 / m);
        Matrix db = dZ.sumRows().multiply(1 / m);
        Matrix dAPrev = weights.transpose().dot(dZ);
        cache.put("dZ" + layerNumber, dZ);
        cache.put("db" + layerNumber, db);
        cache.put("dW" + layerNumber, dW);
        cache.put("dA" + (layerNumber - 1), dAPrev);
    }

    public Matrix getWeights() {
        return weights;
    }

    public void setWeights(Matrix weights) {
        this.weights = weights;
    }

    public ActivationFunction getActivationFunction() {
        return activationFunction;
    }

    public void setActivationFunction(ActivationFunction activationFunction) {
        this.activationFunction = activationFunction;
    }

    public Matrix getBiases() {
        return biases;
    }

    public void setBiases(Matrix biases) {
        this.biases = biases;
    }
}
