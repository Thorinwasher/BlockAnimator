package dev.thorinwasher.blockanimator.supplier;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.List;
import java.util.Map;

public interface BlockSupplier<B> {
    B getBlock(Vector3D targetPosition);

    List<Vector3D> getPositions();
}
