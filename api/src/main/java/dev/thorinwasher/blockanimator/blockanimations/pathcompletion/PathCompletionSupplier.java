package dev.thorinwasher.blockanimator.blockanimations.pathcompletion;

import java.util.List;

public interface PathCompletionSupplier {

    List<Double> compile(double pathLength);
}
