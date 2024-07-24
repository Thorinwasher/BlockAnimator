package dev.thorinwasher.blockanimator.animation;

import dev.thorinwasher.blockanimator.blockanimations.BlockMoveType;
import dev.thorinwasher.blockanimator.container.TwoTuple;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.Map;

public record AnimationFrame(Map<Vector3D, TwoTuple<Vector3D, BlockMoveType>> currentToDestination) {
}
