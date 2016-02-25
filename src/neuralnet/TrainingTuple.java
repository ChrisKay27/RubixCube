package neuralnet;

import java.util.List;

/**
 * Created by chris_000 on 2/9/2016.
 */
public class TrainingTuple {
    private List<Double> inputs;
    private List<Double> expectedOutputs;

    public TrainingTuple(List<Double> inputs, List<Double> expectedOutputs) {
        this.inputs = inputs;
        this.expectedOutputs = expectedOutputs;
    }

    public List<Double> getInputs() {
        return inputs;
    }

    public List<Double> getExpectedOutputs() {
        return expectedOutputs;
    }
}
