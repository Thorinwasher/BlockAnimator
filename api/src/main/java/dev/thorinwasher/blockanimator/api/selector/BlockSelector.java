package dev.thorinwasher.blockanimator.api.selector;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.List;

public interface BlockSelector {

    CompiledBlockSelector compile(List<Vector3D> blocks);
}
