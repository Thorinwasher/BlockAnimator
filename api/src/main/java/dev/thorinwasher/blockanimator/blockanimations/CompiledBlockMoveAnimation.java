package dev.thorinwasher.blockanimator.blockanimations;

import dev.thorinwasher.blockanimator.container.TwoTuple;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.List;

public record CompiledBlockMoveAnimation(List<TwoTuple<Vector3D, BlockMoveType>> frames) {

}
