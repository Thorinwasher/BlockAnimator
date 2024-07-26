package dev.thorinwasher.blockanimator.api.animation;

import dev.thorinwasher.blockanimator.api.blockanimations.BlockMoveType;
import dev.thorinwasher.blockanimator.api.container.TwoTuple;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.Map;

public record AnimationFrame(Map<Vector3D, TwoTuple<Vector3D, BlockMoveType>> currentToDestination) {
}
