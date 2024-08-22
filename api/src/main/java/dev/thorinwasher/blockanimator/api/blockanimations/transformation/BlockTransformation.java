package dev.thorinwasher.blockanimator.api.blockanimations.transformation;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public interface BlockTransformation {

    @NotNull Matrix4f getTransform(double pathCompletion);
}
