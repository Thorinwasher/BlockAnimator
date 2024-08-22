package dev.thorinwasher.blockanimator.api.animation;

import dev.thorinwasher.blockanimator.api.blockanimations.BlockMoveType;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3d;

public record BlockAnimationFrame(Vector3d position, BlockMoveType moveType, @Nullable Matrix4f transform) {
}
