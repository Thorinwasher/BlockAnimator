package dev.thorinwasher.blockanimator.blockanimations;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public interface BlockMoveAnimation {

    CompiledBlockMoveAnimation compile(Vector3D to);
}
