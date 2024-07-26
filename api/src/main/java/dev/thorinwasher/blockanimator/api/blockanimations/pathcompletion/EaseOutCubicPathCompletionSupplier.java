package dev.thorinwasher.blockanimator.api.blockanimations.pathcompletion;

import java.util.ArrayList;
import java.util.List;

public class EaseOutCubicPathCompletionSupplier implements PathCompletionSupplier {

    private final double averageSpeed;

    public EaseOutCubicPathCompletionSupplier(double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    @Override
    public List<Double> compile(double pathLength) {
        int steps = (int) Math.ceil(pathLength / averageSpeed);
        List<Double> output = new ArrayList<>(steps);
        for (int step = 0; step < steps; step++) {
            double pathCompletion = (double) step / steps;
            output.add(1 - Math.pow(1 - pathCompletion, 3));
        }
        output.add(1D);
        return output;
    }

}
