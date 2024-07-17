package dev.thorinwasher.blockanimator.blockanimations.pathcompletion;

import java.util.ArrayList;
import java.util.List;

public class FixedStepsPathCompletionSupplier implements PathCompletionSupplier {

    private final int steps;

    public FixedStepsPathCompletionSupplier(int steps) {
        this.steps = steps;
    }

    @Override
    public List<Double> compile(double pathLength) {
        List<Double> steps = new ArrayList<>(this.steps);
        double stepLength = pathLength / this.steps;
        for (int step = 0; step < this.steps; step++) {
            steps.add(stepLength * step / pathLength);
        }
        return steps;
    }
}
