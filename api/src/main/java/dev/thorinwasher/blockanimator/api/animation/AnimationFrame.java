package dev.thorinwasher.blockanimator.api.animation;

import dev.thorinwasher.blockanimator.api.blockanimations.BlockMoveType;
import dev.thorinwasher.blockanimator.api.container.TwoTuple;
import dev.thorinwasher.blockanimator.api.supplier.ImmutableVector3i;
import org.joml.Vector3d;

import java.util.Map;

public record AnimationFrame(Map<ImmutableVector3i, TwoTuple<Vector3d, BlockMoveType>> currentToDestination) {
}
