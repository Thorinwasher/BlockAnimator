package dev.thorinwasher.blockanimator.api.blockanimations.pathcompletion;

import java.util.List;

public interface PathCompletionSupplier {

    List<Double> compile(double pathLength);
}
