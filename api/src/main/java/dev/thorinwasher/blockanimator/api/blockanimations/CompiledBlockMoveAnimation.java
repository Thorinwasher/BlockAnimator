package dev.thorinwasher.blockanimator.api.blockanimations;

import dev.thorinwasher.blockanimator.api.container.TwoTuple;
import org.joml.Vector3d;

import java.util.List;

public record CompiledBlockMoveAnimation(List<TwoTuple<Vector3d, BlockMoveType>> frames) {

}
