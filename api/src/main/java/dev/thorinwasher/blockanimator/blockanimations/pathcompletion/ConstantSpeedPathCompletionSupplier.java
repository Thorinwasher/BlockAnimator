package dev.thorinwasher.blockanimator.blockanimations.pathcompletion;

import java.util.ArrayList;
import java.util.List;

public class ConstantSpeedPathCompletionSupplier implements PathCompletionSupplier {

    private final double speed;

    public ConstantSpeedPathCompletionSupplier(double speed) {
        this.speed = speed;
    }

    @Override
    public List<Double> compile(double pathLength) {
        int steps = (int) Math.ceil(pathLength / speed);
        List<Double> output = new ArrayList<>(steps + 1);
        for (int step = 0; step < steps; step++) {
            output.add(step * speed / pathLength);
        }
        return output;
    }
}
