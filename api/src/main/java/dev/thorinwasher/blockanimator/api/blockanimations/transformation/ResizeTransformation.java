package dev.thorinwasher.blockanimator.api.blockanimations.transformation;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.function.Function;

public class ResizeTransformation implements BlockTransformation {

    private final float from;
    private final float to;
    private final Function<Double, Double> pathCompletionSupplier;

    public ResizeTransformation(float from, float to, Function<Double, Double> pathCompletionSupplier) {
        this.from = from;
        this.to = to;
        this.pathCompletionSupplier = pathCompletionSupplier;
    }

    @Override
    public @NotNull Matrix4f getTransform(double pathCompletion) {
        double completion = pathCompletionSupplier.apply(pathCompletion);
        float scale = (float) (from * (1 - completion) + to * pathCompletion);
        return new Matrix4f().scale(scale);
    }
}
