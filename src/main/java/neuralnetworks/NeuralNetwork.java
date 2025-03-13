package neuralnetworks;

import algebra.Matrix;
import neuralnetworks.activationfunctions.ActivationFunction;
import neuralnetworks.costfunctions.CostFunction;
import neuralnetworks.costfunctions.BinaryLogCostFunction;

import java.util.*;

public class NeuralNetwork {

    private final Map<String, Matrix> cache = new HashMap<>();
    private final List<Layer> layers = new ArrayList<>();
    private final CostFunction costFunction;

    public NeuralNetwork(List<Integer> layerSizes, List<ActivationFunction> activationFunctions, CostFunction costFunction) {
        assert layerSizes.size() == activationFunctions.size() + 1;
        for (int i = 1; i < layerSizes.size(); i++) {
            Layer layer = new Layer(layerSizes.get(i - 1), layerSizes.get(i), 0.1, activationFunctions.get(i - 1));
            layers.add(layer);
        }
        this.costFunction = costFunction;
    }

    public void forwardPropagate(Matrix input) {
        cache.put("A0", input);
        Matrix in = input;
        int layNum = 1;
        for (Layer layer : layers) {
            layer.forwardPropagate(layNum, in, cache);
            in = cache.get("A" + layNum);
            layNum++;
        }
    }

    public void backPropagate(Matrix expected) {
        List<Layer> reversedLayers = new ArrayList<>(layers);
        Collections.reverse(reversedLayers);
        int l = layers.size();
        Matrix AL = cache.get("A" + l);
        Matrix dAL = costFunction.computeDerivative(AL, expected);
        cache.put("dA" + l, dAL);
        for (Layer layer: reversedLayers) {
            layer.backPropagate(l, dAL, cache);
            dAL = cache.get("dA" + (l - 1));
            l--;
        }
    }

    public void updateParams(double alpha) {
        for (int i = 0; i < layers.size(); i++) {
            Layer layer = layers.get(i);
            layer.setWeights(layer.getWeights().add(cache.get("dW" + (i + 1)).multiply(-alpha)));
            layer.setBiases(layer.getBiases().add(cache.get("db" + (i + 1)).multiply(-alpha)));
        }
    }

    public List<Double> train(Matrix inputs, Matrix outputs, int epochs, double alpha) {
        List<Double> cost = new ArrayList<>();
        for (int i = 0; i < epochs; i++) {
            this.forwardPropagate(inputs);
            cost.add(costFunction.computeCost(cache.get("A" + layers.size()), outputs));
            this.backPropagate(outputs);
            this.updateParams(alpha);
            if (i % 10 == 0) {
                System.out.println("Cost after " + i + " iterations: " + cost.get(i));
            }
        }
        return cost;
    }

    public Matrix predict(Matrix input) {
        Matrix in = input;
        for (Layer layer : layers) {
            in = layer.predict(in);
        }
        return in;
    }
}
