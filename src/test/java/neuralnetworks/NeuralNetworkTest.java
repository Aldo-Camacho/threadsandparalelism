package neuralnetworks;

import algebra.Matrix;
import neuralnetworks.activationfunctions.ActivationFunction;
import neuralnetworks.activationfunctions.HyperbolicTangentActivationFunction;
import neuralnetworks.activationfunctions.SigmoidActivationFunction;
import neuralnetworks.activationfunctions.SoftMaxActivationFunction;
import neuralnetworks.costfunctions.MultyClassLogCostFunction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

class NeuralNetworkTest {

    static class InputHolder {
        List<Double> values = new ArrayList<>();
    }

    static List<InputHolder> inputCache = new ArrayList<>();
    static List<InputHolder> outputCache = new ArrayList<>();
    static NeuralNetwork network;
    static Matrix inputs;
    static Matrix outputs;
    static Matrix trainInputs;
    static Matrix trainOutputs;
    static Matrix testInputs;
    static Matrix testOutputs;

    @BeforeAll
    static void init() throws FileNotFoundException {
        readFile();
        buildInputAndOutput();
        buildTrainAndTestSets();
        buildNetwork();
        train();
    }

    static void readFile() throws FileNotFoundException {
        File file = new File("src/test/resources/iris/iris.data");
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line == null || line.equals("")) {
                continue;
            }
            String[] record = line.split(",");
            int l = record.length;
            InputHolder holder = new InputHolder();
            switch (record[l - 1]) {
                case "Iris-setosa":
                    holder.values.add(1.0);
                    holder.values.add(0.0);
                    holder.values.add(0.0);
                    break;
                case "Iris-versicolor":
                    holder.values.add(0.0);
                    holder.values.add(1.0);
                    holder.values.add(0.0);
                    break;
                case "Iris-virginica":
                    holder.values.add(0.0);
                    holder.values.add(0.0);
                    holder.values.add(1.0);
                    break;
                default:
                    break;
            }
            outputCache.add(holder);
            holder = new InputHolder();
            for (int i = 0; i < l - 1; i++) {
                holder.values.add(Double.valueOf(record[i]));
            }
            inputCache.add(holder);
        }
    }

    static void buildInputAndOutput() {
        inputs = Matrix.zeroes(inputCache.get(0).values.size(), inputCache.size());
        outputs = Matrix.zeroes(3 ,outputCache.size());
        for (int i = 0; i < inputs.getRows(); i++) {
            for (int j = 0; j < inputs.getColumns(); j++) {
                inputs.setElement(i, j, inputCache.get(j).values.get(i));
            }
        }
        for (int i = 0; i < outputs.getRows(); i++) {
            for (int j = 0; j < outputs.getColumns(); j++) {
                outputs.setElement(i, j, outputCache.get(j).values.get(i));
            }
        }
        double inputMean = inputs.getMean();
        double standardDev = inputs.getStdDev(inputMean);
        inputs = inputs.add(-inputMean).multiply(1 / standardDev);
    }

    static void buildTrainAndTestSets() {
        Set<Integer> trainIndexes = new HashSet<>();
        Set<Integer> testIndexes = new HashSet<>();
        Random random = new Random();
        int trainSize = (int) Math.round(0.6 * inputs.getColumns());
        int testSize = inputs.getColumns() - trainSize;
        while (trainIndexes.size() != trainSize) {
            trainIndexes.add(random.nextInt(inputs.getColumns()));
        }
        while (testIndexes.size() != testSize) {
            int rand = random.nextInt(inputs.getColumns());
            if (!trainIndexes.contains(rand)) {
                testIndexes.add(rand);
            }
        }
        trainInputs = Matrix.zeroes(inputs.getRows(), trainSize);
        testInputs = Matrix.zeroes(inputs.getRows(), testSize);
        trainOutputs = Matrix.zeroes(outputs.getRows(), trainSize);
        testOutputs = Matrix.zeroes(outputs.getRows(), testSize);
        int column = 0;
        for (Integer index: trainIndexes) {
            for (int i = 0; i < inputs.getRows(); i++) {
                trainInputs.setElement(i, column, inputs.getElement(i, index));
            }
            for (int i = 0; i < outputs.getRows(); i++) {
                trainOutputs.setElement(i, column, outputs.getElement(i, index));
            }
            column++;
        }
        column = 0;
        for (Integer index: testIndexes) {
            for (int i = 0; i < inputs.getRows(); i++) {
                testInputs.setElement(i, column, inputs.getElement(i, index));
            }
            for (int i = 0; i < outputs.getRows(); i++) {
                testOutputs.setElement(i, column, outputs.getElement(i, index));
            }
            column++;
        }
    }

    static void buildNetwork() {
        List<Integer> networkSizes = new ArrayList<>();
        List<ActivationFunction> activationFunctions = new ArrayList<>();
        networkSizes.add(inputs.getRows());
        networkSizes.add(8);
        networkSizes.add(8);
        networkSizes.add(3);
        for (int i = 1; i < networkSizes.size() - 1; i++) {
            activationFunctions.add(new HyperbolicTangentActivationFunction());
        }
        activationFunctions.add(new SigmoidActivationFunction());
        network = new NeuralNetwork(networkSizes, activationFunctions, new MultyClassLogCostFunction());
    }

    static void train() {
        network.train(trainInputs, trainOutputs, 150, 0.9);
    }

    @Test
    @Order(2)
    void predict() {
        List<Matrix> outputRows = testOutputs.asColumnList();
        int iter = 0;
        int errors = 0;
        for (Matrix row: testInputs.asColumnList()) {
            Matrix output = network.predict(row).applyThreshold(0.5);
            Matrix expected = outputRows.get(iter);
            for (int i = 0; i < output.getRows(); i++) {
                if (expected.getElement(i, 0) - output.getElement(i, 0) != 0) {
                    errors++;
                    break;
                }
            }
            iter++;
        }
        double acc = (double) (testInputs.getColumns() - errors) / testInputs.getColumns() * 100;
        System.out.println("Accuracy: " + acc);
    }
}